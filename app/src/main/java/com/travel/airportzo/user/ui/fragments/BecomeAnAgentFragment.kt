package com.travel.airportzo.user.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
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
import android.text.Editable
import android.text.TextWatcher
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
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.JsonObject
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.BecomeAnAgentFragmentBinding
import com.travel.airportzo.user.model.*
import com.travel.airportzo.user.network.ApiIFSC
import com.travel.airportzo.user.network.ApiResult
import com.travel.airportzo.user.network.NoInternetActivity
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.utils.setOnDebounceListener
import com.travel.airportzo.user.ui.base.BaseFragment
import com.travel.airportzo.user.utils.AWSAgentUploader
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class BecomeAnAgentFragment : BaseFragment(),AWSAgentUploader.AWSAgentUploadListener {

    private val becomeAgent by lazy { BecomeAnAgentFragmentBinding.inflate(layoutInflater) }

    private val countryCode: ArrayList<CountryData> by lazy { arrayListOf() }
    private val countryName: ArrayList<String> by lazy { arrayListOf() }
    private val regionCode: ArrayList<RegionData> by lazy { arrayListOf() }
    private val regionName: ArrayList<String> by lazy { arrayListOf() }
    private val cityCode: ArrayList<CityData> by lazy { arrayListOf() }
    private val cityName: ArrayList<String> by lazy { arrayListOf() }
    private val ifscData: ArrayList<IFSCData> by lazy { arrayListOf() }
    private val becomeAgentData: ArrayList<BecomeAgentData> by lazy { arrayListOf() }
    private val beforeImageData: ArrayList<String> by lazy { arrayListOf() }
    private val beforeImageDataDocument: ArrayList<String> by lazy { arrayListOf() }

    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+"
    private val types = arrayOf("Mr.", "Mrs.", "Ms.")

    private val requestPermission = 100
    private  var imageUpload:Int = 0
    private var backFlow: Int = 0

    private var uploadCloneImage : String = ""
    private var uploadClonePan : String = ""
    private var uploadCloneAddress : String = ""
    private var updateImage:String=""
    private var outCountry: String = ""
    private var outState: String = ""
    private var outCity: String = ""
    private var countryCodes: String = ""
    private var mobile: String = ""
    private var dob: String = ""
    private var address: String = ""
    private var pinCode: String = ""
    private var accountNo: String = ""
    private var ifscCodeNo: String = ""
    private var imagePathName: String = ""


    var state: String = ""
    var initial: String = ""
    var country: String = ""
    var city: String = ""
    var ifscCode: String = ""
    var branchName: String = ""
    var name: String = ""
    var image: String = ""
    var email: String = ""


    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** brand color */

        becomeAgent.rootLayout.setBackgroundColor(Color.parseColor(activity?.let {
            SavedSharedPreference.getCustomColor(
                it
            ).brand_colour
        }))


        becomeAgent.agentButton.setBackgroundColor(Color.parseColor(activity?.let {
            SavedSharedPreference.getCustomColor(
                it
            ).brand_colour
        }))


        becomeAgent.next.setBackgroundColor(Color.parseColor(activity?.let {
            SavedSharedPreference.getCustomColor(
                it
            ).brand_colour
        }))


        becomeAgent.submitButton.setBackgroundColor(Color.parseColor(activity?.let {
            SavedSharedPreference.getCustomColor(
                it
            ).brand_colour
        }))


        readProfile()

        becomeAgent.agentButton.setOnDebounceListener {
            becomeAgent.agentScroll.fullScroll(View.FOCUS_DOWN)
        }

        val fromAdapter: ArrayAdapter<String> = context?.let {
            ArrayAdapter<String>(it, android.R.layout.simple_spinner_dropdown_item, countryName) }!!
        becomeAgent.countryText.threshold = 2
        becomeAgent.countryText.setAdapter(fromAdapter)

        becomeAgent.reportProblem.setOnDebounceListener {
            becomeAgent.cCheckoutLayoutPassenger.visibility=View.VISIBLE
        }

        becomeAgent.ifscEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                ifscCode = s.toString()
                if (ifscCode.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Please fill IFSC code and Press search",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    ifsc()
                }
            }
        })
lifecycleScope.launch {
        viewModel?.countries()?.observe(requireActivity(), countriesCode)
}


        becomeAgent.agentBack.setOnDebounceListener {
            if (backFlow == 0) {
                Navigation.findNavController(requireView()).popBackStack()
            } else {
                backFlow = 0
                becomeAgent.cCheckoutLayoutGst.visibility = View.GONE
                becomeAgent.cCheckoutLayoutPassenger.visibility = View.VISIBLE
            }
        }


        becomeAgent.passengerDatePickerEdittext.setOnDebounceListener {
            val year = Calendar.YEAR
            val month = Calendar.MONTH
            val day = Calendar.DAY_OF_MONTH

            val datePickerDialog = DatePickerDialog(
                requireContext(), { _, monthOfYear, dayOfMonth, year ->
                    val dateType = "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                    val parser = SimpleDateFormat("yyyy-MM-dd")
                    val formatter = SimpleDateFormat("dd MMM, yyyy")
                    val output = parser.parse(dateType)?.let { it1 -> formatter.format(it1) }
                    becomeAgent.passengerDatePickerEdittext.text = output
                }, year, month, day

            )
//            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000)
            datePickerDialog.show()
        }


        becomeAgent.stateText.setOnDebounceListener {
            val country = becomeAgent.countryText.text.toString()
            for (i in 0 until countryCode.size) {
                if (country == countryCode[i].name) {
                    outCountry=""
                    outCountry = countryCode[i].id
                }
            }
            val jsonObject = JsonObject().apply {
                addProperty("country_id", outCountry)
            }
            if (isNetworkConnected(requireContext())) {
                lifecycleScope.launch {
                viewModel?.region(jsonObject=jsonObject)?.observe(requireActivity(),region)
                }
            } else {
                startActivity(Intent(requireContext(), NoInternetActivity::class.java))
            }

        }

        becomeAgent.cityText.setOnDebounceListener {
            stateInput()
        }


        becomeAgent.mrImg.setOnDebounceListener {
            becomeAgent.passengerNameSpinner.performClick()
            nameClick()
        }

        becomeAgent.agentImage.setOnDebounceListener {
            imageUpload=0
            pickImage()
        }

        becomeAgent.upload.setOnDebounceListener {
            imageUpload = 1
            pickImage()
        }

        becomeAgent.upload11.setOnDebounceListener {
            imageUpload=2
            pickImage()
        }




        becomeAgent.submitButton.setOnDebounceListener {
            if (becomeAgent.accountEdittext.text.toString().isEmpty()) {
                Toast.makeText(context, "Please enter account no", Toast.LENGTH_SHORT).show()
            }else if (becomeAgent.accountEdittext.text.toString() != becomeAgent.reAccountEdittext.text.toString()){
                Toast.makeText(context, "Account number not matched", Toast.LENGTH_SHORT).show()
            } else if (becomeAgent.ifscEdittext.text.toString().isEmpty()) {
                Toast.makeText(context, "Please enter IFSC code", Toast.LENGTH_SHORT).show()
            }else if (uploadClonePan.isEmpty()) {
                Toast.makeText(context, "Please Upload pan", Toast.LENGTH_SHORT).show()
            }else if (uploadCloneAddress.isEmpty()){
                Toast.makeText(context, "Please Upload address", Toast.LENGTH_SHORT).show()
            }else{
                name = becomeAgent.passengerNameEdittext.text.toString()
                countryCodes = becomeAgent.code.selectedCountryCode.toString()
                mobile = becomeAgent.passengerNumberEdittext.text.toString()
                email = becomeAgent.passengerMailEdittext.text.toString()
                dob = becomeAgent.passengerDatePickerEdittext.text.toString()
                address = becomeAgent.addressEdittext.text.toString()
                pinCode = becomeAgent.pinCodeEdittext.text.toString()
                accountNo = becomeAgent.accountEdittext.text.toString()
                ifscCodeNo = becomeAgent.ifscEdittext.text.toString()
                branchName = becomeAgent.bankPlace.text.toString()

                val fileUploader = AWSAgentUploader(requireContext(), beforeImageDataDocument, this@BecomeAnAgentFragment)
                fileUploader.uploadImage()
            }
        }



        becomeAgent.next.setOnDebounceListener {
            if(becomeAgent.agentImage.drawable ==null){
                Toast.makeText(context, "Please upload your image", Toast.LENGTH_SHORT).show()
            } else if (becomeAgent.passengerNameEdittext.text.toString().isEmpty()) {
                Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT).show()
            } else if (becomeAgent.code.selectedCountryCode.toString().isEmpty()) {
                Toast.makeText(context, "Please enter your number", Toast.LENGTH_SHORT).show()
            } else if (becomeAgent.passengerNumberEdittext.text.toString().isEmpty()) {
                Toast.makeText(context, "Please enter your Email", Toast.LENGTH_SHORT).show()
            } else if (!becomeAgent.passengerMailEdittext.text.toString().matches(emailPattern.toRegex())) {
                Toast.makeText(context, "Please enter valid email address", Toast.LENGTH_SHORT).show()
            } else if (becomeAgent.addressEdittext.text.toString().isEmpty()) {
                Toast.makeText(context, "Please enter your address", Toast.LENGTH_SHORT).show()
            } else if (becomeAgent.pinCodeEdittext.text.toString().isEmpty()) {
                Toast.makeText(context, "Please enter your pinCode", Toast.LENGTH_SHORT).show()
            } else if (becomeAgent.passengerMailEdittext.text.toString().isEmpty()) {
                Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
            } else if (becomeAgent.passengerDatePickerEdittext.text.toString().isEmpty()) {
                Toast.makeText(context, "Please enter your DOB", Toast.LENGTH_SHORT).show()
            } else if (becomeAgent.initialText.text.toString().isEmpty()) {
                Toast.makeText(context, "Please enter your tittleView", Toast.LENGTH_SHORT).show()
            } else {

                val city = becomeAgent.cityText.text.toString()

                for (i in 0 until cityCode.size){
                    if (city==cityCode[i].name){
                        outCity=""
                        outCity=cityCode[i].id
                    }
                }
                if (updateImage.isEmpty()){
                    becomeAgent.cCheckoutLayoutGst.visibility = View.VISIBLE
                    becomeAgent.cCheckoutLayoutPassenger.visibility = View.GONE
                    val fileUploader = AWSAgentUploader(requireContext(), beforeImageData, this@BecomeAnAgentFragment)
                    fileUploader.uploadImage()
                }else{
                    if (uploadCloneImage.isEmpty()){
                        uploadCloneImage=updateImage
                        becomeAgent.cCheckoutLayoutGst.visibility = View.VISIBLE
                        becomeAgent.cCheckoutLayoutPassenger.visibility = View.GONE
                        val fileUploader = AWSAgentUploader(requireContext(), beforeImageData, this@BecomeAnAgentFragment)
                        fileUploader.uploadImage()
                    }
                }

            }
        }
    }



    private fun nameClick() {
        becomeAgent.passengerNameSpinner.adapter = context.let {
            context?.let { it1 ->
                ArrayAdapter(
                    it1,
                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                    types
                )
            }
        } as SpinnerAdapter

        becomeAgent.passengerNameSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    initial = parent?.getItemAtPosition(position).toString()
                    becomeAgent.initialText.text = initial
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
                    if (imageUpload==0){
                        beforeImageData.clear()
                        beforeImageData.add(imagePathName)
                        context?.let {
                            Glide.with(it).load(uri).apply(RequestOptions.circleCropTransform())
                                .error(R.drawable.uploadphoto).into(becomeAgent.agentImage)
                        }
                    }else{
                        if(imageUpload == 1){
                            uploadClonePan = uri.lastPathSegment!!
                            becomeAgent.upload.visibility=View.GONE
                            becomeAgent.close.visibility=View.VISIBLE
                            becomeAgent.upload1.visibility=View.VISIBLE
                            becomeAgent.tick.visibility=View.VISIBLE
                        }else if(imageUpload == 2){
                            becomeAgent.upload11.visibility=View.GONE
                            becomeAgent.close1.visibility=View.VISIBLE
                            becomeAgent.upload21.visibility=View.VISIBLE
                            becomeAgent.tick1.visibility=View.VISIBLE
                            uploadCloneAddress = uri.lastPathSegment!!
                        }
                        beforeImageDataDocument.add(imagePathName)
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
        return "com.android.externalstorage.documents" == uri.authority
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




    private fun becomeAgent() {
        val token =   SavedSharedPreference.getUserData(requireContext()).token
        val jsonObject = JsonObject().apply {
            addProperty("user_token",token)
            addProperty("title",initial)
            addProperty("name",name)
            addProperty("image",uploadCloneImage)
            addProperty("country_code",countryCodes)
            addProperty("mobile_number",mobile)
            addProperty("email",email)
            addProperty("dob",dob)
            addProperty("address",address)
            addProperty("country_id",outCountry)
            addProperty("state_id",outState)
            addProperty("city_id",outCity)
            addProperty("pincode",pinCode)
            addProperty("account_number",accountNo)
            addProperty("ifsc_code",ifscCodeNo)
            addProperty("branch_name",branchName)
            addProperty("pan_card", "https://d1xqjehqvi7b4u.cloudfront.net/user/agent_documents/$uploadClonePan")
            addProperty("address_proof", "https://d1xqjehqvi7b4u.cloudfront.net/user/agent_documents/$uploadCloneAddress")
        }
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
            viewModel?.becomeagentt(jsonObject = jsonObject)?.observe(requireActivity(),agent)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val agent=Observer<ApiResult<BecomeAgentData>>{
        when (it) {
            is ApiResult.Success -> {
                becomeAgentData.clear()
                becomeAgentData.add(it.data)
                findNavController().navigate(R.id.action_becomeAnAgentFragment_to_navigation_home)
            }
            is ApiResult.Error -> {
                errors("Error", it.message)
            }
            else -> {}
        }
    }



    private fun ifsc() {
        ApiIFSC.ApiInterfaceifsc().product(ifscCode)
            .enqueue(object : Callback<IFSCData> {
                override fun onFailure(call: Call<IFSCData>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<IFSCData>,
                    response: Response<IFSCData>
                ) {
                    if (response.isSuccessful) {
                        ifscData.clear()
                        ifscData.add(response.body()!!)
                        branchName= becomeAgent.bankPlace.setText(response.body()!!.bRANCH).toString()
                    }
                }
            })
    }


    private val region=Observer<ApiResult<List<RegionData>>>{
        when(it){
            is ApiResult.Success -> {
                regionCode.clear()
                regionCode.addAll(it.data)
                for (i in 0 until it.data.size) {
                    regionName.add(it.data[i].name)
                }
                val fromAdapter: ArrayAdapter<String> = context?.let {
                    ArrayAdapter<String>(it, android.R.layout.simple_spinner_dropdown_item, regionName) }!!
                becomeAgent.stateText.threshold = 2
                becomeAgent.stateText.setAdapter(fromAdapter)

            }
            is ApiResult.Error -> {
                Toast.makeText(requireContext(), "No state", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }



    private fun stateInput() {
        val state = becomeAgent.stateText.text.toString()
        for (i in 0 until regionCode.size) {
            if (state == regionCode[i].name) {
                outState = ""
                outState = regionCode[i].id
            }
        }
        val jsonObject = JsonObject().apply {
            addProperty("country_id", outCountry)
            addProperty("region_id", outState)
        }
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
            viewModel?.cities(jsonObject=jsonObject)?.observe(requireActivity(),cities)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val cities=Observer<ApiResult<List<CityData>>>{
        when(it){
            is ApiResult.Success -> {
                cityCode.clear()
                cityCode.addAll(it.data)
                for (i in 0 until it.data.size){
                    cityName.add(it.data[i].name)
                }
                val fromAdapter: ArrayAdapter<String> = context?.let { it ->
                    ArrayAdapter<String>(it, android.R.layout.simple_spinner_dropdown_item, cityName) }!!
                becomeAgent.cityText.threshold = 2
                becomeAgent.cityText.setAdapter(fromAdapter)
            }

            is ApiResult.Error -> {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }



    private val countriesCode=Observer<ApiResult<List<CountryData>>>{
        when(it){
            is ApiResult.Success -> {
                countryCode.clear()
                countryCode.addAll(it.data)
                for (i in 0 until it.data.size){
                    countryName.add(it.data[i].name)
                }
            }
            is ApiResult.Error -> {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }



    private fun readProfile() {
        val token = SavedSharedPreference.getUserData(requireContext()).token
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
                becomeAgent.apply {
                    passengerNameEdittext.setText(it.data.name)
                    passengerNumberEdittext.setText(it.data.mobile_number)
                    passengerMailEdittext.setText(it.data.email)
                    updateImage=it.data.image
                    context?.let { it1 ->
                        Glide.with(it1).load(it.data.image).apply(RequestOptions.circleCropTransform()).error(R.drawable.uploadphoto).into(agentImage)
                    }
                }
            }
            is ApiResult.Error -> {
//                errors("Error", it.message)
            }
            else -> {}
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return becomeAgent.root
    }




    override fun onSuccess(uploadCloneticket: String) {
        if (uploadCloneImage.isEmpty() || beforeImageDataDocument.isEmpty()) {
            when (imageUpload) {
                0 -> {
                    uploadCloneImage = uploadCloneticket
                    Toast.makeText(requireContext(), "Uploaded", Toast.LENGTH_SHORT).show()
                }
                1 -> {
                    uploadClonePan = uploadCloneticket
                }
                2 -> {
                    uploadCloneAddress = uploadCloneticket
                }
            }
        }else{
            becomeAgent()
        }
    }

    override fun onFailure(exception: Exception) {
        Toast.makeText(requireContext(), "Upload E-Ticket Failed", Toast.LENGTH_SHORT).show()
        Log.d(AwsConstants.TAG, "onSuccess: ${exception.localizedMessage}")
    }
}