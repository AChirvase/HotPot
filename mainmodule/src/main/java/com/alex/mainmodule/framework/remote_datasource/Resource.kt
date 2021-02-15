package com.alex.mainmodule.framework.remote_datasource

enum class NetworkCallStatus {
    SUCCESS,
    ERROR,
    LOADING
}

data class Resource<out T>(val status: NetworkCallStatus, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(NetworkCallStatus.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(NetworkCallStatus.ERROR, data, msg)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(NetworkCallStatus.LOADING, data, null)
        }
    }
}