package com.travel.airportzo.user.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonObject
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.CheckoutFragmentBinding
import com.travel.airportzo.user.model.AwsConstants
import com.travel.airportzo.user.model.CheckoutData
import com.travel.airportzo.user.model.PassengerCreateData
import com.travel.airportzo.user.model.PassengerCreateGst
import com.travel.airportzo.user.network.ApiResult
import com.travel.airportzo.user.network.NoInternetActivity
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.ui.activity.PaymentActivity
import com.travel.airportzo.user.ui.adapter.*
import com.travel.airportzo.user.ui.base.BaseFragment
import com.travel.airportzo.user.utils.AWSFileUploader
import com.travel.airportzo.user.utils.FileUtils
import com.travel.airportzo.user.utils.setOnDebounceListener
import com.travel.airportzo.user.webView.WebView
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class CheckoutFragment : BaseFragment(), AWSFileUploader.AWSFileUploadListener {

    private val checkoutFragment by lazy { CheckoutFragmentBinding.inflate(layoutInflater) }

    private val checkoutData: ArrayList<CheckoutData> by lazy { arrayListOf() }
    private val passengerCreateGst: ArrayList<PassengerCreateGst> by lazy { arrayListOf() }
    private val checkOutSavedPassenger: ArrayList<CheckoutData> by lazy { arrayListOf() }
    private val passengerCreateData: ArrayList<PassengerCreateData> by lazy { arrayListOf() }
    private val passengerCreateDataOther: ArrayList<PassengerCreateData> by lazy { arrayListOf() }
    private val passengerCreateDataService: ArrayList<PassengerCreateData> by lazy { arrayListOf() }
    private val savedPassenger: ArrayList<PassengerCreateData> by lazy { arrayListOf() }
    private val checkoutGstSaved: ArrayList<CheckoutData.Gst> by lazy { arrayListOf() }
    private val adapterSaveGst: ArrayList<String> by lazy { arrayListOf() }
    private val adapterSavePassenger: ArrayList<String> by lazy { arrayListOf() }
    private val adapterSaveOtherPassenger: ArrayList<String> by lazy { arrayListOf() }
    private val savedGst: ArrayList<PassengerCreateGst> by lazy { arrayListOf() }
    private val checkoutSaverOtherPassenger: ArrayList<CheckoutData.OtherPassenger> by lazy { arrayListOf() }
    private val totalImages: ArrayList<String> by lazy { arrayListOf() }
    private var resultLauncher: ActivityResultLauncher<Intent>? = null

    private val checkoutSavedPassengerAdapter by lazy {
        context?.let {
            CheckoutSavedPassengerAdapter(
                it, checkOutSavedPassenger
            )
        }
    }
    private val checkoutAdapter by lazy {
        context?.let {
            CheckoutAdapter(
                it, passengerCreateData
            ) { holder -> addonPositionChangeListener(holder) }
        }
    }
    private val checkoutOtherPassengerAdapter by lazy {
        context?.let {
            CheckoutPassengerOtherAdapter(
                it, passengerCreateDataOther
            )
        }
    }
    private val checkoutServiceAdapter by lazy {
        context?.let {
            CheckoutServiceAdapter(
                it, passengerCreateDataService
            ) { holder -> click(holder) }
        }
    }
    private val savedPassengerAdapter by lazy {
        context?.let {
            BottomSheetSavedPassengerAdapter(
                it,
                savedPassenger,
                ::clickedPassenger
            )
        }
    }
    private val checkGst by lazy { context?.let { GstAdapter(it, passengerCreateGst) } }
    private val checkSavedGst by lazy { context?.let { GstSavedAdapter(it, checkoutGstSaved) } }
    private val savedGstAdapter by lazy {
        context?.let {
            BottomSheetSavedGstAdapter(
                it, savedGst, ::clickGst
            )
        }
    }


    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+"
    private val types = arrayOf("Mr.", "Mrs.", "Ms.")


    private var reportAdapter: ReportAdapter? = null
    private var passengerGstName: String = ""
    private var passengerGstNo: String = ""
    private var passengerHead: String = ""
    private val requestPermission = 100
    private var passengerName: String = ""
    private var passengerNumber: String = ""
    private var passengerEmail: String = ""
    private var passengerAge: String = ""
    private var passengerTitle: String = ""
    private var passengerCountry: String = ""
    private var singlePassenger: Int = 0
    private var backFlow: Int = 0
    private var passengerCount: Int = 0

    var imageName: String? = ""
    private var imagepathname: String = ""

//    private var reportAdapter: ReportAdapter? = null


    companion object {
        val checkoutGst: ArrayList<CheckoutData.Gst> by lazy { arrayListOf() }
        val BeforeImageData: ArrayList<String> by lazy { arrayListOf() }
        val checkoutDataOther: ArrayList<CheckoutData.OtherPassenger> by lazy { arrayListOf() }
        val checkoutDataService: ArrayList<CheckoutData.Service> by lazy { arrayListOf() }
        var passengerCreateDataOthers = ArrayList<PassengerCreateData>()
        var passengerCreateDataServices = ArrayList<PassengerCreateData>()
        var array = JsonObject()
        var panNumber: String? = ""
        var placard: String? = ""
        var gstNumber: String? = ""
        var gstName: String? = ""
        var uploadCloneTicket: String = ""
    }


    private val addPassenger by lazy {
        context?.let {
            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
                setContentView(
                    layoutInflater.inflate(
                        R.layout.bottomsheet_checkoutnew_passenger, null
                    )
                )
                /** brand color*/
                val pasAddBtn = findViewById<MaterialButton>(R.id.passengerAdd_Button)
                pasAddBtn?.setBackgroundColor(Color.parseColor(activity?.let { it1 ->
                    SavedSharedPreference.getCustomColor(
                        it1
                    ).brand_colour
                }))
                /** Outline button like border line button */
                val cOtherBtn = findViewById<MaterialButton>(R.id.cOtherBtn)
                val color = Color.parseColor(activity?.let { it1 ->
                    SavedSharedPreference.getCustomColor(
                        it1
                    ).secondary_colour
                })
                cOtherBtn?.strokeColor = ColorStateList.valueOf(color)
                cOtherBtn?.setTextColor(color)
                /** check box */
               val iAgreeCheckBox = findViewById<CheckBox>(R.id.iAgree)
                iAgreeCheckBox?.buttonTintList = ColorStateList.valueOf(color)


                setCancelable(true)
                setOnShowListener {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    }


    private val savePassenger by lazy {
        context?.let {
            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
                setContentView(
                    layoutInflater.inflate(
                        R.layout.bottomsheet_checkoutsaved_passenger, null
                    )
                )

                /** brand color*/
                val searchBtn = findViewById<MaterialButton>(R.id.SearchPassengerButton)
                searchBtn?.setBackgroundColor(Color.parseColor(activity?.let { it1 ->
                    SavedSharedPreference.getCustomColor(
                        it1
                    ).brand_colour
                }))

                val addNewPassengerBtn = findViewById<MaterialButton>(R.id.addNewPassenger)
                val color = Color.parseColor(activity?.let { it1 ->
                    SavedSharedPreference.getCustomColor(
                        it1
                    ).secondary_colour
                })
                addNewPassengerBtn?.strokeColor = ColorStateList.valueOf(color)
                addNewPassengerBtn?.setTextColor(color)

                val iAgreeBtn = findViewById<CheckBox>(R.id.iAgree)
                iAgreeBtn?.buttonTintList = ColorStateList.valueOf(color)



                setCancelable(true)
                setOnShowListener {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    }

    private val addGst by lazy {
        context?.let {
            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
                setContentView(
                    layoutInflater.inflate(
                        R.layout.bottomsheet_checkoutaddnew_gst, null
                    )
                )

                /** brand color*/
                val saveGSTBtn = findViewById<MaterialButton>(R.id.saveGst)
                saveGSTBtn?.setBackgroundColor(Color.parseColor(activity?.let { it1 ->
                    SavedSharedPreference.getCustomColor(
                        it1
                    ).brand_colour
                }))

                val pickSaveGstBtn = findViewById<MaterialButton>(R.id.pickSaveGst)
                val color = Color.parseColor(activity?.let { it1 ->
                    SavedSharedPreference.getCustomColor(
                        it1
                    ).secondary_colour
                })
                pickSaveGstBtn?.strokeColor = ColorStateList.valueOf(color)
                pickSaveGstBtn?.setTextColor(color)


                setCancelable(true)
            }
        }
    }

    private val saveGst by lazy {
        context?.let {
            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
                setContentView(layoutInflater.inflate(R.layout.bottomsheet_checkoutsaved_gst, null))

                val pickSaveGstBtn = findViewById<MaterialButton>(R.id.pickSaveGst)
                val color = Color.parseColor(activity?.let { it1 ->
                    SavedSharedPreference.getCustomColor(
                        it1
                    ).secondary_colour
                })
                pickSaveGstBtn?.strokeColor = ColorStateList.valueOf(color)
                pickSaveGstBtn?.setTextColor(color)

                setCancelable(true)
            }
        }
    }

    private fun clickedPassenger(token: String) {
        if (singlePassenger == 0) {
            val savePassengerCheck = savePassenger?.findViewById<CheckBox>(R.id.iAgree)

            val index1 = passengerCreateData.indexOfFirst { it.token == token }

            if (index1 != -1) {
                alertToast(requireContext(), "Already added passenger name cannot be added again")
                return
            }

            val index2 = passengerCreateDataOther.indexOfFirst { it.token == token }

            if (index2 != -1) {
                alertToast(requireContext(), "Already added passenger name cannot be added again")
                return
            }

            val index3 = passengerCreateDataService.indexOfFirst { it.token == token }

            if (index3 != -1) {
                alertToast(requireContext(), "Already added passenger name cannot be added again")
                return
            }

            if (savePassengerCheck?.isChecked == false) {
                alertToast(requireContext(), "Please agree the terms before selecting passenger")
                return
            }

            adapterSavePassenger.clear()
            adapterSavePassenger.add(token)
            for (i in 0 until savedPassenger.size) {
                if (token == savedPassenger[i].token) {
                    passengerCreateData.clear()
                    passengerCreateData.add(savedPassenger[i])
                    checkoutData.add(
                        CheckoutData(
                            token = savedPassenger[i].token,
                            name = savedPassenger[i].name,
                            title = savedPassenger[i].title,
                            name_view = savedPassenger[i].name_view,
                            country_code = savedPassenger[i].country_code,
                            mobile_number = savedPassenger[i].mobile_number,
                            email_id = savedPassenger[i].email_id,
                            date_of_birth = savedPassenger[i].date_of_birth,
                            other = checkoutDataOther,
                            service = checkoutDataService,
                            gst = checkoutGst
                        )
                    )
                    checkoutFragment.cContactPassengerRecyclerview.adapter = checkoutAdapter
//                    checkoutFragment.cContactBtn.visibility = View.GONE
                    checkoutFragment.cContactBtn.text = "Change Lead Passenger"
                }
                savePassenger?.dismiss()
            }
        } else if (singlePassenger == 1) {


            val index1 = passengerCreateData.indexOfFirst { it.token == token }

            if (index1 != -1) {
                alertToast(requireContext(), "Already added passenger name cannot be added again")
                return
            }

            val index2 = passengerCreateDataOther.indexOfFirst { it.token == token }

            if (index2 != -1) {
                alertToast(requireContext(), "Already added passenger name cannot be added again")
                return
            }

            val index3 = passengerCreateDataService.indexOfFirst { it.token == token }

            if (index3 != -1) {
                alertToast(requireContext(), "Already added passenger name cannot be added again")
                return
            }

            val selectedPosition = savedPassenger.indexOfFirst { it.token == token }
            if (savedPassenger[selectedPosition].selected) {
                savedPassenger[selectedPosition].selected = false
                if (checkoutDataOther.isNotEmpty()) {
                    val deletePosition = checkoutDataOther.indexOfFirst { it.token == token }
                    if (deletePosition != -1) {
                        checkoutDataOther.removeAt(deletePosition)
                    }
                }
            } else {

                if (checkoutDataOther.size == passengerCount - 1) {
                    alertToast(
                        requireContext(),
                        "You can add only ${passengerCount - 1} passengers"
                    )
                    return
                }

                savedPassenger[selectedPosition].selected = true
                checkoutDataOther.add(
                    CheckoutData.OtherPassenger(
                        token = savedPassenger[selectedPosition].token,
                        name = savedPassenger[selectedPosition].name,
                        title = savedPassenger[selectedPosition].title,
                        name_view = savedPassenger[selectedPosition].name_view,
                        country_code = savedPassenger[selectedPosition].country_code,
                        mobile_number = savedPassenger[selectedPosition].mobile_number,
                        email_id = savedPassenger[selectedPosition].email_id,
                        date_of_birth = savedPassenger[selectedPosition].date_of_birth
                    )
                )
            }

            savedPassengerAdapter!!.notifyItemChanged(selectedPosition)

//            for (passenger in savedPassenger){
//                if (passenger.token == token){
//
//
//                    passenger.selected = true
//
//                    //passengerCreateDataOther - added in array
////                    passengerCreateDataOther.add(passenger)
//                    checkoutDataOther.add(
//                        CheckoutData.OtherPassenger(
//                            token = passenger.token,
//                            name = passenger.name,
//                            title = "Mr",
//                            name_view = passenger.name_view,
//                            country_code = passenger.country_code,
//                            mobile_number = passenger.mobile_number,
//                            email_id = passenger.email_id,
//                            date_of_birth = passenger.date_of_birth
//                        )
//                    )
//
//                    savePassenger?.findViewById<RecyclerView>(R.id.passengerRecycler)?.adapter =
//                        savedPassengerAdapter
////                    checkoutFragment.cContactOtherPassengerRecyclerview.adapter =
////                        checkoutOtherPassengerAdapter
//                }
//            }


//            for (k in 0 until adapterSavePassenger.size) {
//                if (savedPassenger[k].token == token) {
//                    Toast.makeText(requireContext(), "Already added", Toast.LENGTH_SHORT).show()
//                } else {
//                    adapterSaveOtherPassenger.add(token)
//                    for (j in 0 until savedPassenger.size) if (token == savedPassenger[j].token) {
//                        passengerCreateDataOther.add(savedPassenger[j])
//                        checkoutDataOther.add(
//                            CheckoutData.OtherPassenger(
//                                token = savedPassenger[j].token,
//                                name = savedPassenger[j].name,
//                                title = "Mr",
//                                name_view = savedPassenger[j].name_view,
//                                country_code = savedPassenger[j].country_code,
//                                mobile_number = savedPassenger[j].mobile_number,
//                                email_id = savedPassenger[j].email_id,
//                                date_of_birth = savedPassenger[j].date_of_birth
//                            )
//                        )
//                        checkoutFragment.cContactOtherPassengerRecyclerview.adapter =
//                            checkoutOtherPassengerAdapter
//                    }
//
//                    savePassenger?.dismiss()
//                }
//            }
        } else if (singlePassenger == 2) {


            val index1 = passengerCreateData.indexOfFirst { it.token == token }

            if (index1 != -1) {
                alertToast(requireContext(), "Already added passenger name cannot be added again")
                return
            }

            val index2 = passengerCreateDataOther.indexOfFirst { it.token == token }

            if (index2 != -1) {
                alertToast(requireContext(), "Already added passenger name cannot be added again")
                return
            }

            val index3 = passengerCreateDataService.indexOfFirst { it.token == token }

            if (index3 != -1) {
                alertToast(requireContext(), "Already added passenger name cannot be added again")
                return
            }

            val selectedPosition = savedPassenger.indexOfFirst { it.token == token }

            passengerCreateDataService.clear()
            passengerCreateDataService.add(savedPassenger[selectedPosition])
            checkoutFragment.cContactServicePassengerRecyclerview.adapter =
                checkoutServiceAdapter
//            checkoutFragment.cGreeterBtn.visibility = View.GONE
            checkoutFragment.cGreeterBtn.text = "Change Alternate Contact"
            checkoutDataService.add(
                CheckoutData.Service(
                    token = savedPassenger[selectedPosition].token,
                    name = savedPassenger[selectedPosition].name,
                    title = savedPassenger[selectedPosition].title,
                    name_view = savedPassenger[selectedPosition].name_view,
                    country_code = savedPassenger[selectedPosition].country_code,
                    mobile_number = savedPassenger[selectedPosition].mobile_number,
                    email_id = savedPassenger[selectedPosition].email_id,
                    date_of_birth = savedPassenger[selectedPosition].date_of_birth
                )
            )

            savePassenger?.dismiss()
        }

    }


    //    private val adapterSavePassenger:ArrayList<PassengerCreateData> by lazy { arrayListOf() }

    private fun clickGst(token: String) {
        adapterSaveGst.add(token)
        for (i in 0 until savedGst.size) {
            if (token == savedGst[i].token) {
                passengerCreateGst.clear()
                passengerCreateGst.add(savedGst[i])
                checkoutGst.clear()
                checkoutGst.add(
                    CheckoutData.Gst(
                        gstName = savedGst[i].name, gstno = savedGst[i].gstin
                    )
                )
                checkoutFragment.cCheckoutGstNo.adapter = checkGst
            }
            saveGst?.dismiss()
        }
    }


    private fun click(holder: CheckoutServiceAdapter.MyViewHolder) {
        if (holder.checkoutServiceAdapterBinding.checkoutCancel.isClickable) {
            checkoutDataService.clear()
//            checkoutFragment.cGreeterBtn.visibility = View.VISIBLE
            checkoutFragment.cGreeterBtn.text = "Add Alternate Contact"
        }
    }


    private fun addonPositionChangeListener(holder: CheckoutAdapter.MyViewHolder) {
        if (holder.checkoutAdapterBinding.checkoutCancel.isClickable) {
            checkoutData.clear()
            checkoutFragment.cContactBtn.text = "Add Lead Passenger"
//            checkoutFragment.cContactBtn.visibility = View.VISIBLE
        }
    }


    override fun onStart() {
        super.onStart()
        checkoutDataOther
    }

    @SuppressLint("SimpleDateFormat", "CutPasteId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** brand color*/
        checkoutFragment.checkOut.setBackgroundColor(Color.parseColor(activity?.let {
            SavedSharedPreference.getCustomColor(
                it
            ).brand_colour
        }))
        checkoutFragment.constraintLayout.setBackgroundColor(Color.parseColor(activity?.let {
            SavedSharedPreference.getCustomColor(
                it
            ).brand_colour
        }))




        passengerCount = requireArguments().getInt("passenger_count")

        if (SummaryFragment.selectItem == "INR") {
            checkoutFragment.cCheckoutAmount.text = SummaryFragment.totalAmountCheckout
        } else {
            checkoutFragment.cCheckoutAmount.text = SummaryFragment.totalAmountCheckout
        }


        checkoutFragment.checkoutBack.setOnDebounceListener {
            if (backFlow == 0) {
                Navigation.findNavController(requireView()).popBackStack()
            } else {
                backFlow = 0
                checkoutFragment.cCheckoutLayoutGst.visibility = View.GONE
                checkoutFragment.cCheckoutLayoutPassenger.visibility = View.VISIBLE
            }
        }

//        checkoutFragment.aServiceTerms.setOnDebounceListener {
//            findNavController().navigate(R.id.action_navigation_checkout_to_policiesFragment2)
//        }
//
//        checkoutFragment.aServicePolicy.setOnDebounceListener {
//            findNavController().navigate(R.id.action_navigation_checkout_to_policiesFragment2)
//        }
//
//        checkoutFragment.aServicePolicyName.setOnDebounceListener {
//            findNavController().navigate(R.id.action_navigation_checkout_to_policiesFragment2)
//        }


        checkoutFragment.cContactBtn.setOnDebounceListener {

            if (checkoutFragment.cContactBtn.text.toString() == "Change Lead Passenger"){
                singlePassenger = 0
                addPassenger?.findViewById<TextView>(R.id.passengerDetailAdd)?.text =
                    "Add Lead Passenger"
                addPassenger?.findViewById<TextView>(R.id.passengerDetailNew)?.text =
                    "Please Enter Lead Passenger Details"
                addPassenger?.findViewById<TextView>(R.id.passengerNumberEdittext)?.text =
                    SavedSharedPreference.getUserData(requireContext()).mobile
                addPassenger?.findViewById<Button>(R.id.passangeraddbutton)?.visibility = View.GONE
                addPassenger?.findViewById<CheckBox>(R.id.iAgree)?.visibility = View.VISIBLE
                addPassenger?.show()
            } else if ((passengerCreateData.size + passengerCreateDataOther.size) == passengerCount) {
                alertToast(requireContext(), "Exceeded passenger count")
            } else {
                singlePassenger = 0
                addPassenger?.findViewById<TextView>(R.id.passengerDetailAdd)?.text =
                    "Add Lead Passenger"
                addPassenger?.findViewById<TextView>(R.id.passengerDetailNew)?.text =
                    "Please Enter Lead Passenger Details"
                addPassenger?.findViewById<TextView>(R.id.passengerNumberEdittext)?.text =
                    SavedSharedPreference.getUserData(requireContext()).mobile
                addPassenger?.findViewById<Button>(R.id.passangeraddbutton)?.visibility = View.GONE
                addPassenger?.findViewById<CheckBox>(R.id.iAgree)?.visibility = View.VISIBLE
                addPassenger?.show()
            }
        }

        checkoutFragment.cOtherBtn.setOnDebounceListener {
            if ((passengerCreateData.size + passengerCreateDataOther.size) == passengerCount) {
                alertToast(requireContext(), "Exceeded passenger count")
            } else {
                singlePassenger = 1
                addPassenger?.findViewById<TextView>(R.id.passengerDetailAdd)?.text =
                    "Add Other Passenger Details"
                addPassenger?.findViewById<TextView>(R.id.passengerDetailNew)?.text =
                    "Please Enter Other Passenger Details"
                addPassenger?.findViewById<TextView>(R.id.passengerNumberEdittext)?.text =
                    SavedSharedPreference.getUserData(requireContext()).mobile
                addPassenger?.findViewById<Button>(R.id.passangeraddbutton)?.visibility = View.GONE
                addPassenger?.findViewById<CheckBox>(R.id.iAgree)?.visibility = View.GONE
                addPassenger?.show()
            }
        }

        checkoutFragment.cGreeterBtn.setOnDebounceListener {
            singlePassenger = 2
            addPassenger?.findViewById<TextView>(R.id.passengerDetailAdd)?.text =
                "Add Greeter / Family Contact Details"
            addPassenger?.findViewById<TextView>(R.id.passengerDetailNew)?.text =
                "Please Enter Greeter / Family Contact Details"
            addPassenger?.findViewById<Button>(R.id.passangeraddbutton)?.visibility = View.GONE
            addPassenger?.findViewById<CheckBox>(R.id.iAgree)?.visibility = View.GONE
            addPassenger?.show()
        }

        addPassenger?.findViewById<Button>(R.id.cOtherBtn)?.setOnDebounceListener {
            addPassenger?.dismiss()

            if (singlePassenger == 0) {
                savePassenger?.findViewById<Button>(R.id.SearchPassengerButton)?.text =
                    "Add 1 Passenger"
                savePassenger?.findViewById<CheckBox>(R.id.iAgree)?.visibility = View.VISIBLE
            } else if (singlePassenger == 1) {
                savePassenger?.findViewById<Button>(R.id.SearchPassengerButton)?.text =
                    "Add ${passengerCount - 1} Passenger"
                savePassenger?.findViewById<CheckBox>(R.id.iAgree)?.visibility = View.GONE
            }

            savePassenger?.show()
            readPassenger()
        }

        addGst?.findViewById<Button>(R.id.pickSaveGst)?.setOnDebounceListener {
            addGst?.dismiss()
            saveGst?.show()
            readGst()
        }

        savePassenger?.findViewById<Button>(R.id.addNewPassenger)?.setOnDebounceListener {
            savePassenger?.dismiss()
            addPassenger?.findViewById<Button>(R.id.passangeraddbutton)?.visibility = View.GONE
            addPassenger?.show()
        }

        savePassenger?.findViewById<Button>(R.id.SearchPassengerButton)?.setOnDebounceListener {
            if (singlePassenger == 1) {
                passengerCreateDataOther.clear()
                for (data in checkoutDataOther) {
                    passengerCreateDataOther.add(
                        PassengerCreateData(
                            token = data.token!!,
                            name = data.name!!,
                            title = data.title!!,
                            name_view = data.name_view,
                            country_code = data.country_code,
                            mobile_number = data.mobile_number,
                            email_id = data.email_id,
                            date_of_birth = data.date_of_birth,
                            age = "0",
                            selected = true
                        )
                    )
                }
                checkoutFragment.cContactOtherPassengerRecyclerview.adapter =
                    checkoutOtherPassengerAdapter
                savePassenger?.dismiss()
            }
        }

        addPassenger?.findViewById<Spinner>(R.id.passangername_spinner)?.adapter = context.let {
            context?.let { it1 ->
                ArrayAdapter(
                    it1, R.layout.spinner_item, types
                )
            }
        } as SpinnerAdapter

        addPassenger?.findViewById<Spinner>(R.id.passangername_spinner)?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    passengerHead = parent?.getItemAtPosition(position).toString()
                }
            }


        addPassenger?.findViewById<Button>(R.id.passengerAdd_Button)?.setOnDebounceListener {

            if (singlePassenger == 0) {
                val name =
                    addPassenger?.findViewById<EditText>(R.id.passengerNameEdittext)?.text.toString()

                val index1 = passengerCreateData.indexOfFirst { it.name == name }

                if (index1 != -1) {
                    alertToast(
                        requireContext(),
                        "Already added passenger name cannot be added again"
                    )
                    return@setOnDebounceListener
                }

                val index2 = passengerCreateDataOther.indexOfFirst { it.name == name }

                if (index2 != -1) {
                    alertToast(
                        requireContext(),
                        "Already added passenger name cannot be added again"
                    )
                    return@setOnDebounceListener
                }

                val index3 = passengerCreateDataService.indexOfFirst { it.name == name }

                if (index3 != -1) {
                    alertToast(
                        requireContext(),
                        "Already added passenger name cannot be added again"
                    )
                    return@setOnDebounceListener
                }
            }

            if (singlePassenger == 1) {

                val name =
                    addPassenger?.findViewById<EditText>(R.id.passengerNameEdittext)?.text.toString()

                val index1 = passengerCreateData.indexOfFirst { it.name == name }

                if (index1 != -1) {
                    alertToast(
                        requireContext(),
                        "Already added passenger name cannot be added again"
                    )
                    return@setOnDebounceListener
                }

                val index2 = passengerCreateDataOther.indexOfFirst { it.name == name }

                if (index2 != -1) {
                    alertToast(
                        requireContext(),
                        "Already added passenger name cannot be added again"
                    )
                    return@setOnDebounceListener
                }

                val index3 = passengerCreateDataService.indexOfFirst { it.name == name }

                if (index3 != -1) {
                    alertToast(
                        requireContext(),
                        "Already added passenger name cannot be added again"
                    )
                    return@setOnDebounceListener
                }

                if (checkoutDataOther.size == passengerCount - 1) {
                    alertToast(requireContext(), "You can add only ${passengerCount - 1} passenger")
                    return@setOnDebounceListener
                }
            }

            if (addPassenger?.findViewById<CheckBox>(R.id.iAgree)?.visibility == View.VISIBLE) {
                if (addPassenger?.findViewById<CheckBox>(R.id.iAgree)?.isChecked == false) {
                    Toast.makeText(requireActivity(), "Please agree the terms", Toast.LENGTH_SHORT)
                        .show()
                } else if (addPassenger?.findViewById<EditText>(R.id.passengerNameEdittext)?.text?.isEmpty() == true) {
                    Toast.makeText(requireActivity(), "Name field is empty", Toast.LENGTH_SHORT)
                        .show()
                }
                else if (addPassenger?.findViewById<EditText>(R.id.passengerNumberEdittext)?.text?.isEmpty() == true) {
                    Toast.makeText(requireActivity(), "Number field is empty", Toast.LENGTH_SHORT)
                        .show()
                } else if (addPassenger?.findViewById<EditText>(R.id.passengerEmailEdittext)?.text?.isEmpty() == true) {
                    Toast.makeText(requireActivity(), "Email field is empty", Toast.LENGTH_SHORT)
                        .show()
                }
                else if (!addPassenger?.findViewById<EditText>(R.id.passengerEmailEdittext)?.text.toString()
                        .matches(emailPattern.toRegex())
                ) {
                    Toast.makeText(context, "Please enter valid email address", Toast.LENGTH_SHORT)
                        .show()
//            }else if (AddPassenger?.findViewById<Spinner>(R.id.passangername_spinner)?.text?.){
//                Toast.makeText(requireActivity(), "Check Mobile Number", Toast.LENGTH_SHORT).show()
                } else {

                    passengerName =
                        addPassenger?.findViewById<EditText>(R.id.passengerNameEdittext)?.text.toString()
                    passengerNumber =
                        addPassenger?.findViewById<EditText>(R.id.passengerNumberEdittext)?.text.toString()
                    passengerEmail =
                        addPassenger?.findViewById<EditText>(R.id.passengerEmailEdittext)?.text.toString()
                    passengerTitle =
                        addPassenger?.findViewById<Spinner>(R.id.passengerNameSpinner)?.selectedItem.toString()
                    passengerCountry =
                        addPassenger?.findViewById<com.hbb20.CountryCodePicker>(R.id.code)?.selectedCountryCode.toString()

                    addPassenger?.dismiss()

                    addPassenger?.findViewById<EditText>(R.id.passengerEmailEdittext)?.text?.clear()
                    addPassenger?.findViewById<EditText>(R.id.passengerNameEdittext)?.text?.clear()
                    addPassenger?.findViewById<EditText>(R.id.passangernumber_edittext)?.text?.clear()

                    buttonClick()
                }
            } else {
                if (addPassenger?.findViewById<EditText>(R.id.passengerNameEdittext)?.text?.isEmpty() == true) {
                    Toast.makeText(requireActivity(), "Name field is empty", Toast.LENGTH_SHORT)
                        .show()
                }
                else if (addPassenger?.findViewById<EditText>(R.id.passengerNumberEdittext)?.text?.isEmpty() == true) {
                    Toast.makeText(requireActivity(), "Mobile Number field is empty", Toast.LENGTH_SHORT)
                        .show()
                }
//                else if (addPassenger?.findViewById<EditText>(R.id.passengerEmailEdittext)?.text?.isEmpty() == true) {
//                    Toast.makeText(requireActivity(), "Email field is empty", Toast.LENGTH_SHORT)
//                        .show()
//                }
//                else if (!addPassenger?.findViewById<EditText>(R.id.passengerEmailEdittext)?.text.toString()
//                        .matches(emailPattern.toRegex())
//                ) {
//                    Toast.makeText(context, "Please enter valid email address", Toast.LENGTH_SHORT)
//                        .show()
////            }else if (AddPassenger?.findViewById<Spinner>(R.id.passangername_spinner)?.text?.){
////                Toast.makeText(requireActivity(), "Check Mobile Number", Toast.LENGTH_SHORT).show()
//                }
                else {
                    passengerName =
                        addPassenger?.findViewById<EditText>(R.id.passengerNameEdittext)?.text.toString()
                    passengerNumber =
                        addPassenger?.findViewById<EditText>(R.id.passengerNumberEdittext)?.text.toString()
                    passengerEmail =
                        addPassenger?.findViewById<EditText>(R.id.passengerEmailEdittext)?.text.toString()
                    passengerTitle =
                        addPassenger?.findViewById<Spinner>(R.id.passengerNameSpinner)?.selectedItem.toString()
                    passengerCountry =
                        addPassenger?.findViewById<com.hbb20.CountryCodePicker>(R.id.code)?.selectedCountryCode.toString()

                    addPassenger?.dismiss()

                    addPassenger?.findViewById<EditText>(R.id.passengerEmailEdittext)?.text?.clear()
                    addPassenger?.findViewById<EditText>(R.id.passengerNameEdittext)?.text?.clear()
                    addPassenger?.findViewById<EditText>(R.id.passengerNumberEdittext)?.text?.clear()

                    buttonClick()

                }
            }
        }



        addGst?.findViewById<Button>(R.id.saveGst)?.setOnDebounceListener {
            val gstNumberEditText = addGst?.findViewById<EditText>(R.id.addGstNo)
            val gstNumber = gstNumberEditText?.text?.toString()
            if (addGst?.findViewById<EditText>(R.id.addGstCompany)?.text?.isEmpty() == true) {
                Toast.makeText(context, "Please Add the Gst Name", Toast.LENGTH_SHORT).show()
            }else if ((gstNumber?.length ?: 0) < 15) {
                Toast.makeText(context, "GST number must be 15 digits", Toast.LENGTH_SHORT).show()
            } else if (gstNumber.isNullOrEmpty()) {
                Toast.makeText(context, "Please add the GSTIN", Toast.LENGTH_SHORT).show()
            } else {
                passengerGstName =
                    addGst?.findViewById<EditText>(R.id.addGstCompany)?.text.toString()
                passengerGstNo = addGst?.findViewById<EditText>(R.id.addGstNo)?.text.toString()

                addGst?.dismiss()
                buttonGst()

                addGst?.findViewById<EditText>(R.id.addGstCompany)?.text?.clear()
                addGst?.findViewById<EditText>(R.id.addGstNo)?.text?.clear()
            }
        }


        checkoutFragment.cUploadBtn.setOnDebounceListener {
            BeforeImageData.clear()
            pickImage()
        }



        savePassenger?.findViewById<SearchView>(R.id.search)
            ?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                @SuppressLint("SetTextI18n")
                override fun onQueryTextSubmit(search: String): Boolean {
                    if (search.isNotEmpty()) {
                        if (search.length >= 3) {
                            savedPassengerAdapter?.filter?.filter(search)
                        }
                    } else {
                        savedPassengerAdapter?.addData(savedPassenger)
                        savePassenger?.findViewById<RecyclerView>(R.id.passengerRecycler)?.adapter =
                            savedPassengerAdapter
                    }
                    return false
                }

                @SuppressLint("SetTextI18n")
                override fun onQueryTextChange(search: String): Boolean {
                    if (search.isNotEmpty()) {
                        if (search.length >= 3) {
                            savedPassengerAdapter?.filter?.filter(search)
                        }
                    } else {
                        savedPassengerAdapter?.addData(savedPassenger)
                        savePassenger?.findViewById<RecyclerView>(R.id.passengerRecycler)?.adapter =
                            savedPassengerAdapter

                    }
                    return false
                }
            })

        checkoutFragment.cCheckoutGstBtn.setOnDebounceListener {
            addGst?.show()
        }

        checkoutFragment.next.setOnDebounceListener {
            backFlow = 1
            if ((passengerCreateData.size + passengerCreateDataOther.size) < passengerCount) {
                alertToast(requireContext(), "Please add all passenger details")
            } else if (passengerCreateData.isEmpty()) {
                Toast.makeText(context, "Please add lead Passenger", Toast.LENGTH_SHORT).show()
//            }else if(passengerCreateDataother.isEmpty()){
//                Toast.makeText(context, "Please other Passenger", Toast.LENGTH_SHORT).show()
//            }
                //            else if (passengerCreateDataservice.isEmpty()){
//                Toast.makeText(context, "Please Greeter Passenger", Toast.LENGTH_SHORT).show()
//            } else if (passengerCreateDataOther.isEmpty()) {
//                Toast.makeText(context, "Please other Passenger", Toast.LENGTH_SHORT).show()
            } else if (BeforeImageData.isEmpty()) {
                Toast.makeText(context, "Please upload E-Ticket", Toast.LENGTH_SHORT).show()
            } else {
                val fileUploader = AWSFileUploader(requireContext(), BeforeImageData, this)
                fileUploader.uploadImage()
                checkoutFragment.cCheckoutLayoutGst.visibility = View.VISIBLE
                checkoutFragment.cCheckoutLayoutPassenger.visibility = View.GONE
                checkoutFragment.nestedScrollView.fullScroll(View.FOCUS_UP)
            }
        }

        checkoutFragment.checkOut.setOnDebounceListener {

//            panNumber = checkoutFragment.cCheckoutpanedittext.text.toString()
//            placard = checkoutFragment.cCheckoutplacecarddetailedittext.text.toString()
//            if (panNumber!!.isEmpty()){
//                Toast.makeText(context, "Please add Pan Detail", Toast.LENGTH_SHORT).show()
//            }else if(checkoutGst.isEmpty()){
//                Toast.makeText(context, "Please add GST Detail", Toast.LENGTH_SHORT).show()
//            }else if(placard!!.isEmpty()){
//                Toast.makeText(context, "Please add Meet and Greet Detail", Toast.LENGTH_SHORT).show()
//            }else{

            if ((passengerCreateData.size + passengerCreateDataOther.size) < passengerCount) {
                alertToast(requireContext(), "Please add all passenger details")
            } else if (passengerCreateData.isEmpty()) {
                Toast.makeText(context, "Please add lead Passenger", Toast.LENGTH_SHORT).show()
            }else if (BeforeImageData.isEmpty()) {
                Toast.makeText(context, "Please upload E-Ticket", Toast.LENGTH_SHORT).show()
            } else if (checkoutFragment.aServiceCheckbox.isChecked) {
                panNumber = checkoutFragment.cCheckoutPanEdittext.text.toString()
                placard = checkoutFragment.cCheckoutPlaceCardDetailEdittext.text.toString()
                if (checkoutGst.isEmpty()) {
                    gstNumber = ""
                    gstName = ""
                } else {
                    gstNumber = checkoutGst[0].gstno
                    gstName = checkoutGst[0].gstName
                }

                for (i in 0 until checkoutData.size) {
                    array = JsonObject().apply {
                        addProperty("token", checkoutData[i].token)
                        addProperty("title", checkoutData[i].title)
                        addProperty("name", checkoutData[i].name)
                        addProperty("name_view", checkoutData[i].name_view)
                        addProperty("country_code", checkoutData[i].country_code)
                        addProperty("mobile_number", checkoutData[i].mobile_number)
                        addProperty("email_id", checkoutData[i].email_id)
                        addProperty("date_of_birth", checkoutData[i].date_of_birth)
                        addProperty("age", "18")
                    }
                }
                launchActivity(PaymentActivity::class.java)
                Log.d("creat_order","Checkout")
            } else {
                Toast.makeText(context, "Please accept terms and condition", Toast.LENGTH_SHORT)
                    .show()
            }
        }


        saveGst?.findViewById<Button>(R.id.pickSaveGst)?.setOnDebounceListener {
            saveGst?.dismiss()
            addGst?.show()
        }

        customTextView(checkoutFragment.termsTextView)



        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->

                val data = result.data
                // check condition
                // check condition
                if (data != null) {
                    // When data is not equal to empty
                    // Get PDf uri
                    // set Uri on text view
                    val sPath: String = getRealPathFromURI(data.data!!, requireContext()).toString()

                    BeforeImageData.clear()
                    BeforeImageData.add(sPath)
                    checkoutFragment.uploadRecyclerview.adapter =
                        ReportAdapter(requireContext(), BeforeImageData)

                    val fileUploader = AWSFileUploader(requireContext(), BeforeImageData, this)
                    fileUploader.uploadImage()


                }
            })


        addPassenger?.findViewById<Spinner>(R.id.passengerNameSpinner)?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    (view as TextView).setTextColor(ContextCompat.getColor(requireContext(), R.color.textcolor)) //Change selected text color
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }
    }


    private fun getRealPathFromURI(uri: Uri, context: Context): String? {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        val nameIndex =  returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val size = returnCursor.getLong(sizeIndex).toString()
        val file = File(context.filesDir, name)
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable: Int = inputStream?.available() ?: 0
            //int bufferSize = 1024;
            val bufferSize = Math.min(bytesAvailable, maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream?.read(buffers).also {
                    if (it != null) {
                        read = it
                    }
                } != -1) {
                outputStream.write(buffers, 0, read)
            }
            Log.e("File Size", "Size " + file.length())
            inputStream?.close()
            outputStream.close()
            Log.e("File Path", "Path " + file.path)

        } catch (e: java.lang.Exception) {
            Log.e("Exception", e.message!!)
        }
        return file.path
    }


    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun pickImage() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            MaterialAlertDialogBuilder(requireContext())
                .setCancelable(false)
                .setMessage("What document do you want to upload.")
                .setPositiveButton("Upload Image/Photo", DialogInterface.OnClickListener { dialog, which ->
                    ImagePicker.with(this).crop().maxResultSize(1080, 1080).start()
                })
                .setNegativeButton("Upload Document", DialogInterface.OnClickListener { dialog, which ->
                    selectPdf()
                })
                .setNeutralButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                  dialog.dismiss()
                })
                .show()
        } else {
            checkCameraPermission()
        }
    }

    private fun selectPdf() {
//        val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
//        pdfIntent.type = "application/pdf"
//        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
//        startActivityForResult(pdfIntent, 12)

        selectPDF()
    }


    private fun selectPDF() {
        // Initialize intent
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        // set type
        intent.type = "application/pdf"
        // Launch intent
        resultLauncher!!.launch(intent)
    }




    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.CAMERA), requestPermission
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (resultCode) {
                Activity.RESULT_OK -> {

                    val uri = data!!.data
                    if (uri != null){
                        val fullPath = getPath(activity, uri)
                        imagepathname = fullPath?.let { File(it).toString() }!!
                        BeforeImageData.clear()
                        BeforeImageData.add(imagepathname)
                        checkoutFragment.uploadRecyclerview.adapter =
                            ReportAdapter(requireContext(), BeforeImageData)

                        val fileUploader = AWSFileUploader(requireContext(), BeforeImageData, this)
                        fileUploader.uploadImage()
                    }
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(
                        requireContext(),
                        "You have achieved maximum number of upload...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    Toast.makeText(requireContext(), ".......", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    @SuppressLint("ObsoleteSdkInt")
    fun getPath(context: Context?, uri: Uri): String? {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(
                context, uri
            )
        ) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                return FileUtils(requireContext()).getPath(uri)
//                val id = DocumentsContract.getDocumentId(uri)
//                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
//                return context?.let { getDataColumn(it, contentUri, null, null) }
            } else if (isMediaDocument(uri)) { // MediaProvider
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                when (type) {
                    "image" -> {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    "document" -> {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return context?.let { getDataColumn(it, contentUri, selection, selectionArgs) }
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) { // MediaStore (and general)
            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else context?.let {
                getDataColumn(
                    it, uri, null, null
                )
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) { // File
            return uri.path
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    private fun getDataColumn(
        context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }


    private fun buttonClick() {
        val token = SavedSharedPreference.getUserData(requireContext()).token
        val jsonObject = JsonObject().apply {
            addProperty("user_token", token)
            addProperty("title", passengerTitle.replace(".", ""))
            addProperty("name", passengerName)
            addProperty("country_code", passengerCountry)
            addProperty("mobile_number", passengerNumber)
            addProperty("email_id", passengerEmail)
            addProperty("date_of_birth", passengerAge)
        }
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
                viewModel?.createpassenger(jsonObject = jsonObject)
                    ?.observe(requireActivity(), createPassenger)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val createPassenger = Observer<ApiResult<PassengerCreateData>> {
        when (it) {
            is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {
                when (singlePassenger) {
                    0 -> {
                        passengerCreateData.clear()
                        passengerCreateData.add(it.data)
                        checkoutFragment.cContactPassengerRecyclerview.adapter = checkoutAdapter
//                        checkoutFragment.cContactBtn.visibility = View.GONE
                        checkoutFragment.cContactBtn.text = "Change Lead Passenger"
                        checkoutData.add(
                            CheckoutData(
                                token = it.data.token,
                                name = it.data.name,
                                title = it.data.title,
                                name_view = it.data.name_view,
                                country_code = it.data.country_code,
                                mobile_number = it.data.mobile_number,
                                email_id = it.data.email_id,
                                date_of_birth = it.data.date_of_birth,
                                other = checkoutDataOther,
                                service = checkoutDataService,
                                gst = checkoutGst
                            )
                        )

                    }
                    1 -> {
                        passengerCreateDataOther.add(it.data)
                        checkoutFragment.cContactOtherPassengerRecyclerview.adapter =
                            checkoutOtherPassengerAdapter
                        checkoutDataOther.add(
                            CheckoutData.OtherPassenger(
                                token = it.data.token,
                                name = it.data.name,
                                title = it.data.title,
                                name_view = it.data.name_view,
                                country_code = it.data.country_code,
                                mobile_number = it.data.mobile_number,
                                email_id = it.data.email_id,
                                date_of_birth = it.data.date_of_birth
                            )
                        )

                    }
                    2 -> {
                        passengerCreateDataService.clear()
                        passengerCreateDataService.add(it.data)
                        checkoutFragment.cContactServicePassengerRecyclerview.adapter =
                            checkoutServiceAdapter
//                        checkoutFragment.cGreeterBtn.visibility = View.GONE
                        checkoutFragment.cGreeterBtn.text = "Change Alternate Contact"
                        checkoutDataService.add(
                            CheckoutData.Service(
                                token = it.data.token,
                                name = it.data.name,
                                title = it.data.title,
                                name_view = it.data.name_view,
                                country_code = it.data.country_code,
                                mobile_number = it.data.mobile_number,
                                email_id = it.data.email_id,
                                date_of_birth = it.data.date_of_birth
                            )
                        )
                    }
                }
            }
            is ApiResult.Error -> {
                errors("Error", it.message)
            }
        }
    }


    private fun buttonGst() {
        val token = SavedSharedPreference.getUserData(requireContext()).token
        val jsonObject = JsonObject().apply {
            addProperty("user_token", token)
            addProperty("name", passengerGstName)
            addProperty("gstin", passengerGstNo)
        }
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
            viewModel?.creategst(jsonObject = jsonObject)?.observe(requireActivity(), createGst)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val createGst = Observer<ApiResult<PassengerCreateGst>> {
        when (it) {
            is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {
                passengerCreateGst.clear()
                passengerCreateGst.add(it.data)
                checkoutFragment.cCheckoutGstNo.adapter = checkGst
//                checkoutFragment.cCheckoutGstBtn.visibility = View.GONE
                checkoutFragment.cCheckoutGstBtn.text = "Change GSTIN"
                checkoutGst.clear()
                checkoutGst.add(CheckoutData.Gst(gstName = it.data.name, gstno = it.data.gstin))
            }
            is ApiResult.Error -> {
                errors("Error", it.message)
            }
        }
    }


    private fun readPassenger() {
        val token = SavedSharedPreference.getUserData(requireContext()).token
        val jsonObject = JsonObject().apply {
            addProperty("user_token", token)
        }
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
            viewModel?.readdpassenger(jsonObject = jsonObject)
                ?.observe(requireActivity(), readPassengers)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val readPassengers = Observer<ApiResult<List<PassengerCreateData>>> {
        when (it) {
            is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {
                savedPassenger.clear()
                savedPassenger.addAll(it.data)

                for (passengerData in savedPassenger) {
                    if (singlePassenger == 0) {

                    } else if (singlePassenger == 1) {
                        val indexPos =
                            passengerCreateDataOther.indexOfFirst { data -> passengerData.token == data.token }
                        passengerData.selected = indexPos != -1
                    }
                }

                savePassenger?.findViewById<RecyclerView>(R.id.passengerRecycler)?.adapter =
                    savedPassengerAdapter
            }
            is ApiResult.Error -> {
                Toast.makeText(requireContext(), "No Added Passenger", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun readGst() {
        val token = SavedSharedPreference.getUserData(requireContext()).token
        val jsonObject = JsonObject().apply {
            addProperty("user_token", token)
        }
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
            viewModel?.readdgst(jsonObject = jsonObject)?.observe(requireActivity(), gstRead)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val gstRead = Observer<ApiResult<List<PassengerCreateGst>>> {
        when (it) {
            is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {
                savedGst.clear()
                savedGst.addAll(it.data)
                saveGst?.findViewById<RecyclerView>(R.id.recentlyAddedGstRecycler)?.adapter =
                    savedGstAdapter
            }
            is ApiResult.Error -> {
                Toast.makeText(requireContext(), "No Added GST", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        /** brand color */
        val color = Color.parseColor(context?.let { SavedSharedPreference.getCustomColor(it).secondary_colour })
        checkoutFragment.cContactBtn.strokeColor = ColorStateList.valueOf(color)
        checkoutFragment.cContactBtn.setTextColor(color)

        checkoutFragment.cOtherBtn.strokeColor = ColorStateList.valueOf(color)
        checkoutFragment.cOtherBtn.setTextColor(color)

        checkoutFragment.cGreeterBtn.strokeColor = ColorStateList.valueOf(color)
        checkoutFragment.cGreeterBtn.setTextColor(color)

        checkoutFragment.cUploadBtn.strokeColor = ColorStateList.valueOf(color)
        checkoutFragment.cUploadBtn.setTextColor(color)

        checkoutFragment.cCheckoutGstBtn.strokeColor = ColorStateList.valueOf(color)
        checkoutFragment.cCheckoutGstBtn.setTextColor(color)

        checkoutFragment.aServiceCheckbox.buttonTintList = ColorStateList.valueOf(color)

        return checkoutFragment.root
    }


    override fun onSuccess(uploadClonePdf: String) {
        uploadCloneTicket = uploadClonePdf
        Log.d(AwsConstants.TAG, "onSuccess: ${uploadClonePdf}")
        Toast.makeText(requireContext(), "Upload E-Ticket Successfully", Toast.LENGTH_SHORT).show()
    }

    override fun onFailure(exception: Exception) {
        Toast.makeText(requireContext(), "Upload E-Ticket Failed", Toast.LENGTH_SHORT).show()
        Log.d(AwsConstants.TAG, "onSuccess: ${exception.localizedMessage}")
    }

    @SuppressLint("SetTextI18n")
    private fun customTextView(textView: TextView) {

        val hyperColor = ContextCompat.getColor(requireContext(), R.color.hyperLink)
        val blackColor = ContextCompat.getColor(requireContext(), R.color.textcolor)

        val termsText = SpannableStringBuilder()

        termsText.color(blackColor) { append("I agree to ") }
        termsText.color(hyperColor) {
            append("Terms and Conditions ")
            val start = 11
            val end = 31
            setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    requireActivity().startActivity(
                        Intent(requireContext(), WebView::class.java)
                            .putExtra(
                                "URL",
                                "https://airportzostage.in/web-app/terms.php"
                            )
                            .putExtra("heading", "Terms and Conditions")
                    )
                }

            }, start, end, 0)
        }
        termsText.color(blackColor) { append("& ") }
        termsText.color(hyperColor) {
            append("Cancellation Policy ")
            val start = 34
            val end = 53
            setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    requireActivity().startActivity(
                        Intent(requireContext(), WebView::class.java)
                            .putExtra(
                                "URL",
                                "https://airportzostage.in/web-app/cancellation_policy.php"
                            )
                            .putExtra("heading", "Cancellation Policy")
                    )
                }

            }, start, end, 0)
        }
        termsText.color(blackColor) { append("of AirportZo Ltd.") }

        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setText(termsText, TextView.BufferType.SPANNABLE)
    }
}

