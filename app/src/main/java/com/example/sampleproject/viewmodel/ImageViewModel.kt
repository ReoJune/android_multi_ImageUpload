package com.example.sampleproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sampleproject.model.ImageRepository
import com.example.sampleproject.util.Event
import com.example.sampleproject.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ImageViewModel  @Inject constructor(
    private var imageModel: ImageRepository
): ViewModel() {

    private val _imageFile = MutableLiveData<Event<File>>()
    val imageFile: LiveData<Event<File>> get() = _imageFile

    private val _resource = MutableLiveData<Resource<Any>>()
    val resource: LiveData<Resource<Any>> get() = _resource

    fun uploadImage(file: File) {
        _resource.postValue(Resource.loading(true, null))

        val profileImage: RequestBody = file
            .asRequestBody("image/jpg".toMediaTypeOrNull())

        val profileImageBody: MultipartBody.Part =
            MultipartBody.Part.createFormData(
                "file",
                file.name, profileImage
            )

        val preset = getMultiPartFormRequestBody("<your upload_preset>")

        viewModelScope.launch {

            imageModel.getUploadImage(profileImageBody,preset).let {
                if (it.isSuccessful) {
                    _resource.postValue(Resource.success(false,it.body()))
                }else {
                    _resource.postValue(Resource.error(it.errorBody().toString(), false, null))
                }
            }
        }
    }

    private fun getMultiPartFormRequestBody(tag: String?): RequestBody {
        return tag!!.toRequestBody(MultipartBody.FORM)
    }

    fun sendImageFile(file: File) {
        _imageFile.postValue(Event(file))

    }
}
