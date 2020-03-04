package com.gordeevbr.revolut.exceptions

import com.sun.net.httpserver.HttpExchange

abstract class WebException(override val message: String): Exception(message) {

    abstract fun respond(exchange: HttpExchange)

}