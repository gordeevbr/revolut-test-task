package com.gordeevbr.revolut.exceptions

import com.gordeevbr.revolut.web.Body
import com.gordeevbr.revolut.web.TEXT
import com.gordeevbr.revolut.web.respond
import com.sun.net.httpserver.HttpExchange

class UnsupportedMethodException(method: String): WebException("Unsupported HTTP Method: $method") {

    override fun respond(exchange: HttpExchange) = exchange.respond(405, Body(message.toByteArray(), TEXT))

}