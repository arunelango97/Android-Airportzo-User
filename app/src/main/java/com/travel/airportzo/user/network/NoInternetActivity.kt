package com.travel.airportzo.user.network


import android.os.Bundle
import android.widget.Toast
import com.travel.airportzo.user.databinding.NointernetActivityBinding
import com.travel.airportzo.user.utils.setOnDebounceListener
import com.travel.airportzo.user.ui.base.BaseActivity


class NoInternetActivity :  BaseActivity() {
//    lateinit var results: MutableList<ScanResult>
//    lateinit var wifiManager: WifiManager
//    private  var wifiList: ArrayList<String> = ArrayList()
//    private val wifiAdapter by lazy { this?.let { WifiAdapter(it,wifiList) } }

    private val nointernetActivity by lazy { NointernetActivityBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(nointernetActivity.root)

        nointernetActivity.scanBtn.setOnDebounceListener {
            internet()
        }

        // wifi scan
//        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//
//
//        if (!wifiManager!!.isWifiEnabled) {
//            Toast.makeText(this, "WiFi is disabled ... We need to enable it", Toast.LENGTH_LONG).show()
//            wifiManager!!.isWifiEnabled = true
//        }
//
//        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
//        ) {
//            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 0)
//        } else {
//            scanWifi()
//        }
//
    }

    private fun internet() {
        if (isNetworkConnected(applicationContext)) {
            finish()
        } else {
            Toast.makeText(applicationContext, "Please check your connection", Toast.LENGTH_SHORT).show()
        }
    }
//
//    private fun scanWifi() {
//        registerReceiver(wifiReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
//        wifiManager!!.startScan()
//        Toast.makeText(this, "Scanning WiFi ...", Toast.LENGTH_SHORT).show()
//    }
//
//    var wifiReceiver: BroadcastReceiver = object : BroadcastReceiver() {
//        @SuppressLint("NotifyDataSetChanged")
//        override fun onReceive(context: Context, intent: Intent) {
//            results = wifiManager!!.scanResults
//            for (scanResult in results!!) {
//                var wifi_ssid=scanResult.SSID
//
//                wifiList.add(wifi_ssid)
////               adapter.notifyDataSetChanged()
//
//            }
//        }
//    }
}