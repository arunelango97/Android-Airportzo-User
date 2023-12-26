package com.travel.airportzo.user.ui.activity


import android.annotation.SuppressLint
import android.content.*
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.badge.BadgeDrawable.*
import com.google.gson.JsonObject
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.MainActivityBinding
import com.travel.airportzo.user.model.UpdateprofileData
import com.travel.airportzo.user.network.ApiResult
import com.travel.airportzo.user.network.NoInternetActivity
import com.travel.airportzo.user.network.NotificationListener
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.ui.base.BaseActivity
import com.travel.airportzo.user.ui.fragments.PackageServiceFragment
import kotlinx.coroutines.launch


class MainActivity : BaseActivity() {

    //TODO: All variables must have access modifiers
    //TODO: all variables must use its proper declaration like val or var


    private val myNavHostFragment by lazy { supportFragmentManager.findFragmentById(com.travel.airportzo.user.R.id.fragment) as NavHostFragment }

    private val inflater by lazy { myNavHostFragment.navController.navInflater }

    private val navController by lazy {
        Navigation.findNavController(
            this,
            R.id.fragment
        )
    }

    var agent: Boolean = false
    private val mainActivity by lazy { MainActivityBinding.inflate(layoutInflater) }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainActivity.root)



        /** brand color top space color*/
        // Get the current Window object
        val window: Window = window
        val brandColor = SavedSharedPreference.getCustomColor(this).brand_colour
        // Change the status bar color dynamically
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!brandColor.isNullOrEmpty()) {
                window.statusBarColor = Color.parseColor(brandColor)
            }
        }

        /** brand color nav active bar color*/
        val colorString = SavedSharedPreference.getCustomColor(this).brand_colour
        val color = Color.parseColor(colorString)
        val selectorDrawable: Drawable? = AppCompatResources.getDrawable(this, R.drawable.activity_main_tab_background)
        selectorDrawable?.setTint(color)
        mainActivity.bottomNavigation.itemBackground = selectorDrawable

        agentCall()
        badge(PackageServiceFragment.totalCount)

        initBottomNavigationView()

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(openCartReceiver, IntentFilter("open-cart"))

        /** brand color*/
        val colorStateList = ColorStateList.valueOf(Color.parseColor(SavedSharedPreference.getCustomColor(this,).brand_colour))
        mainActivity.bottomNavigation.itemTextColor = colorStateList
        mainActivity.bottomNavigation.itemIconTintList = colorStateList

       /* val activeIndicatorBarDrawable = bottom_navigation_view.itemBackground as GradientDrawable
        activeIndicatorBarDrawable.setColor(ContextCompat.getColor(this, R.color.red))*/
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(openCartReceiver);
    }

    fun agentCall() {
        val token = SavedSharedPreference.getUserData(this).token
        val jsonObject = JsonObject().apply {
            addProperty("user_token", token)
        }
        if (isNetworkConnected(this)) {
            lifecycleScope.launch {
            viewModel.updateprofile(jsonObject = jsonObject).observe(this@MainActivity, updatedProfile)
            }
        } else {
            startActivity(Intent(this, NoInternetActivity::class.java))
        }

    }

    private val updatedProfile = Observer<ApiResult<UpdateprofileData>> {
        when (it) {
            is ApiResult.Success -> {
                agent = it.data.is_agent && it.data.is_approved == "Approved"
            }
            is ApiResult.Error -> {
//                errors("Error", it.message)
            }
            else -> {}
        }
    }

    fun badge(totalCount: ArrayList<Int>) {
        mainActivity.bottomNavigation.getOrCreateBadge(R.id.cartmenu).apply {
            backgroundColor = resources.getColor(R.color.redButton)
            badgeTextColor = resources.getColor(R.color.white)
//            val menuItemId: Int = bottomNavigationView.getMenu().getItem(0).getItemId()
//            val badge: BadgeDrawable = bottomNavigationView.getOrCreateBadge(menuItemId)
//            badge.number = 2
            badgeGravity = TOP_END
            maxCharacterCount = 10
            number = totalCount.size
            if (PackageServiceFragment.totalCount.size == 0) {
                isVisible = false
            } else if (PackageServiceFragment.totalCount.size > 0) {
                isVisible = true
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun initBottomNavigationView() {
        mainActivity.apply {
            bottomNavigation.itemIconTintList = null
            bottomNavigation.itemTextColor
            bottomNavigation.setOnItemSelectedListener {

                when (it.itemId) {
                    R.id.homemenu -> {
                        if (bottomNavigation.selectedItemId != it.itemId) {

                            val graph = inflater.inflate(R.navigation.navigation_home)
                            myNavHostFragment.navController.graph = graph
                            return@setOnItemSelectedListener true
                        }
                    }
                    R.id.cartmenu -> {
                        if (bottomNavigation.selectedItemId != it.itemId) {
                            val graph = inflater.inflate(R.navigation.navigation_cart)
                            myNavHostFragment.navController.graph = graph
                            return@setOnItemSelectedListener true
                        }
                    }
                    R.id.bookingmenu -> {
                        if (agent) {
                            if (bottomNavigation.selectedItemId != it.itemId) {
                                val graph = inflater.inflate(R.navigation.navigation_agentbooking)
                                myNavHostFragment.navController.graph = graph
                                return@setOnItemSelectedListener true
                            }
                        } else {
                            if (bottomNavigation.selectedItemId != it.itemId) {
                                val graph = inflater.inflate(R.navigation.navigation_booking)
                                myNavHostFragment.navController.graph = graph
                                return@setOnItemSelectedListener true
                            }
                        }
                    }
//                    R.id.notificationmenu -> {
//                        if (bottomNavigation.selectedItemId != it.itemId) {
//                            val graph = inflater.inflate(R.navigation.navigation_notification)
//                            myNavHostFragment.navController.graph = graph
//                            return@setOnItemSelectedListener true
//                        }
//                    }
                }
                return@setOnItemSelectedListener false
            }

        }
    }


    private val openCartReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val message = intent.getStringExtra("message")
            if (message == "open_cart") {
                mainActivity.bottomNavigation.selectedItemId = R.id.cartmenu
            } else if (message == "gotobooking"){
                mainActivity.bottomNavigation.selectedItemId = R.id.bookingmenu
            }
        }
    }

    override fun onBackPressed() {

        // navigation home
        if (myNavHostFragment.navController.graph.startDestDisplayName == inflater.inflate(
                R.navigation.navigation_home
            ).startDestDisplayName
        ) {
            if (navController.currentDestination?.id == R.id.navigation_home) {
                super.onBackPressed()
            } else if (navController.currentDestination?.id == R.id.navigation_chooseservice) {
                val graph = inflater.inflate(R.navigation.navigation_home)
                myNavHostFragment.navController.graph = graph
            } else if (navController.currentDestination?.id == R.id.navigation_profile) {
                val graph = inflater.inflate((R.navigation.navigation_home))
                myNavHostFragment.navController.graph = graph
            } else if (navController.currentDestination?.id == R.id.navigation_service) {
                val graph = inflater.inflate((R.navigation.navigation_home))
                myNavHostFragment.navController.graph = graph
            }

            //Personal Fragment

            else if (navController.currentDestination?.id == R.id.personalFragment) {
                myNavHostFragment.navController.navigate(R.id.navigation_profile)
            } else if (navController.currentDestination?.id == R.id.personalFragment) {
                myNavHostFragment.navController.navigate(R.id.updateProfileFragment)
            } else if (navController.currentDestination?.id == R.id.personalFragment) {
                myNavHostFragment.navController.navigate(R.id.savedGstFragment)
            } else if (navController.currentDestination?.id == R.id.personalFragment) {
                myNavHostFragment.navController.navigate(R.id.savedPassengerFragment)
            } else if (navController.currentDestination?.id == R.id.personalFragment) {
                myNavHostFragment.navController.navigate(R.id.savedBankFragment)
            }

            //Agent Fragment

            else if (navController.currentDestination?.id == R.id.becomeAnAgentFragment) {
                myNavHostFragment.navController.navigate(R.id.navigation_profile)
            }

            //Agent Dashboard
            else if (navController.currentDestination?.id == R.id.agentDashBoardFragment) {
                myNavHostFragment.navController.navigate(R.id.navigation_profile)
            }

            //Setting Fragment
            else if (navController.currentDestination?.id == R.id.navigation_setting) {
                myNavHostFragment.navController.navigate(R.id.navigation_profile)
            } else if (navController.currentDestination?.id == R.id.policiesFragment) {
                myNavHostFragment.navController.navigate(R.id.navigation_setting)
            }

        }

        //navigation cart
        else if (myNavHostFragment.navController.graph.startDestDisplayName == inflater.inflate(
                R.navigation.navigation_cart
            ).startDestDisplayName
        ) {

            if ( navController.currentDestination?.id == R.id.navigation_cart) {
                val graph = inflater.inflate(R.navigation.navigation_home)
                myNavHostFragment.navController.graph = graph
                mainActivity.bottomNavigation.selectedItemId = R.id.homemenu

            } else if (navController.currentDestination?.id == R.id.navigation_summary) {
                val graph = inflater.inflate(R.navigation.navigation_cart)
                myNavHostFragment.navController.graph = graph
            } else if (navController.currentDestination?.id == R.id.navigation_checkout) {
                myNavHostFragment.navController.navigate(R.id.navigation_summary)
            } else if (navController.currentDestination?.id == R.id.cartPoliciesFragment) {
                myNavHostFragment.navController.navigate(R.id.navigation_checkout)
            }

        }


        //navigation booking
        else if (myNavHostFragment.navController.graph.startDestDisplayName == inflater.inflate(
                R.navigation.navigation_booking
            ).startDestDisplayName
        ) {
            if (navController.currentDestination?.id == R.id.navigation_booking) {
                val graph = inflater.inflate(R.navigation.navigation_home)
                myNavHostFragment.navController.graph = graph
                mainActivity.bottomNavigation.selectedItemId = R.id.homemenu
            } else if (navController.currentDestination?.id == R.id.navigation_booking_details) {
                val graph = inflater.inflate(R.navigation.navigation_booking)
                myNavHostFragment.navController.graph = graph
            }
        }

        //navigation notification
//        else if (myNavHostFragment.navController.graph.startDestDisplayName == inflater.inflate(R.navigation.navigation_notification).startDestDisplayName) {
//            if (navController.currentDestination?.id == R.id.navigation_notification) {
//                val graph = inflater.inflate(R.navigation.navigation_home)
//                myNavHostFragment.navController.graph = graph
//                mainActivity.bottomNavigation.selectedItemId = R.id.homemenu
//            }
//        }

    }


}