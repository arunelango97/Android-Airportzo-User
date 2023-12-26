package com.travel.airportzo.user.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.gson.JsonObject
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.SavedPassengerFragmentBinding
import com.travel.airportzo.user.model.PassengerCreateData
import com.travel.airportzo.user.network.ApiResult
import com.travel.airportzo.user.network.NoInternetActivity
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.utils.setOnDebounceListener
import com.travel.airportzo.user.ui.adapter.SavedPassengerAdapter
import com.travel.airportzo.user.ui.base.BaseFragment
import kotlinx.coroutines.launch
import java.util.*

class SavedPassengerFragment : BaseFragment() {

    private val savedPassengerFragment by lazy { SavedPassengerFragmentBinding.inflate(layoutInflater) }

    private val newPassengerData: ArrayList<PassengerCreateData> by lazy { arrayListOf() }
    private val savedPassengerAdapter by lazy { context?.let { SavedPassengerAdapter(it,newPassengerData,::onclick) }}

    private  var passengerName :String = ""
    private  var passengerNumber :String = ""
    private  var passengerEmail :String = ""
    private var passengerTitle:String =""
    private var passengerCountry:String = ""
    private var passengerHead:String =""
    private var passengerToken:String =""

    private val types = arrayOf("Mr.","Mrs.","Ms.")

    private val addPassenger by lazy {
        context?.let {
            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
                setContentView(layoutInflater.inflate(R.layout.bottomsheet_checkoutnew_passenger, null))
                /** brand color*/
                val passAddBtn = findViewById<MaterialButton>(R.id.passengerAdd_Button)
                passAddBtn?.setBackgroundColor(Color.parseColor(activity?.let { it1 ->
                    SavedSharedPreference.getCustomColor(
                        it1
                    ).brand_colour
                }))
                setCancelable(true)}
        }
    }

    private val updatePassenger by lazy {
        context?.let {
            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
                setContentView(layoutInflater.inflate(R.layout.bottomsheet_updatepassenger, null))
                /** brand Color*/
                val updateBtn = findViewById<MaterialButton>(R.id.updatebutton)
                updateBtn?.setBackgroundColor(Color.parseColor(activity?.let { it1 ->
                    SavedSharedPreference.getCustomColor(
                        it1
                    ).brand_colour
                }))
                setCancelable(true)}
        }
    }

    private fun onclick(outPassenger: String) {
        passengerToken = outPassenger
        for (i in 0 until newPassengerData.size) {
            if (outPassenger == newPassengerData[i].token) {
                updatePassenger?.findViewById<EditText>(R.id.passangername_edittext)?.setText(newPassengerData[i].name)
                updatePassenger?.findViewById<EditText>(R.id.passangernumber_edittext)?.setText(newPassengerData[i].mobile_number)
                updatePassenger?.findViewById<EditText>(R.id.passangeremail_edittext)?.setText(newPassengerData[i].email_id)
            }
        }
        updatePassenger?.show()
        updatePassenger?.findViewById<Button>(R.id.updatebutton)?.setOnDebounceListener {
            updatePassenger()
            updatePassenger?.dismiss()
        }

        updatePassenger?.findViewById<Button>(R.id.deletebutton)?.setOnDebounceListener {
            deletePassenger()
            updatePassenger?.dismiss()
        }
    }






    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** brand color */

        savedPassengerFragment.rootLayout.setBackgroundColor(Color.parseColor(activity?.let {
            SavedSharedPreference.getCustomColor(
                it
            ).brand_colour
        }))

        readPass()

        savedPassengerFragment.gstBack.setOnDebounceListener {
            onBackPressed()
        }

        savedPassengerFragment.add.setOnDebounceListener {
            addPassenger?.findViewById<Button>(R.id.cOtherBtn)?.visibility=View.GONE
            addPassenger?.findViewById<Button>(R.id.passengerAdd_Button)?.visibility=View.GONE
            addPassenger?.findViewById<CheckBox>(R.id.iAgree)?.visibility=View.GONE
            addPassenger?.findViewById<Button>(R.id.passengerAddButton)?.visibility=View.VISIBLE
            addPassenger?.show()
        }

        addPassenger?.findViewById<Spinner>(R.id.passangername_spinner)?.adapter= context.let { context?.let { it1 -> ArrayAdapter(it1, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, types)
        } } as SpinnerAdapter

        addPassenger?.findViewById<Spinner>(R.id.passangername_spinner)?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                passengerHead = parent?.getItemAtPosition(position).toString()
            }}

        savedPassengerFragment.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            @SuppressLint("SetTextI18n")
            override fun onQueryTextSubmit(search: String): Boolean {
                if (search.isNotEmpty()) {
                    if (search.length >= 3) {
                        savedPassengerAdapter?.filter?.filter(search)
                    }
                } else{
                    savedPassengerAdapter?.addData(newPassengerData)
                    savedPassengerFragment.addPassengerRecycler.adapter = savedPassengerAdapter
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
                    savedPassengerAdapter?.addData(newPassengerData)
                    savedPassengerFragment.addPassengerRecycler.adapter = savedPassengerAdapter
                }
                return false
            }
        })

        addPassenger?.findViewById<Button>(R.id.passengerAddButton)?.setOnDebounceListener {
            if (addPassenger?.findViewById<EditText>(R.id.passengerNameEdittext)?.text?.isEmpty() == true) {
                Toast.makeText(requireActivity(), "Name field is empty", Toast.LENGTH_SHORT).show()
            } else if (addPassenger?.findViewById<EditText>(R.id.passengerNumberEdittext)?.text?.isEmpty() == true) {
                Toast.makeText(requireActivity(), "Number field is empty", Toast.LENGTH_SHORT)
                    .show()
            } else if (addPassenger?.findViewById<EditText>(R.id.passengerEmailEdittext)?.text?.isEmpty() == true) {
                Toast.makeText(requireActivity(), "Email field is empty", Toast.LENGTH_SHORT).show()
//            }else if (addPassenger?.findViewById<Spinner>(R.id.passangername_spinner).selectedItem.i){
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
                buttonClick()

                addPassenger?.findViewById<EditText>(R.id.passengerEmailEdittext)?.text?.clear()
                addPassenger?.findViewById<EditText>(R.id.passengerNameEdittext)?.text?.clear()
                addPassenger?.findViewById<EditText>(R.id.passengerNumberEdittext)?.text?.clear()
            }
        }
    }



    private fun buttonClick(){
        val token =   SavedSharedPreference.getUserData(requireContext()).token
        val jsonObject = JsonObject().apply {
            addProperty("user_token",token)
            addProperty("title",passengerTitle)
            addProperty("name",passengerName)
            addProperty("country_code",passengerCountry)
            addProperty("mobile_number",passengerNumber)
            addProperty("email_id",passengerEmail)
        }
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
            viewModel?.createpassenger(jsonObject = jsonObject )?.observe(requireActivity(),newPassenger)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val newPassenger=Observer<ApiResult<PassengerCreateData>>{
        when(it) {
//            is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {
                Toast.makeText(requireActivity(), "Passenger Added Successfully", Toast.LENGTH_SHORT).show()
                readPass()
            }
            is ApiResult.Error -> {
//                errors("Error", it.message)
            }
            else -> {}
        }
    }



    private fun readPass() {
        val token =   SavedSharedPreference.getUserData(requireContext()).token
        val jsonObject = JsonObject().apply {
            addProperty("user_token", token)
        }
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
            viewModel?.readdpassenger(jsonObject = jsonObject)?.observe(requireActivity(),readPassenger)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val readPassenger=Observer<ApiResult<List<PassengerCreateData>>>{
        when(it) {
            is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {
                newPassengerData.clear()
                newPassengerData.addAll(it.data)
                savedPassengerFragment.addPassengerRecycler.adapter=savedPassengerAdapter
            }
            is ApiResult.Error -> {
                newPassengerData.clear()
                savedPassengerFragment.addPassengerRecycler.adapter=savedPassengerAdapter

//              errors("Error", it.message)
            }
            else -> {}
        }
    }



    private fun deletePassenger() {
        val token =   SavedSharedPreference.getUserData(requireContext()).token
        val jsonObject = JsonObject().apply {
            addProperty("user_token", token)
            addProperty("token", passengerToken)
        }
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
            viewModel?.deletepassenger(jsonObject = jsonObject)?.observe(requireActivity(),deletePassenger)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val deletePassenger=Observer<ApiResult<Any>>{
        when(it) {
            is ApiResult.Success -> {
                readPass()
                Toast.makeText(requireContext(), "Passenger Deleted Successfully", Toast.LENGTH_SHORT).show()

            }
            is ApiResult.Error -> {
                errors("Error", it.message)
            }
            else -> {}
        }
    }



    private fun updatePassenger() {
        val name=updatePassenger?.findViewById<EditText>(R.id.passangername_edittext)?.text.toString()
        val title = addPassenger?.findViewById<Spinner>(R.id.passangername_spinner)?.selectedItem.toString()

        val number=updatePassenger?.findViewById<EditText>(R.id.passangernumber_edittext)?.text.toString()
        val email=updatePassenger?.findViewById<EditText>(R.id.passangeremail_edittext)?.text.toString()
        val passengerCountry =
            addPassenger?.findViewById<com.hbb20.CountryCodePicker>(R.id.code)?.selectedCountryCode.toString()
        val token =   SavedSharedPreference.getUserData(requireContext()).token
        val jsonObject = JsonObject().apply {
            addProperty("user_token", token)
            addProperty("token", passengerToken)
            addProperty("title", title.replace(".",""))
            addProperty("name", name)
            addProperty("country_code", passengerCountry)
            addProperty("mobile_number", number)
            addProperty("email_id", email)
        }
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
                viewModel?.updatepassenger(jsonObject = jsonObject)
                    ?.observe(requireActivity(), updatePassengers)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val updatePassengers=Observer<ApiResult<PassengerCreateData>>{
        when(it) {
            is ApiResult.Success -> {
                Toast.makeText(requireContext(), "Passenger Updated Successfully", Toast.LENGTH_SHORT).show()
                readPass()
            }
            is ApiResult.Error -> {
//                errors("Error", it.message)
            }
            else -> {}
        }
    }



    private fun onBackPressed() {
        Navigation.findNavController(requireView()).popBackStack()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        return savedPassengerFragment.root
    }

}