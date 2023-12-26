package com.travel.airportzo.user.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.JsonObject
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.UpdateProfileFragmentBinding
import com.travel.airportzo.user.model.AwsProfile
import com.travel.airportzo.user.model.UpdateprofileData
import com.travel.airportzo.user.network.ApiResult
import com.travel.airportzo.user.network.NoInternetActivity
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.ui.activity.MainActivity
import com.travel.airportzo.user.utils.setOnDebounceListener
import com.travel.airportzo.user.ui.base.BaseFragment
import com.travel.airportzo.user.utils.AWSProfileUploader
import kotlinx.coroutines.launch
import java.io.File


class UpdateProfileFragment : BaseFragment(), AWSProfileUploader.AWSProfileUploadListener {

private val updateProfileFragment by lazy { UpdateProfileFragmentBinding.inflate(layoutInflater) }

    private val beforeImageData: ArrayList<String> by lazy { arrayListOf() }

    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+"
    private val types = arrayOf("Mr.","Mrs.","Ms.")

    private  var passengerName :String = ""
    private  var passengerNumber :String = ""
    private  var passengerEmail :String = ""
    private var passengerTitle:String =""
    private var passengerCountry:String = ""
    private var passengerHead:String =""
    private val requestPermission = 100

    private var uploadCloneImage : String = ""
    private var updateImage:String=""
    private var imagePathName: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** Brand color for button */
        updateProfileFragment.updateProfileButton.setBackgroundColor(Color.parseColor(activity?.let {
            SavedSharedPreference.getCustomColor(
                it
            ).secondary_colour
        }))

        /** brand color */

        updateProfileFragment.rootLayout.setBackgroundColor(Color.parseColor(activity?.let {
            SavedSharedPreference.getCustomColor(
                it
            ).brand_colour
        }))




        updateProfile()

         updateProfileFragment.updateProfileBack.setOnDebounceListener {
            onBackPressed()
        }

        updateProfileFragment.updateText.setOnDebounceListener {
            beforeImageData.clear()
            pickImage()
        }

        updateProfileFragment.apply {
            updateNameSpinner.adapter=context.let {
               context?.let {
                   ArrayAdapter(
                  it,
                   androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                types)
               }
            }as SpinnerAdapter


            updateNameSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    passengerHead = parent?.getItemAtPosition(position).toString()
                }
            }

            updateProfileButton.setOnDebounceListener {
                if (updateNameEdit.text.toString().isEmpty()) {
                    Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT).show()
                } else if (updateNumberEdittext.text.toString().isEmpty()) {
                    Toast.makeText(context, "Please enter your number", Toast.LENGTH_SHORT).show()
                } else if (updateEmailEdittext.text.toString().isEmpty()) {
                    Toast.makeText(context, "Please enter your Email", Toast.LENGTH_SHORT).show()
                } else if (!updateEmailEdittext.text.toString().matches(emailPattern.toRegex())) {
                    Toast.makeText(context, "Please enter valid email address", Toast.LENGTH_SHORT).show()
                }else{
                    passengerTitle = updateNameSpinner.selectedItem.toString()
                    passengerName = updateNameEdit.text.toString()
                    passengerCountry = updateCode.selectedCountryCodeWithPlus.toString()
                    passengerNumber = updateNumberEdittext.text.toString()
                    passengerEmail = updateEmailEdittext.text.toString()

                    if (beforeImageData.isEmpty()){
                        uploadCloneImage=updateImage
                        buttonClick()
                    }else {
                        val fileUploader = AWSProfileUploader(
                            requireContext(), beforeImageData, this@UpdateProfileFragment
                        )
                        fileUploader.uploadImage()
                    }
                    updateNameEdit.text?.clear()
                    updateNumberEdittext.text?.clear()
                    updateEmailEdittext.text?.clear()

                }
            }
    }
    }

    private fun pickImage() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            ImagePicker.with(this).crop()
                .maxResultSize(1080, 1080).start()
        } else {
            checkCameraPermission()
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                requestPermission
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (resultCode) {
                Activity.RESULT_OK -> {

                    val uri = data!!.data
                    val fullPath = uri?.let { getPath(activity, it) }
                    imagePathName = fullPath?.let { File(it).toString() }!!
                    beforeImageData.add(imagePathName)
                    context?.let {
                        Glide.with(it).load(uri).apply(RequestOptions.circleCropTransform()).error(R.drawable.ic_profile_1x).into(updateProfileFragment.updateProfile)
                    }
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), "You have achieved maximum number of upload...", Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    Toast.makeText(requireContext(), ".......", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    @SuppressLint("ObsoleteSdkInt")
    fun getPath(context: Context?, uri: Uri): String? {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(
                context,
                uri
            )
        ) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return context?.let { getDataColumn(it, contentUri, null, null) }
            } else if (isMediaDocument(uri)) { // MediaProvider
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                when (type) {
                    "image" -> {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return context?.let { getDataColumn(it, contentUri, selection, selectionArgs) }
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) { // MediaStore (and general)
            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else context?.let { getDataColumn(it, uri, null, null) }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) { // File
            return uri.path
        }
        return null
    }
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalised.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }



    private fun updateProfile() {
        val token =   SavedSharedPreference.getUserData(requireContext()).token
        val jsonObject = JsonObject().apply {
            addProperty("user_token", token)
        }

        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
            viewModel?.updateprofile(jsonObject = jsonObject)?.observe(requireActivity(), updatedProfileRead)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val updatedProfileRead=Observer<ApiResult<UpdateprofileData>>{
        when(it){
            is ApiResult.Success -> {
                updateProfileFragment.apply {
                    updateNameEdit.setText(it.data.name)
                    updateNumberEdittext.setText(it.data.mobile_number)
                    updateEmailEdittext.setText(it.data.email)
                    updateImage=it.data.image
                    context?.let { it1 ->
                        Glide.with(it1).load(it.data.image).apply(RequestOptions.circleCropTransform()).error(R.drawable.ic_profile_1x).into(updateProfile)
                    }
                }
            }
            is ApiResult.Error -> {
//                errors("Error", it.message)
            }
            else -> {}
        }
    }



    private fun buttonClick(){
        val token =   SavedSharedPreference.getUserData(requireContext()).token
        val jsonObject = JsonObject().apply {
            addProperty("user_token",token)
            addProperty("title",passengerTitle)
            addProperty("name",passengerName)
            addProperty("image",uploadCloneImage)
            addProperty("mobile_number",passengerNumber)
            addProperty("country_code",passengerCountry)
            addProperty("email",passengerEmail)

        }

        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
            viewModel?.update(jsonObject = jsonObject )?.observe(requireActivity(),update)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val update=Observer<ApiResult<UpdateprofileData>>{
        when(it) {
            is ApiResult.Success -> {

                SavedSharedPreference.setUserData(
                    ctx = requireContext(),
                    name = it.data.name,
                    mobile = it.data.mobile_number,
                    token = it.data.token,
                    date = it.data.dob,
                    type = "",
                    email = it.data.email,
                    code = it.data.country_code
                )

                if (!TextUtils.isEmpty(it.data.image)){
                    SavedSharedPreference.setImageUrl(context = requireContext(), imageUrl = it.data.image)
                }

                launchActivity(MainActivity::class.java)
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
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        return updateProfileFragment.root
    }

    override fun onSuccess(uploadCloneTicket: String) {
        uploadCloneImage = uploadCloneTicket
        Toast.makeText(requireContext(), "Upload profile image successfully", Toast.LENGTH_SHORT).show()
        buttonClick()
    }

    override fun onFailure(exception: Exception) {
        Toast.makeText(requireContext(), "Upload Failed", Toast.LENGTH_SHORT).show()
        Log.d(AwsProfile.TAG, "onSuccess: ${exception.localizedMessage}")
    }



}