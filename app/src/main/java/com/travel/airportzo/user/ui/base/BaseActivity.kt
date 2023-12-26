package com.travel.airportzo.user.ui.base


import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.travel.airportzo.user.R
import com.travel.airportzo.user.network.ApiClient
import com.travel.airportzo.user.network.NetworkHelper
import com.travel.airportzo.user.viewmodel.MainRepository
import com.travel.airportzo.user.viewmodel.MainViewModel
import com.travel.airportzo.user.viewmodel.ViewModelFactory


abstract class BaseActivity : AppCompatActivity() {
    lateinit var viewModel: MainViewModel
    private var dialog: AlertDialog? = null
    var isRetry = MutableLiveData<Boolean>()
    private lateinit var networkHelper: NetworkHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, ViewModelFactory(MainRepository(ApiClient.apis), Application()))[MainViewModel::class.java]



    }



  /*  override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        when (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                // Light mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                // Dark mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }

    }*/


    fun alertToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    fun launchActivity(
        javaClass: Class<out AppCompatActivity>,
        bundle: Bundle? = null,
        isClearPreviousTask: Boolean = false
    ) {
        Intent(this, javaClass).apply {
            if (bundle != null)
                putExtras(bundle)

            if (isClearPreviousTask)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(this)
        }
    }

    fun errors(title: String, message: String) {
        if (message == getString(R.string.nointernet)) {
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)
                .setBackgroundTint(ContextCompat.getColor(baseContext, R.color.batchColor))
                .setTextColor(ContextCompat.getColor(baseContext, R.color.white)).setAction(getString(R.string.ok)) {
                    isRetry.value = true
                }.show()
        } else {
//            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)
//                .setBackgroundTint(resources.getColor(R.color.white))
//                .setTextColor(resources.getColor(R.color.black)).setAction(getString(R.string.ok)) {
//                }.show()

            val alert = AlertDialog.Builder(this).setMessage(message).setCancelable(false)
                .setPositiveButton("Proceed", DialogInterface.OnClickListener {
                        dialog, id -> dialog.dismiss()
                }.apply {
                    titleColor = ContextCompat.getColor(applicationContext, R.color.black)
                }).create()
            alert.setTitle(title)
            alert.show()
        }
    }


    fun isNetworkConnected(context: Context) : Boolean {
        networkHelper = NetworkHelper(context)
        return networkHelper.isNetworkConnected()
    }

}

