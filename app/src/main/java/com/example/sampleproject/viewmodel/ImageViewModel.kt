package com.example.sampleproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sampleproject.model.ImageRepository
import com.example.sampleproject.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _imageFile = MutableLiveData<ArrayList<File>>()
    val imageFile: LiveData<ArrayList<File>> get() = _imageFile

    private val _resource = MutableLiveData<Resource<Any>>()
    val resource: LiveData<Resource<Any>> get() = _resource

    fun uploadImage(file: ArrayList<File>) {
        if(file.isNotEmpty()){
            _resource.postValue(Resource.loading(true, null))

            viewModelScope.launch {
                val profileImageBody = arrayListOf<MultipartBody.Part>()
                val preset = getMultiPartFormRequestBody("<your preset>")

                for(i in file.indices){
                    val profileImage: RequestBody = file[i].asRequestBody("image/jpg".toMediaTypeOrNull())
                    val emptyPart = MultipartBody.Part.createFormData("file",file[i].name,profileImage)
                    profileImageBody.add(emptyPart)

                    imageModel.getUploadImage(profileImageBody[i],preset).let {
                        if (it.isSuccessful) {
                            if(file.size == profileImageBody.size)
                                _resource.postValue(Resource.success(false,it.body()))
                        }else {
                            _resource.postValue(Resource.error(it.errorBody().toString(), false, null))
                        }
                    }
                }
            }
        }
    }

    private fun getMultiPartFormRequestBody(tag: String?): RequestBody {
        return tag!!.toRequestBody(MultipartBody.FORM)
    }

    fun sendImageFile(file: ArrayList<File>) {
        _imageFile.postValue(file)
    }
}
