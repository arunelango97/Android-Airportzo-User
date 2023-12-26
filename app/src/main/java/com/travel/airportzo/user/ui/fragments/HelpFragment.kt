package com.travel.airportzo.user.ui.fragments

import android.content.ActivityNotFoundException
import android.content.ClipDescription
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.gson.JsonObject
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.HelpFragmentBinding
import com.travel.airportzo.user.model.ContactData
import com.travel.airportzo.user.model.ContactResponse
import com.travel.airportzo.user.model.FaqData
import com.travel.airportzo.user.network.ApiResult
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.ui.adapter.FaqAdapter
import com.travel.airportzo.user.ui.base.BaseFragment
import com.travel.airportzo.user.utils.setOnDebounceListener
import kotlinx.coroutines.launch


/**
 * Help fragment
 *
 * @constructor Create empty Help fragment
 */
class HelpFragment : BaseFragment() {

    private val helpFragment by lazy { HelpFragmentBinding.inflate(layoutInflater) }
    private val faqData: ArrayList<FaqData> by lazy { arrayListOf() }
    private val faqAdapter by lazy { FaqAdapter(faqData) }


    private var mail_us: String = ""
    private var mobile_number: String = ""
    private var whatsapp_number: String = ""

    private val contactBottomSheet by lazy {
        context?.let {
            BottomSheetDialog(it, R.style.DialogStyleBottomSheet).apply {
                setContentView(layoutInflater.inflate(R.layout.bottomsheet_writetous, null))
                /** Brand Color*/
                val submitBtn = findViewById<MaterialButton>(R.id.submit)
                submitBtn?.setBackgroundColor(Color.parseColor(activity?.let { it1 ->
                    SavedSharedPreference.getCustomColor(
                        it1
                    ).brand_colour
                }))

                setCancelable(true)
                setOnShowListener {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** brand color */

        helpFragment.rootLayout.setBackgroundColor(Color.parseColor(activity?.let {
            SavedSharedPreference.getCustomColor(
                it
            ).brand_colour
        }))

        helpFragment.writeToUs.setBackgroundColor(Color.parseColor(activity?.let { it1 ->
            SavedSharedPreference.getCustomColor(
                it1
            ).brand_colour
        }))

        helpFragment.helpBack.setOnDebounceListener {
            onBackPressed()
        }

        helpFragment.writeToUs.setOnDebounceListener {
            showContactSheet()
        }

        helpFragment.emailUs.setOnDebounceListener {
            openEmail(emailAddress = mail_us)
        }

        helpFragment.callUs.setOnDebounceListener {
            openCall(mobileNumber = mobile_number)
        }

        helpFragment.whatsApp.setOnDebounceListener {
            openWhatsapp(whatsAppNumber = whatsapp_number)
        }

lifecycleScope.launch {
    viewModel?.readContact()?.observe(requireActivity(), readContactObserver)
}

        lifecycleScope.launch {
            viewModel?.faqQuestion()?.observe(requireActivity(),faqQuestion)
        }

    }

    private val faqQuestion = Observer<ApiResult<List<FaqData>>>{
        when(it){
            is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {
                faqData.clear()
                faqData.addAll(it.data)

                helpFragment.faqList.adapter=faqAdapter
            }
            is ApiResult.Error -> {
                errors("Error", it.message)
            }

        }
    }


    private val readContactObserver= Observer<ApiResult<ContactResponse>>{
        when(it){
//            is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {
                if (it.data.statusCode == 200){
                    helpFragment.textView14.text = it.data.data[0].corporateAddress
                    mail_us = it.data.data[0].mailUs
                    whatsapp_number = it.data.data[0].whatsappNumber
                    mobile_number = it.data.data[0].mobileNumber
                }
            }
            is ApiResult.Error -> {
                errors("Error", it.message)
            }
            else -> {}
        }
    }

    private fun openEmail(emailAddress: String){
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = ClipDescription.MIMETYPE_TEXT_PLAIN
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
        startActivity(Intent.createChooser(intent,"Send Email"))

       /* val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:$emailAddress")
        startActivity(Intent.createChooser(intent, "Email via..."))*/
    }

    private fun openCall(mobileNumber: String){
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:$mobileNumber")
        startActivity(callIntent)
    }

    private fun openWhatsapp(whatsAppNumber: String){
        val url = "https://api.whatsapp.com/send?phone=$whatsAppNumber"
        try {
            val pm = requireContext().packageManager
//            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            intent.setPackage("com.whatsapp")
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            alertToast(requireContext(), "Whatsapp app not installed in your phone")
            e.printStackTrace()
        }

    }

    private fun showContactSheet() {
        contactBottomSheet?.show()
        contactBottomSheet?.findViewById<MaterialButton>(R.id.submit)?.setOnDebounceListener {
            val name = contactBottomSheet?.findViewById<EditText>(R.id.name)?.text.toString()
            val emailAddress = contactBottomSheet?.findViewById<EditText>(R.id.emailAddress)?.text.toString()
            val subject = contactBottomSheet?.findViewById<EditText>(R.id.subject)?.text.toString()
            val message = contactBottomSheet?.findViewById<EditText>(R.id.message)?.text.toString()

            if (TextUtils.isEmpty(name)){
                alertToast(requireContext(), "Enter full name")
                return@setOnDebounceListener
            }

            if (TextUtils.isEmpty(emailAddress)){
                alertToast(requireContext(), "Enter email address")
                return@setOnDebounceListener
            }

            if (TextUtils.isEmpty(subject)){
                alertToast(requireContext(), "Enter Subject")
                return@setOnDebounceListener
            }

            if (TextUtils.isEmpty(message)){
                alertToast(requireContext(), "Enter message")
                return@setOnDebounceListener
            }

            val jsonObject = JsonObject().apply {
                addProperty("user_name", name)
                addProperty("user_email", emailAddress)
                addProperty("user_subject", subject)
                addProperty("user_feedback", message)
            }
            Log.d("send_contact_detail", "contactDetail: $jsonObject")
            lifecycleScope.launch {
                viewModel?.contactDetail(jsonObject)?.observe(requireActivity(), contactObserver)
            }
        }
    }


    private val contactObserver= Observer<ApiResult<ContactData>>{
        when(it){
            is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {
                errors("", it.data.message)
                if (it.data.statusCode == 200){
                    contactBottomSheet?.dismiss()
                }
            }
            is ApiResult.Error -> {
                errors("Error", it.message)
            }
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return helpFragment.root
    }

    private fun onBackPressed() {
        Navigation.findNavController(requireView()).popBackStack()
    }
}