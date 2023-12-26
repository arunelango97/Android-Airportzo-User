package com.travel.airportzo.user.utils

import android.content.Context
import android.util.Log
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.*
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.travel.airportzo.user.model.AwsConstants
import java.io.File

class AWSFileUploader(context: Context,private val filePaths: List<String>, private val awsFileUploadListener: AWSFileUploadListener) {

    private val uploadTicket: ArrayList<String> by lazy { arrayListOf() }
    private var uploadClonePdf: String =""

    private val credentialsProvider by lazy {
        CognitoCachingCredentialsProvider(
            context,
            AwsConstants.COGNITO_IDENTITY_ID,
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
            Utility.uploadimage = ""

            val transferObserver = transferUtility.upload(
                AwsConstants.BUCKET_NAME,
                AwsConstants.folderPath + file.name,
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
                val imagePath = AwsConstants.S3_URL +transferObserver.key
                Log.d(AwsConstants.TAG, "onStateChanged: $imagePath")
                uploadTicket.add(imagePath)
                uploadClonePdf=imagePath

                if (uploadTicket.size == filePaths.size) {
                    awsFileUploadListener.onSuccess(uploadClonePdf)
                }
            }
            else{
               println("Fail")
            }
        }
        override fun onError(id: Int, ex: Exception) {
            awsFileUploadListener.onFailure(ex)
        }
    }
    interface AWSFileUploadListener {
        fun onSuccess(uploadClonePdf: String)
        fun onFailure(exception: Exception)
    }
}