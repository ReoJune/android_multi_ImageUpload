package com.example.sampleproject.model

import com.example.sampleproject.api.Network
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class ImageRepository @Inject constructor(private val network: Network) {
    suspend fun getUploadImage(
        profileImageBody: MultipartBody.Part,
        preset: RequestBody
    ): Response<Any> {
        return network.uploadImage(
            profileImageBody, preset)
    }
}
