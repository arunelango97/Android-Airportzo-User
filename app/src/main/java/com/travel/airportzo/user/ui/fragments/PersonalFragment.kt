package com.travel.airportzo.user.ui.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.PersonalFragmentBinding
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.ui.base.BaseFragment
import com.travel.airportzo.user.utils.setOnDebounceListener


class PersonalFragment : BaseFragment() {

    private val personalFragment by lazy { PersonalFragmentBinding.inflate(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        /** brand color */

        personalFragment.rootLayout.setBackgroundColor(Color.parseColor(activity?.let {
            SavedSharedPreference.getCustomColor(
                it
            ).brand_colour
        }))

        val directAgent : Boolean = arguments?.getBoolean("agent")!!

        val directApproved : String = arguments?.getString("approved")!!

         if (directAgent && directApproved=="Approved") {
             personalFragment.saveBank.visibility=View.VISIBLE
         }else{
             personalFragment.saveBank.visibility=View.GONE
         }

        personalFragment.personalBack.setOnDebounceListener {
            onBackPressed()
        }


        personalFragment.updateLayout.setOnDebounceListener {
            findNavController().navigate(R.id.action_personalFragment_to_updateProfileFragment)
        }

        personalFragment.savedGst.setOnDebounceListener {
            findNavController().navigate(R.id.action_personalFragment_to_savedGstFragment)
        }

        personalFragment.savePassenger.setOnDebounceListener {
            findNavController().navigate(R.id.action_personalFragment_to_savedPassengerFragment)
        }

        personalFragment.saveBank.setOnDebounceListener {
            findNavController().navigate(R.id. action_personalFragment_to_savedBankFragment)
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
        return personalFragment.root
    }

}