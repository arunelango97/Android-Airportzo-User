package com.travel.airportzo.user.ui.activity


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.travel.airportzo.user.databinding.ChatActivityBinding
import com.travel.airportzo.user.model.AwsProfile
import com.travel.airportzo.user.model.ChatData
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.ui.adapter.ChatAdapter
import com.travel.airportzo.user.ui.base.BaseActivity
import com.travel.airportzo.user.utils.AWSChatUploader
import com.travel.airportzo.user.utils.setOnDebounceListener
import java.io.File


class ChatActivity : BaseActivity(), AWSChatUploader.AWSChatUploaderListener {

    private val chatActivity by lazy { ChatActivityBinding.inflate(layoutInflater) }
    private val db = Firebase.firestore
    private val messageData: ArrayList<ChatData> by lazy { arrayListOf() }
    private val beforeImageData: ArrayList<String> by lazy { arrayListOf() }
    private lateinit var chatAdapter: ChatAdapter
    private var myMessage: String = ""
    private var tpmysg: Int = 0
    private val requestPermission = 100
    private var imagePathName: String = ""
    private var uploadCloneImage: String = ""
    private var chatToken: String = ""
    private var chatService: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(chatActivity.root)

        chatToken = intent.getStringExtra("chatToken")!!
        chatService = intent.getStringExtra("chatService")!!

        chatActivity.agentName.text = chatService.toString()

        chatActivity.personalBack.setOnDebounceListener {
            onBackPressed()
        }

        chatActivity.sendImage.setOnClickListener {
            val input = chatActivity.sendEditText.text.toString()
            insertMessage(input, "text")
        }

        chatActivity.chatImage.setOnDebounceListener {
            pickImage()
        }

        db.collection("service").document(chatToken).collection("chat")
            .orderBy("date_time", Query.Direction.ASCENDING).addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("FirebaseError", error.toString())
                    return@addSnapshotListener
                }
                for (doc in value!!.documentChanges) {
                    val commentId = doc.document.id
                    val datetime = doc.document.data["date_time"] as Timestamp?
                    val message = doc.document.data["message"].toString()
                    val messageType = doc.document.data["message_type"].toString()
                    val senderId = doc.document.data["sender_id"].toString()


                    val token = SavedSharedPreference.getUserData(this).token
                    if (senderId == "user-$token") {
                        if (messageType == "text") {
                            myMessage = "textmy"
                            tpmysg = 0
                        } else if (messageType == "image") {
                            myMessage = "imagemy"
                            tpmysg = 2
                        }
                    } else {
                        if (messageType == "text") {
                            myMessage = "textrec"
                            tpmysg = 1
                        } else if (messageType == "image") {
                            myMessage = "imagerec"
                            tpmysg = 3
                        }
                    }
                    messageData.add(
                        ChatData(
                            date_time = datetime,
                            message = message,
                            message_type = messageType,
                            sender_id = senderId,
                            myMessage = myMessage,
                            tpmysg = tpmysg
                        )
                    )
                }

                Log.e("manoj", messageData.toString())
                chatActivity.bookingList.layoutManager = LinearLayoutManager(this)
                chatAdapter = ChatAdapter(messageData)
                chatAdapter.notifyItemRangeInserted(0, messageData.size)
                chatActivity.bookingList.setItemViewCacheSize(messageData.size)
                chatActivity.bookingList.scrollToPosition(messageData.size - 1)
                chatActivity.bookingList.adapter = chatAdapter
            }
    }

    private fun pickImage() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            ImagePicker.with(this).crop().maxResultSize(1080, 1080).start()
        } else {
            checkCameraPermission()
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), requestPermission
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (resultCode) {
                Activity.RESULT_OK -> {

                    val uri = data!!.data
                    val fullPath = uri?.let { getPath(this, it) }
                    imagePathName = fullPath?.let { File(it).toString() }!!
                    beforeImageData.clear()
                    beforeImageData.add(imagePathName)
                    val fileUploader =
                        AWSChatUploader(application, beforeImageData, this@ChatActivity)
                    fileUploader.uploadImage()
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(
                        this, "You have achieved maximum number of upload...", Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    Toast.makeText(this, ".......", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    @SuppressLint("ObsoleteSdkInt")
    fun getPath(context: Context?, uri: Uri): String? {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(
                context, uri
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
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return context?.let { getDataColumn(it, contentUri, null, null) }
            } else if (isMediaDocument(uri)) { // MediaProvider
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return context?.let { getDataColumn(it, contentUri, selection, selectionArgs) }
            }
        } else if ("content".equals(
                uri.scheme, ignoreCase = true
            )
        ) { // MediaStore (and general)
            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else context?.let {
                getDataColumn(
                    it, uri, null, null
                )
            }
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
        context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?
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

    private fun insertMessage(message: String, type: String) {
        val token = SavedSharedPreference.getUserData(this).token
        if (message.trim { it <= ' ' } != "") {
            chatActivity.sendEditText.setText("")
            val user = hashMapOf(
                "date_time" to Timestamp.now(),
                "message" to message,
                "message_type" to type,
                "sender_id" to "user-$token",
            )

            db.collection("service").document(chatToken).collection("chat").add(user)
                .addOnSuccessListener { documentReference ->
                    if (type == "text") {
                        chatActivity.sendEditText.setText("")
                    } else if (type == "image") {
//                        documentPathId = documentReference.id
                    }
                    Log.d("User", documentReference.id)
                }.addOnFailureListener { e ->
                    Log.d("User", "Error adding document", e)
                }
        }

    }

    override fun onSuccess(uploadCloneTicket: String) {
        uploadCloneImage = uploadCloneTicket
        insertMessage(uploadCloneImage, "image")

        /*val storageRef: StorageReference = storage.reference
    assert(imageUri != null)
    val riversRef = storageRef.child("chat/" + imageUri?.lastPathSegment)

    binding.message.setText("")

    val uploadTask = riversRef.putFile(imageUri!!)
    uploadTask.addOnFailureListener { exception ->
        Log.d("UploadFail", exception.toString())
    }.addOnSuccessListener {
        riversRef.downloadUrl.addOnSuccessListener { uri ->
            insertMessage(uploadCloneImage, 1)
        }
    }*/
    }

    override fun onFailure(exception: Exception) {
        Log.d(AwsProfile.TAG, "onSuccess: ${exception.localizedMessage}")
    }
}

