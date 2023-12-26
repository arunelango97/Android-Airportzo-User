package com.travel.airportzo.user.ui.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.gson.JsonObject
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.SavedGstFragmentBinding
import com.travel.airportzo.user.model.PassengerCreateGst
import com.travel.airportzo.user.network.ApiResult
import com.travel.airportzo.user.network.NoInternetActivity
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.utils.setOnDebounceListener
import com.travel.airportzo.user.ui.adapter.SavedGstAdapter
import com.travel.airportzo.user.ui.base.BaseFragment
import kotlinx.coroutines.launch

class SavedGstFragment : BaseFragment() {

    private val savedGstFragment by lazy { SavedGstFragmentBinding.inflate(layoutInflater) }

    private val passengerSavedGst:ArrayList<PassengerCreateGst> by lazy{ arrayListOf()}
    private val savedGst by lazy { context?.let { SavedGstAdapter(it,passengerSavedGst,::clicked) }}

    private var passengergstName:String = ""
    private var passengerGstNo:String =""
    private var gstToken:String=""




    private val addGst by lazy {
        context?.let {
            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
                setContentView(layoutInflater.inflate(R.layout.bottomsheet_checkoutaddnew_gst, null))
                /** Brand Color*/
                val saveGstBtn = findViewById<MaterialButton>(R.id.saveGst)
                saveGstBtn?.setBackgroundColor(Color.parseColor(activity?.let { it1 ->
                    SavedSharedPreference.getCustomColor(
                        it1
                    ).brand_colour
                }))

                val pickSaveGst = findViewById<MaterialButton>(R.id.pickSaveGst)
                val colorStateList = Color.parseColor(activity?.let { it1 ->
                    SavedSharedPreference.getCustomColor(
                        it1
                    ).secondary_colour
                })
                pickSaveGst?.strokeColor = ColorStateList.valueOf(colorStateList)





                setCancelable(true)
            }
        }
    }

    private val updateGst by lazy {
        context?.let {
            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
                setContentView(layoutInflater.inflate(R.layout.bottomsheet_updategst, null))
                /** Brand Color*/
                val updateGstBtn = findViewById<MaterialButton>(R.id.saveGst)
                updateGstBtn?.setBackgroundColor(Color.parseColor(activity?.let { it1 ->
                    SavedSharedPreference.getCustomColor(
                        it1
                    ).brand_colour
                }))

                setCancelable(true)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** brand color */

       savedGstFragment.rootLayout.setBackgroundColor(Color.parseColor(activity?.let {
           SavedSharedPreference.getCustomColor(
               it
           ).brand_colour
       }))


        readGST()

        savedGstFragment.gstBack.setOnDebounceListener {
            onBackPressed()
        }

        savedGstFragment.cContactButton.setOnDebounceListener {
            addGst?.findViewById<Button>(R.id.pickSaveGst)?.visibility=View.GONE
            addGst?.show()
        }

        addGst?.findViewById<Button>(R.id.saveGst)?.setOnDebounceListener {
            if (addGst?.findViewById<EditText>(R.id.addGstCompany)?.text?.isEmpty() == true) {
                Toast.makeText(context, "Please add  company Name", Toast.LENGTH_SHORT).show()
            } else if (addGst?.findViewById<EditText>(R.id.addGstNo)?.text?.isEmpty() == true) {
                Toast.makeText(context, "Please add  GSTIN", Toast.LENGTH_SHORT).show()
            } else {
                passengergstName = addGst?.findViewById<EditText>(R.id.addGstCompany)?.text.toString()
                passengerGstNo = addGst?.findViewById<EditText>(R.id.addGstNo)?.text.toString()

                addGst?.dismiss()
                buttonGst()

                addGst?.findViewById<EditText>(R.id.addGstCompany)?.text?.clear()
                addGst?.findViewById<EditText>(R.id.addGstNo)?.text?.clear()

            }
        }
    }


    private fun clicked(outGst: String) {
        gstToken = outGst
        for (i in 0 until passengerSavedGst.size) {
            if (outGst == passengerSavedGst[i].token) {
                updateGst?.findViewById<EditText>(R.id.addGstCompany)?.setText(passengerSavedGst[i].name)
                updateGst?.findViewById<EditText>(R.id.addGstNo)?.setText(passengerSavedGst[i].gstin)
            }
        }
        updateGst?.show()
        updateGst?.findViewById<Button>(R.id.saveGst)?.setOnDebounceListener {
            updateGST()
            updateGst?.dismiss()
        }

        updateGst?.findViewById<Button>(R.id.deleteGst)?.setOnDebounceListener {
            deleteGST()
            updateGst?.dismiss()
        }
    }



    private fun deleteGST() {
        val token =   SavedSharedPreference.getUserData(requireContext()).token
        val jsonObject = JsonObject().apply {
            addProperty("user_token", token)
            addProperty("token", gstToken)
        }
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
            viewModel?.deletegst(jsonObject = jsonObject)?.observe(requireActivity(),deleteGst)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val deleteGst=Observer<ApiResult<Any>>{
        when(it) {
            is ApiResult.Success -> {
                Toast.makeText(requireContext(), "GST Deleted Successfully", Toast.LENGTH_SHORT).show()
                readGST()
            }
            is ApiResult.Error -> {
                errors("Error", it.message)
            }
            else -> {}
        }
    }



    private fun updateGST() {
        val name=updateGst?.findViewById<EditText>(R.id.addGstCompany)?.text.toString()
        val number=updateGst?.findViewById<EditText>(R.id.addGstNo)?.text.toString()
        val token =   SavedSharedPreference.getUserData(requireContext()).token
        val jsonObject = JsonObject().apply {
            addProperty("user_token", token)
            addProperty("token", gstToken)
            addProperty("name", name)
            addProperty("gstin", number)
        }
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
            viewModel?.updategst(jsonObject = jsonObject)?.observe(requireActivity(),updateGstDetail)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val updateGstDetail=Observer<ApiResult<PassengerCreateGst>>{
        when(it) {
            is ApiResult.Success -> {
                Toast.makeText(requireContext(), "GST Updated Successfully", Toast.LENGTH_SHORT).show()
                readGST()
            }
            is ApiResult.Error -> {
//                errors("Error", it.message)
            }
            else -> {}
        }
    }



    private fun readGST() {
        val token =   SavedSharedPreference.getUserData(requireContext()).token
        val jsonObject = JsonObject().apply {
            addProperty("user_token", token)
        }
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
                viewModel?.readdgst(jsonObject = jsonObject)?.observe(requireActivity(), readGst)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val readGst=Observer<ApiResult<List<PassengerCreateGst>>>{
        when(it) {
            is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {
                passengerSavedGst.clear()
                passengerSavedGst.addAll(it.data)
                savedGstFragment.saveNewGst.adapter=savedGst
            }
            is ApiResult.Error -> {
                passengerSavedGst.clear()
                savedGstFragment.saveNewGst.adapter=savedGst
//                errors("Error", it.message)
            }
            else -> {}
        }
    }


    private fun buttonGst() {
        val token = SavedSharedPreference.getUserData(requireContext()).token
        val jsonObject = JsonObject().apply {
            addProperty("user_token", token)
            addProperty("name", passengergstName)
            addProperty("gstin", passengerGstNo)
        }
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
            viewModel?.creategst(jsonObject = jsonObject)?.observe(requireActivity(), saveGst)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val saveGst=Observer<ApiResult<PassengerCreateGst>>{
        when(it) {
            is ApiResult.Success -> {
                Toast.makeText(requireContext(), "GST Added Successfully", Toast.LENGTH_SHORT).show()
                readGST()
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
        return savedGstFragment.root
    }


}