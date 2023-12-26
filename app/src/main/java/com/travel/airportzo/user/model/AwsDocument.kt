package com.travel.airportzo.user.model

import com.amazonaws.regions.Regions

object AwsDocument {
    const val TAG = "S3_UPLOAD_TRACKER"

    val COGNITO_IDENTITY_ID: String = "ap-south-1:0d3824be-4bcd-4ac8-8f34-b29baa427f00"
    val COGNITO_REGION: Regions = Regions.AP_SOUTH_1
    val BUCKET_NAME: String = "airportzoapp"

    val S3_URL: String = "https://d1xqjehqvi7b4u.cloudfront.net/"
    val folderPath = "user/agent_documents/"

}