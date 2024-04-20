package com.parth.pestotest.network

class Resource<T>(val status: Status, var data: T?, val message: String?) {

    companion object {
        fun <T> success(data: T? = null): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String?, data: T? = null): Resource<T> {
            return Resource(Status.ERROR, data, msg)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }

        fun <T> logout(): Resource<T> {
            return Resource(Status.LOGOUT, null, null)
        }
    }
}

enum class Status {
    SUCCESS, ERROR, LOADING, LOGOUT
}