package com.travel.airportzo.user.ui.activity


import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.travel.airportzo.user.R
import com.travel.airportzo.user.ui.base.BaseActivity
import com.travel.airportzo.user.ui.adapter.ImageBannerAdapter
import com.travel.airportzo.user.utils.Utility.Alertdialog
import com.travel.airportzo.user.databinding.LaunchActivityBinding
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.utils.setOnDebounceListener


class LaunchActivity : BaseActivity() {

    private var language: String? = ""
    private var currency: String? = ""
    private var banners = ArrayList<String>()
    private val sliderHandler = Handler(Looper.getMainLooper())
    private var currentPage : Int? = 0
    private var bannersize : Int? = 0
    private val launchScreen by lazy { LaunchActivityBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(launchScreen.root)


        launchScreen.english.setOnDebounceListener {
            language = "English"
            launchScreen.english.backgroundTintList = ContextCompat.getColorStateList(this, R.color.blue)
            launchScreen.english.setTextColor(ContextCompat.getColorStateList(this, R.color.white))
            launchScreen.tamil.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
            launchScreen.tamil.setTextColor(ContextCompat.getColorStateList(this, R.color.black))
            launchScreen.hindi.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
            launchScreen.hindi.setTextColor(ContextCompat.getColorStateList(this, R.color.black))
        }

        launchScreen.tamil.setOnDebounceListener {
            language = "Tamil"
            launchScreen.tamil.backgroundTintList = ContextCompat.getColorStateList(this, R.color.blue)
            launchScreen.tamil.setTextColor(ContextCompat.getColorStateList(this, R.color.white))
            launchScreen.english.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
            launchScreen.english.setTextColor(ContextCompat.getColorStateList(this, R.color.black))
            launchScreen.hindi.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
            launchScreen.hindi.setTextColor(ContextCompat.getColorStateList(this, R.color.black))
        }

        launchScreen.hindi.setOnDebounceListener {
            language = "Hindi"
            launchScreen.hindi.backgroundTintList = ContextCompat.getColorStateList(this, R.color.blue)
            launchScreen.hindi.setTextColor(ContextCompat.getColorStateList(this, R.color.white))
            launchScreen.english.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
            launchScreen.english.setTextColor(ContextCompat.getColorStateList(this, R.color.black))
            launchScreen.tamil.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
            launchScreen.tamil.setTextColor(ContextCompat.getColorStateList(this, R.color.black))
        }





        launchScreen.indian.setOnDebounceListener {
            currency = "INR(₹)"
            launchScreen.indian.backgroundTintList = ContextCompat.getColorStateList(this, R.color.blue)
            launchScreen.indian.setTextColor(ContextCompat.getColorStateList(this, R.color.white))
            launchScreen.usa.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
            launchScreen.usa.setTextColor(ContextCompat.getColorStateList(this, R.color.black))
        }

        launchScreen.usa.setOnDebounceListener {
            currency = "USD($)"
            launchScreen.usa.backgroundTintList = ContextCompat.getColorStateList(this, R.color.blue)
            launchScreen.usa.setTextColor(ContextCompat.getColorStateList(this, R.color.white))
            launchScreen.indian.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
            launchScreen.indian.setTextColor(ContextCompat.getColorStateList(this, R.color.black))
        }

        val brandColor = SavedSharedPreference.getCustomColor(this).brand_colour
        if (!brandColor.isNullOrEmpty()) {
            launchScreen.bookService.setBackgroundColor(Color.parseColor(brandColor))

            val colorStateList = ColorStateList.valueOf(Color.parseColor(brandColor))
            launchScreen.next.backgroundTintList = colorStateList
        }


        launchScreen.bookService.setOnDebounceListener {
            if (currency?.isEmpty() == true){
                Alertdialog("Please Choose Currency")
            }else{
                onBoardingFinished()
                this.startActivity(Intent(this, MainActivity::class.java))
                finish()

            }
        }


        launchScreen.next.setOnDebounceListener {
            if (language?.isEmpty() == true){
                Alertdialog("Please Choose Language")
            }else{

                launchScreen.firstLayout.visibility = View.INVISIBLE
                launchScreen.secondLayout.visibility = View.VISIBLE
                launchScreen.next.visibility = View.GONE


            }
        }

        launchScreen.back.setOnDebounceListener {
                launchScreen.firstLayout.visibility = View.VISIBLE
                launchScreen.secondLayout.visibility = View.INVISIBLE
                launchScreen.next.visibility = View.VISIBLE
        }


        banners.add("https://d25t7bbtlnltob.cloudfront.net/service_provider_company/documents/1702977711592.jpg")
        banners.add("https://d25t7bbtlnltob.cloudfront.net/service_provider_company/documents/1702977738670.jpg")
        banners.add("https://d25t7bbtlnltob.cloudfront.net/service_provider_company/documents/1703159589096.jpg")
        banners.add("https://d25t7bbtlnltob.cloudfront.net/service_provider_company/documents/1702977777290.jpg")
        banners.add("https://d25t7bbtlnltob.cloudfront.net/service_provider_company/documents/1702977795401.jpg")
        banners.add("https://d25t7bbtlnltob.cloudfront.net/service_provider_company/documents/1702977804642.jpg")
        launchScreen.banners.adapter = ImageBannerAdapter(applicationContext, banners,::itemList)
        launchScreen.banners.orientation = ViewPager2.ORIENTATION_HORIZONTAL


        launchScreen.segmentedProgressbar.setSegmentCount(banners.size)



        launchScreen.segmentedProgressbar.playSegment(3000)


        launchScreen.banners.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                launchScreen.segmentedProgressbar.setCompletedSegments(position)
                launchScreen.segmentedProgressbar.playSegment(3000)
                sliderHandler.removeCallbacks(slideRunnable)
                sliderHandler.postDelayed(slideRunnable, 3000)

                if (position == 0){
                    launchScreen.textView7.text = "Baggage Wrapping"
                }else if (position == 1){
                    launchScreen.textView7.text = "Car SPA"
                }else if (position == 2){
                    launchScreen.textView7.text = "Lounge"
                }else if (position == 3){
                    launchScreen.textView7.text = "Meet And Assist"
                }else if (position == 4){
                    launchScreen.textView7.text = "Nap Room"
                }else if (position == 5){
                    launchScreen.textView7.text = "Porter"
                }

            }
        })
    }

    private val slideRunnable =
        Runnable {
            if (currentPage == bannersize?.minus(1)) {
                launchScreen.banners.currentItem = 0
            } else {
                launchScreen. banners.currentItem = launchScreen.banners.currentItem + 1
            }
        }

    private fun itemList(s: String) {
       Alertdialog("image")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    private fun onBoardingFinished(){
        val sharedPref =getSharedPreferences("onBoarding",Context.MODE_PRIVATE)
        val editor =sharedPref.edit()
        editor.putBoolean("Finished",true)
        editor.apply()
    }
}