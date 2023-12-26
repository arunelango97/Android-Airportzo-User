package com.travel.airportzo.user.ui.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.gson.JsonObject
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.SettingFragmentBinding
import com.travel.airportzo.user.model.CloseAccount
import com.travel.airportzo.user.model.CurrencyData
import com.travel.airportzo.user.network.ApiClient
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.ui.activity.MainActivity
import com.travel.airportzo.user.ui.adapter.SummaryLocationAdapter
import com.travel.airportzo.user.ui.base.BaseFragment
import com.travel.airportzo.user.utils.setOnDebounceListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SettingFragment : BaseFragment() {

    private val settingFragment by lazy { SettingFragmentBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** brand color */

        settingFragment.rootlayout.setBackgroundColor(Color.parseColor(activity?.let {
            SavedSharedPreference.getCustomColor(
                it
            ).brand_colour
        }))

        settingFragment.settingback.setOnDebounceListener {
            onBackPressed()
        }

        if (this.let { SavedSharedPreference.getUserData(requireContext()).token?.isEmpty() }!!) {
            settingFragment.logout.visibility = View.GONE
            settingFragment.appCompatTextViewLogut.visibility = View.GONE
        } else {
            settingFragment.logout.visibility = View.VISIBLE
            settingFragment.appCompatTextViewLogut.visibility = View.VISIBLE
        }

        settingFragment.logoutLayout.setOnDebounceListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder.setMessage("Are you sure you want to Logout?")
                .setCancelable(false)
                .setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
                .setPositiveButton("Logout", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                    this.let { it1 ->
                        SavedSharedPreference.setUserData(
                            requireActivity(), "", "",
                            "",
                            "", "", "", ""
                        )
                    }
                    launchActivity(MainActivity::class.java)
                    activity?.finishAffinity()
                })
                .show()
        }

        settingFragment.policyLayout.setOnDebounceListener {
            findNavController().navigate(R.id.action_navigation_setting_to_policiesFragment)
        }

        settingFragment.aboutLayout.setOnDebounceListener {
            findNavController().navigate(R.id.action_navigation_setting_to_aboutFragment)
        }

        settingFragment.accountLayout.setOnDebounceListener {
            findNavController().navigate(R.id.action_navigation_setting_to_manageAccountFragment)
        }



        settingFragment.closeAccountLayout.setOnDebounceListener {
            closeAccount?.show()

            val closeAccountReason = closeAccount?.findViewById<AppCompatEditText>(R.id.addGstCompany)
            val closeAccountCheck = closeAccount?.findViewById<CheckBox>(R.id.aServiceCheckbox)
            val closeAccountButton = closeAccount?.findViewById<MaterialButton>(R.id.closeAccountBtn)

            closeAccountButton?.setOnDebounceListener {
                if (closeAccountReason?.text?.isEmpty() == true){
                    Toast.makeText(context, "Please Add Reason", Toast.LENGTH_SHORT).show()
                }else if (closeAccountCheck?.isChecked == false){
                    Toast.makeText(context, "CheckBox is Not Choose", Toast.LENGTH_SHORT).show()
                }else {
                    val reason = closeAccountReason?.text.toString()
                    val token =   SavedSharedPreference.getUserData(requireContext()).token

                    val jsonObject = JsonObject().apply {
                        addProperty("user_token", token)
                        addProperty("reason", reason)
                    }

                    Log.d("TAG", "onCreate: $jsonObject")

                    ApiClient.APIinterface().closeAccount(jsonObject)
                        .enqueue(object : Callback<CloseAccount> {
                            override fun onResponse(
                                call: Call<CloseAccount>,
                                response: Response<CloseAccount>
                            ) {
                                if (response.isSuccessful) {
                                    SavedSharedPreference.setUserData(
                                        requireActivity(), "", "",
                                        "",
                                        "", "", "", ""
                                    )
                                    findNavController().navigate(R.id.action_navigation_setting_to_navigation_home)
                                    closeAccount?.dismiss()
                                }else{
                                    Toast.makeText(requireContext(), "currency fail", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }

                            override fun onFailure(call: Call<CloseAccount>, t: Throwable) {

                            }
                        })
                }
            }
        }

        /** security */
        settingFragment.securityLayout.setOnDebounceListener {
            securityEnabled?.show()
            val enableSwitch = securityEnabled?.findViewById<SwitchMaterial>(R.id.btSecurityEnableSwitch)
            /** this onclick code enable to switch  color change  */
            enableSwitch?.setOnCheckedChangeListener{buttonView,isChecked ->
                if (isChecked){
                    enableSwitch.trackTintList = context?.let { it1 ->
                        ContextCompat.getColor(
                            it1,R.color.blue)
                    }?.let { it2 -> ColorStateList.valueOf(it2) }
                }
                else{
                    enableSwitch.trackTintList = context?.let { it1 ->
                        ContextCompat.getColor(
                            it1,R.color.greyText)
                    }?.let { it2 -> ColorStateList.valueOf(it2) }
                }
            }
        }

        /** Choose Language */
        settingFragment.languageLayout.setOnDebounceListener {

           /* chooseLanguage?.findViewById<Button>(R.id.english)?.backgroundTintList =
                context?.let { it1 -> ContextCompat.getColorStateList(it1,R.color.blue) }
            chooseLanguage?.findViewById<Button>(R.id.english)?.setTextColor(context?.let { it1 ->
                ContextCompat.getColorStateList(
                    it1,R.color.white)
            })
            chooseLanguage?.findViewById<Button>(R.id.tamil)?.backgroundTintList =
                context?.let { it1 -> ContextCompat.getColorStateList(it1,R.color.white) }
            chooseLanguage?.findViewById<Button>(R.id.tamil)?.setTextColor(context?.let { it1 ->
                ContextCompat.getColorStateList(
                    it1,R.color.black)
            })
            chooseLanguage?.findViewById<Button>(R.id.hindi)?.backgroundTintList =
                context?.let { it1 -> ContextCompat.getColorStateList(it1,R.color.white) }
            chooseLanguage?.findViewById<Button>(R.id.hindi)?.setTextColor(context?.let { it1 ->
                ContextCompat.getColorStateList(
                    it1,R.color.black)
            })*/
            chooseLanguage?.show()
        }

        chooseLanguage?.findViewById<Button>(R.id.language_button)?.setOnDebounceListener {
            chooseLanguage?.dismiss()
        }

        settingFragment.currencyLayout.setOnDebounceListener {
            chooseCurrency?.show()
        }
    }

    /** security Bottom Sheet*/
    private val securityEnabled by lazy {
        context?.let {
            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
                setContentView(layoutInflater.inflate(R.layout.bottomsheet_security, null))
                val securityEnableSwitch = findViewById<SwitchMaterial>(R.id.btSecurityEnableSwitch)

                securityEnableSwitch?.isChecked = SavedSharedPreference.getSecurity(context)


                securityEnableSwitch?.setOnClickListener {
                    if (securityEnableSwitch.isChecked) {
                      SavedSharedPreference.setSecurity(context,true)
                    } else {
                        SavedSharedPreference.setSecurity(context,false)
                    }
                }
                setCancelable(true)
                setOnShowListener {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    }


    /** Choose Language Bottom Sheet*/
    private val chooseLanguage by lazy {
        context?.let {
            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
                setContentView(
                    layoutInflater.inflate(
                        R.layout.bottomsheet_language, null
                    )
                )

                /** brand color */
                val currencyBtn = findViewById<MaterialButton>(R.id.language_button)
                currencyBtn?.setBackgroundColor(Color.parseColor(activity?.let { it1 ->
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

    /** Choose Currency Bottom Sheet*/
    private val chooseCurrency by lazy {
        context?.let {
            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
                setContentView(
                    layoutInflater.inflate(
                        R.layout.bottomsheet_currency, null
                    )
                )

                /** brand color */
                val langBtn = findViewById<MaterialButton>(R.id.language_button)
                langBtn?.setBackgroundColor(Color.parseColor(activity?.let { it1 ->
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

    private val closeAccount by lazy {
        context?.let {
            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
                setContentView(
                    layoutInflater.inflate(
                        R.layout.bottomsheet_closeaccount, null
                    )
                )

                /** brand color */
                val langBtn = findViewById<MaterialButton>(R.id.language_button)
                langBtn?.setBackgroundColor(Color.parseColor(activity?.let { it1 ->
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



    private fun onBackPressed() {
        Navigation.findNavController(requireView()).popBackStack()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return settingFragment.root
    }

}

