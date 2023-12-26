package com.travel.airportzo.user.utils

import android.content.Context
import android.util.Log
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.*
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.travel.airportzo.user.model.AwsChat
import java.io.File

class AWSChatUploader(context: Context, private val filePaths: List<String>, private val awsChatUploadListener: AWSChatUploaderListener,
) {
    private val uploadTicket: ArrayList<String> by lazy { arrayListOf() }
    private var uploadCloneTicket: String =""

    private val credentialsProvider by lazy {
        CognitoCachingCredentialsProvider(
            context,
            AwsChat.COGNITO_IDENTITY_ID,
            Regions.AP_SOUTH_1
        )
    }
    private val s3Client by lazy {
        AmazonS3Client(
            credentialsProvider,
            Region.getRegion(Regions.AP_SOUTH_1)
        )
    }
    private val transferUtility by lazy {
        TransferUtility.builder()
            .s3Client(s3Client)
            .context(context)
            .transferUtilityOptions(TransferUtilityOptions())
            .build()
    }

    fun uploadImage() {
        uploadTicket.clear()
        filePaths.map {
            val file = File(it)
            Utility.chatimage = ""
            Utility.chatimage = file.name
            val transferObserver = transferUtility.upload(
                AwsChat.BUCKET_NAME,
                AwsChat.folderPath + file.name,
                file,
                CannedAccessControlList.Private
            )
            transferObserver.setTransferListener(transferListener(transferObserver))
            Log.d(transferObserver.toString(), "onStateChanged")
        }
    }
    private fun transferListener(transferObserver: TransferObserver) = object : TransferListener {
        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) { }
        override fun onStateChanged(id: Int, state: TransferState?) {
            if (state == TransferState.COMPLETED) {
                val imagePath = AwsChat.S3_URL +transferObserver.key
                Log.d(AwsChat.TAG, "onStateChanged: $imagePath")
                uploadTicket.add(imagePath)
                uploadCloneTicket = imagePath

                if (uploadTicket.size == filePaths.size) {
                    awsChatUploadListener.onSuccess(uploadCloneTicket)
                }
            }
        }
        override fun onError(id: Int, ex: Exception) {
            awsChatUploadListener.onFailure(ex)
        }
    }
    interface AWSChatUploaderListener {
        fun onSuccess(uploadCloneTicket: String)
        fun onFailure(exception: Exception)
    }
}
