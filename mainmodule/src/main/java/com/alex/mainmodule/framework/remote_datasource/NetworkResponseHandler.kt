package com.alex.mainmodule.framework.remote_datasource

import retrofit2.HttpException
import java.net.SocketTimeoutException

enum class ErrorCodes(val code: Int) {
    SocketTimeOut(-1),
    Unauthorized(401),
    NotFound(404)
}


/*
    Wrapper over the Network Response that simplifies the server calls code and
    the way network calls errors are handled
 */
open class NetworkResponseHandler {
    fun <T : Any> handleSuccess(data: T): Resource<T> {
        return Resource.success(data)
    }

    fun <T : Any> handleException(e: Exception): Resource<T> {
        return when (e) {
            is HttpException -> Resource.error(getErrorMessage(e.code()), null)
            is SocketTimeoutException -> Resource.error(
                getErrorMessage(ErrorCodes.SocketTimeOut.code),
                null
            )
            else -> Resource.error(getErrorMessage(Int.MAX_VALUE), null)
        }
    }

    private fun getErrorMessage(code: Int): String {
        return when (code) {
            ErrorCodes.SocketTimeOut.code -> "Timeout"
            ErrorCodes.Unauthorized.code -> "Unauthorised"
            ErrorCodes.NotFound.code -> "Not found"
            else -> "Something went wrong; Error Code not handled well"
        }
    }
}