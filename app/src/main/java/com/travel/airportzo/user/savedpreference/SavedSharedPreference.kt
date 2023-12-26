package com.travel.airportzo.user.savedpreference

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.travel.airportzo.user.model.CustomisedColor
import com.travel.airportzo.user.model.ServiceTicketData

object SavedSharedPreference {

    private const val PREF_name = "name"
    private const val PREF_mobile = "mobile"
    private const val PREF_id = "token"
    private const val PREF_date = "date"
    private const val PREF_type = "type"
    private const val PREF_email = "email"
    private const val PREF_code = "code"
    private const val PREF_image = "image"
//    private const val FINAL="final"
    private fun getSharedPreferences(ctx: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    fun setUserData(
        ctx: Context,
        name: String?,
        mobile: String?,
        token: String?,
        date: String? = null,
        type: String? = null,
        email: String?,
        code: String?,
//        image: String?,
//        final:String,
    )
    {
        val editor = getSharedPreferences(ctx).edit()
        editor.putString(PREF_name, name)
        editor.putString(PREF_mobile, mobile)
        editor.putString(PREF_id, token)
        editor.putString(PREF_date, date)
        editor.putString(PREF_type, type)
        editor.putString(PREF_email, email)
        editor.putString(PREF_code, code)
//        editor.putString(PREF_image, image)
//        editor.putString(FINAL,final)
        editor.apply()
    }

    fun setImageUrl(context: Context, imageUrl: String){
        val editor = getSharedPreferences(context).edit()
        editor.putString(PREF_image, imageUrl)
        editor.apply()
    }

    fun getImageUrl(context: Context): String{
        return getSharedPreferences(context).getString(PREF_image, "").toString()
    }

    fun getUserData(ctx: Context): SharedDataClass {
        return SharedDataClass(
            getSharedPreferences(ctx).getString(PREF_name, ""),
            getSharedPreferences(ctx).getString(PREF_mobile, ""),
            getSharedPreferences(ctx).getString(PREF_id, ""),
            getSharedPreferences(ctx).getString(PREF_date, ""),
            getSharedPreferences(ctx).getString(PREF_type, ""),
            getSharedPreferences(ctx).getString(PREF_email, ""),
            getSharedPreferences(ctx).getString(PREF_code, ""),
//            getSharedPreferences(ctx).getString(PREF_image, "")
//            getSharedPreferences(ctx).getString(FINAL, ""),
        )
    }

    /** customized color*/

    fun setCustomColor(
            ctx:Context,
         header_logo : String,
         footer_logo : String,
         banner_image : String,
         poster_image : String,
         header_colour : String,
         header_text_colour : String,
         brand_colour : String,
         secondary_colour : String){
        val editor = getSharedPreferences(ctx).edit()
        editor.putString("headerLogo",header_logo)
        editor.putString("footerLogo",footer_logo)
        editor.putString("bannerImage",banner_image)
        editor.putString("posterImage",poster_image)
        editor.putString("headerColor",header_colour)
        editor.putString("headerTextColor",header_text_colour)
        editor.putString("brandColor",brand_colour)
        editor.putString("secondaryColor",secondary_colour)
        editor.apply()
    }


    fun getCustomColor(ctx: Context): CustomisedColor{
        return CustomisedColor(
            getSharedPreferences(ctx).getString("headerLogo",""),
            getSharedPreferences(ctx).getString("footerLogo",""),
            getSharedPreferences(ctx).getString("bannerImage",""),
            getSharedPreferences(ctx).getString("posterImage",""),
            getSharedPreferences(ctx).getString("headerColor",""),
            getSharedPreferences(ctx).getString("headerTextColor",""),
            getSharedPreferences(ctx).getString("brandColor",""),
            getSharedPreferences(ctx).getString("secondaryColor","")

        )
    }

    fun setSecurity(ctx: Context, enableSecurity: Boolean){
        val editor = getSharedPreferences(ctx).edit()
        editor.apply {
            putBoolean("EnableSecurity", enableSecurity)
            apply()
        }
    }

    fun getSecurity(ctx: Context): Boolean {
        return getSharedPreferences(ctx).getBoolean("EnableSecurity", false)
    }




}