package com.example.sampleproject.util

data class Resource<out T>(val status: Status, val data: T?, val message: String?, val isData: Boolean) {

    companion object {

        fun <T> success(isData:Boolean, data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null, isData)
        }

        fun <T> error(msg: String, isData:Boolean, data: T?): Resource<T> {
            return Resource(Status.ERROR, data, msg, isData)
        }

        fun <T> loading(isData: Boolean, data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null, isData)
        }
    }
}