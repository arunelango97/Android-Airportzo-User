package com.travel.airportzo.user.ui.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.travel.airportzo.user.databinding.ManageAccountFragmentBinding
import com.travel.airportzo.user.model.ManageAccountData
import com.travel.airportzo.user.model.UpdateprofileData
import com.travel.airportzo.user.network.ApiResult
import com.travel.airportzo.user.network.NoInternetActivity
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.ui.activity.MainActivity
import com.travel.airportzo.user.ui.adapter.ManageAccountAdapter
import com.travel.airportzo.user.ui.base.BaseFragment
import com.travel.airportzo.user.utils.setOnDebounceListener
import kotlinx.coroutines.launch
import java.util.ArrayList


class ManageAccountFragment : BaseFragment() {

    private val manageAccountFragment by lazy { ManageAccountFragmentBinding.inflate(layoutInflater) }
    private val manageAccountData: ArrayList<ManageAccountData> by lazy { arrayListOf() }
    private val manageAccountAdapter by lazy { ManageAccountAdapter(manageAccountData,::logout) }
    private var deviceTokenArray = JsonArray()

    private fun logout(DeviceToken: String) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("Are you sure you want to Logout?")
            .setCancelable(false)
            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
            .setPositiveButton("Logout", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                val token =   SavedSharedPreference.getUserData(requireContext()).token
                val jsonObject = JsonObject().apply {
                    addProperty("user_token", token)
                    add("device_token", JsonArray().apply { add(DeviceToken)})
                }
                Log.d("TAG", "logout: "+jsonObject)
                if (isNetworkConnected(requireContext())) {
                    lifecycleScope.launch{
                        viewModel?.accountLogout(jsonObject = jsonObject)?.observe(requireActivity(),accountLogout)
                    }
                    findNavController().popBackStack()
                } else {
                    startActivity(Intent(requireContext(), NoInternetActivity::class.java))
                }

            })
            .show()
    }

    private val accountLogout=Observer<ApiResult<Any>>{
        when(it) {
//          is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {

            }
            is ApiResult.Error -> {
//                errors("Error", it.message)
            }
            else -> {}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileData()
        /** brand color */

        manageAccountFragment.rootLayout.setBackgroundColor(Color.parseColor(activity?.let {
            SavedSharedPreference.getCustomColor(
                it
            ).brand_colour
        }))


        manageAccountFragment.agentButton.setBackgroundColor(Color.parseColor(activity?.let {
            SavedSharedPreference.getCustomColor(
                it
            ).brand_colour
        }))


        manageAccountFragment.personalBack.setOnDebounceListener {
            findNavController().popBackStack()
        }

        manageAccountFragment.agentButton.setOnDebounceListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder.setMessage("Are you sure you want to Logout?")
                .setCancelable(false)
                .setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
                .setPositiveButton("Logout", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                    deviceTokenArray.apply {
                        for (i in 0 until manageAccountData.size) {
                            add(manageAccountData[i].deviceToken)
                        }
                    }
                    val token = SavedSharedPreference.getUserData(requireContext()).token
                    val jsonObject = JsonObject().apply {
                        addProperty("user_token", token)
                        add("device_token", deviceTokenArray)
                    }
                    viewModel?.accountLogout(jsonObject = jsonObject)
                        ?.observe(requireActivity(), allAccountLogout)
                    Log.d("TAG", "onCreate: $jsonObject")

                })
                .show()
        }
    }

    private val allAccountLogout=Observer<ApiResult<Any>>{
        when(it) {
//          is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {

            }
            is ApiResult.Error -> {
//                errors("Error", it.message)
            }
            else -> {}
        }
    }


    private fun profileData() {
        val token =   SavedSharedPreference.getUserData(requireContext()).token
        val jsonObject = JsonObject().apply {
            addProperty("user_token", token)
        }
        Log.d("TAG", "jsonObject: $jsonObject")
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
                viewModel?.manageAccount(jsonObject = jsonObject)?.observe(requireActivity(), manageAccount)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val manageAccount = Observer<ApiResult<List<ManageAccountData>>>{
        when(it){
            is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {
                manageAccountData.clear()
                manageAccountData.addAll(it.data)
                manageAccountFragment.manageAccountRecyclerview.adapter = manageAccountAdapter
            }
            is ApiResult.Error -> {
                errors("Error", it.message)
            }
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return manageAccountFragment.root
    }


}