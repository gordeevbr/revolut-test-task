package com.gordeevbr.revolut.web

annotation class Controller

annotation class RestMethod(val path: String, val method: Method = Method.GET)

enum class Method(val value: String) {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE")
}

data class RestMethodDescriptor(val path: String, val method: Method = Method.GET)