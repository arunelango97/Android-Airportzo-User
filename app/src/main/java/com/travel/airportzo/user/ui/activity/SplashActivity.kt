package com.travel.airportzo.user.ui.activity

import UserDynamicDataColor
import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.travel.airportzo.user.databinding.SplashActivityBinding
import com.travel.airportzo.user.model.ManageAccountData
import com.travel.airportzo.user.network.ApiClient
import com.travel.airportzo.user.network.ApiResult
import com.travel.airportzo.user.network.NoInternetActivity
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.ui.base.BaseActivity
import kotlinx.coroutines.launch
import retrofit2.Call
import java.util.ArrayList
import java.util.concurrent.Executor

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    private val splashscreen by lazy { SplashActivityBinding.inflate(layoutInflater) }
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        super.onCreate(savedInstanceState)
        setContentView(splashscreen.root)
        Log.d("Debug", "I'm the best")

        val w: Window = window
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        userDynamicColor()
//        checkToken()

        /** biometric */
        if (SavedSharedPreference.getSecurity(this)){
            if (toCheckBioMetric()){
                showBioMetric()
            }else{
                Log.d("Debug", "Biometric authentication is not available.")
            }
        }else{
            Log.d("TAG", "Biometric authentication is not available.")
            checkToken()
        }
    }


    private fun toCheckBioMetric():Boolean{
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.")
                return true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.e("MY_APP_TAG", "No biometric features available on this device.")
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Prompts the user to create credentials that your app accepts.
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                }
                resultLauncher.launch(enrollIntent)
                return false
            }
        }
        return true
    }
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    Toast.makeText(this, "biometric", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun showBioMetric(){
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    showDialog()
                  /*  Toast.makeText(applicationContext, "Authentication error: $errString", Toast.LENGTH_SHORT).show()*/
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    checkToken()
                    Toast.makeText(applicationContext, "Authentication succeeded!", Toast.LENGTH_SHORT).show()

                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()
        biometricPrompt.authenticate(promptInfo)
    }
    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Airportzo is Locked")
        builder.setMessage("Authentication is required to access the Airporzo app")
            .setPositiveButton("Unlock now") { _, _ ->
                showBioMetric()
            }
        builder.setCancelable(false)
        builder.create()
            .show()
    }


    private fun checkToken() {
            if (onBoardingFinished()){
                Handler(Looper.getMainLooper()).postDelayed({ this.startActivity(Intent(this, MainActivity::class.java))
                    finish()}, 2000)
                Log.d("checkToken", "checkToken: MainActivity ")
            }else{
                Handler(Looper.getMainLooper()).postDelayed({ this.startActivity(Intent(this, LaunchActivity::class.java))
                    finish()}, 2000)
                Log.d("checkToken", "checkToken: LaunchActivity ")
            }
        }

    private fun onBoardingFinished(): Boolean {
        val sharedPref =getSharedPreferences("onBoarding",Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished",false)
    }


    private fun userDynamicColor(){
        ApiClient.APIinterface().getDetails().enqueue(object : retrofit2.Callback<UserDynamicDataColor> {
            override fun onResponse(
                call: Call<UserDynamicDataColor>,
                response: retrofit2.Response<UserDynamicDataColor>
            ) {
                if (response.isSuccessful){
                    Log.d("TAG", "isSuccessful: LaunchActivity ")
                        response.body()?.data?.header_logo?.let { it1 ->
                            response.body()?.data?.footer_logo?.let { it2 ->
                                response.body()?.data?.banner_image?.let { it3 ->
                                    response.body()?.data?.poster_image?.let { it4 ->
                                        response.body()?.data?.header_colour?.let { it5 ->
                                            response.body()?.data?.header_text_colour?.let { it6 ->
                                                response.body()?.data?.brand_colour?.let { it7 ->
                                                    response.body()?.data?.secondary_colour?.let { it8 ->
                                                        SavedSharedPreference.setCustomColor(
                                                            this@SplashActivity, it1, it2, it3, it4, it5, it6, it7, it8)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    checkToken()
                }else{
                    Toast.makeText(this@SplashActivity, "fail", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserDynamicDataColor>, t: Throwable) {
                Log.d("onFailure",t.message.toString())
            }
        })

    }





}

