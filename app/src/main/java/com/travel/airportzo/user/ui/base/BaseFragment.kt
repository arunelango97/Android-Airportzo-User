package com.travel.airportzo.user.ui.base

import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.amazonaws.services.cognitoidentity.model.GetIdRequest
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.travel.airportzo.user.R
import com.travel.airportzo.user.network.ApiClient
import com.travel.airportzo.user.network.NetworkHelper
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.ui.fragments.PackageServiceFragment
import com.travel.airportzo.user.viewmodel.MainRepository
import com.travel.airportzo.user.viewmodel.MainViewModel
import com.travel.airportzo.user.viewmodel.ViewModelFactory

abstract  class BaseFragment : Fragment() {
    private lateinit var networkHelper: NetworkHelper
//    lateinit var viewModel: MainViewModel
    var viewModel : MainViewModel? = null
    private var dialog: AlertDialog? = null
    private var isRetry = MutableLiveData<Boolean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, ViewModelFactory(MainRepository(ApiClient.apis), Application()))[MainViewModel::class.java]

//        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

    }
    /*val onDataChangedListener = object : PackageServiceFragment.OnDataChangedListener{
        override fun onDataChanged(data: String) {

        }

    }*/

    fun alertToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    val loginView by lazy {
        context?.let {
            BottomSheetDialog(it, R.style.DialogStyleBottomSheet).apply {
                setContentView(layoutInflater.inflate(R.layout.bottomsheet_login, null))
                val sendOTPBtn = findViewById<MaterialButton>(R.id.SendOtp)
                val logo = findViewById<AppCompatImageView>(R.id.logo)
                if (logo != null) {
                    Glide.with(this@BaseFragment)
                        .load(activity?.let {it1 -> SavedSharedPreference.getCustomColor(it1).header_logo})
                        .into(logo)
                }
                val colorString = activity?.let { SavedSharedPreference.getCustomColor(it).brand_colour }
                val brandColor = Color.parseColor(colorString)
                sendOTPBtn?.setBackgroundColor(brandColor)



                setCancelable(true)
            }
        }
    }

    val otpView by lazy {
        context?.let {
            BottomSheetDialog(it, R.style.DialogStyleBottomSheet).apply {
                setContentView(layoutInflater.inflate(R.layout.bottomsheet_otp, null))
                val logo = findViewById<AppCompatImageView>(R.id.logo)
                val verifyOTPBtn = findViewById<MaterialButton>(R.id.verifyOtp)
                if (logo != null){
                    Glide.with(this@BaseFragment)
                        .load(activity?.let { it1 -> SavedSharedPreference.getCustomColor(it1).header_logo })
                        .into(logo)
                }
                verifyOTPBtn?.setBackgroundColor(Color.parseColor(activity?.let { it1 ->
                    SavedSharedPreference.getCustomColor(
                        it1
                    ).brand_colour
                }))

                setCancelable(true)
            }
        }
    }

    fun isNetworkConnected(context: Context) : Boolean {
        networkHelper = NetworkHelper(context)
        return networkHelper.isNetworkConnected()
    }

    open val loader = Observer<Boolean> {
        if (it) {
            val customLayout = layoutInflater.inflate(R.layout.loader, null)
            dialog = AlertDialog.Builder(requireContext()).setView(customLayout).setCancelable(false).show()
            dialog!!.window!!.setLayout(250, ViewGroup.LayoutParams.WRAP_CONTENT)
        } else {
           try {
               dialog!!.dismiss()
           }catch (e: Exception){e.printStackTrace()}
        }
    }

    fun errors(title: String, message: String) {
        if (message == getString(R.string.nointernet)) {
            Snackbar.make(requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)
                .setBackgroundTint(ContextCompat.getColor(requireActivity(), R.color.batchColor))
                .setTextColor(ContextCompat.getColor(requireActivity(), R.color.white)).setAction(getString(
                    R.string.ok)) {
                    isRetry.value = true
                }.show()
        } else {
            val alert = AlertDialog.Builder(requireActivity()).setMessage(message).setCancelable(false)
                .setPositiveButton("Proceed", DialogInterface.OnClickListener {
                        dialog, id -> dialog.dismiss()
                }.apply {
                    requireActivity().titleColor = ContextCompat.getColor(requireActivity(), R.color.black)
                }).create()
            alert.setTitle(title)
            alert.show()
        }
    }

    fun errorsAlert(title: String, message: String) {
        if (message == getString(R.string.nointernet)) {
            Snackbar.make(requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)
                .setBackgroundTint(ContextCompat.getColor(requireActivity(), R.color.batchColor))
                .setTextColor(ContextCompat.getColor(requireActivity(), R.color.white)).setAction(getString(
                    R.string.ok)) {
                    isRetry.value = true
                }.show()
        } else {
            val alert = AlertDialog.Builder(requireActivity()).setMessage(message).setCancelable(false)
                .setPositiveButton("OK", DialogInterface.OnClickListener {
                        dialog, id -> dialog.dismiss()
                }.apply {
                    requireActivity().titleColor = ContextCompat.getColor(requireActivity(), R.color.black)
                }).create()
            alert.setTitle(title)
            alert.show()
        }
    }

    fun launchActivity(
        javaClass: Class<out AppCompatActivity>,
        bundle: Bundle? = null,
        isClearPreviousTask: Boolean = false
    ) {
        Intent(requireContext(), javaClass).apply {
            if (bundle != null)
                putExtras(bundle)

            if (isClearPreviousTask)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(this)
        }
    }



}