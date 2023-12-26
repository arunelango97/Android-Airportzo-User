package com.travel.airportzo.user.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonObject
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.ProfileFragmentBinding
import com.travel.airportzo.user.model.UpdateprofileData
import com.travel.airportzo.user.network.ApiResult
import com.travel.airportzo.user.network.NoInternetActivity
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.utils.setOnDebounceListener
import com.travel.airportzo.user.ui.base.BaseFragment
import kotlinx.coroutines.launch


class ProfileFragment : BaseFragment() {

    private val profileFragment by lazy { ProfileFragmentBinding.inflate(layoutInflater) }

     var agent : Boolean = false
     private var approved : String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        profileData()

        profileFragment.summaryBack.setOnDebounceListener {
            onBackPressed()
        }

        profileFragment.settingLayout.setOnDebounceListener {
                findNavController().navigate(R.id.action_home_to_Settingfragment)
        }

        profileFragment.helpLayout.setOnDebounceListener {
                findNavController().navigate(R.id.action_navigation_profile_to_helpFragment)
        }

        profileFragment.lookServiceButton.setOnDebounceListener {
                findNavController().navigate(R.id.action_navigation_profile_to_becomeAnAgentFragment)
        }

        profileFragment.bookingDetailCard.setOnDebounceListener {
               findNavController().navigate(R.id.action_navigation_profile_to_agentDashBoardFragment)
        }

        profileFragment.personalLayout.setOnDebounceListener {
                findNavController().navigate(R.id.action_navigation_profile_to_personalFragment,bundleOf("agent" to agent ,"approved" to approved))
        }


    }

    private fun profileData() {
        val token =   SavedSharedPreference.getUserData(requireContext()).token
        val jsonObject = JsonObject().apply {
            addProperty("user_token", token)
        }
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
            viewModel?.updateprofile(jsonObject = jsonObject)?.observe(requireActivity(), updatedProfile)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val updatedProfile =Observer<ApiResult<UpdateprofileData>>{
        when(it){
            is ApiResult.Success -> {
              profileFragment.apply {
                  agent = it.data.is_agent
                  approved = it.data.is_approved
                  profileName.text = it.data.name
                  profileNumber.text = it.data.country_code .plus(it.data.mobile_number)
                  profileEmail.text = it.data.email
                  context?.let { it1 ->
                      Glide.with(it1).load(it.data.image)
                          .apply(RequestOptions.circleCropTransform())
                          .error(R.drawable.ic_profile_1x).into(profileFragment.notUpdateImage)
                  }
                  if (!it.data.is_agent){
                      profileFragment.knowMore.visibility = View.VISIBLE
                      profileFragment.agentPending.visibility = View.GONE
                  }else if (it.data.is_agent && it.data.is_approved == "Pending") {
                      profileFragment.knowMore.visibility = View.GONE
                      profileFragment.bookingDetailCard.visibility = View.GONE
                      profileFragment.agentPending.visibility = View.VISIBLE
                  } else if (it.data.is_agent && it.data.is_approved == "Rejected") {
                      profileFragment.knowMore.visibility = View.VISIBLE
                      profileFragment.bookingDetailCard.visibility = View.GONE
                      profileFragment.agentPending.visibility = View.GONE
                  } else if(it.data.is_agent && it.data.is_approved == "Approved"){
                      profileFragment.knowMore.visibility = View.GONE
                      profileFragment.bookingDetailCard.visibility = View.VISIBLE
                      profileFragment.agentPending.visibility = View.GONE
                  }
              }
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
        savedInstanceState: Bundle?
    ): View {
        return profileFragment.root
    }
}