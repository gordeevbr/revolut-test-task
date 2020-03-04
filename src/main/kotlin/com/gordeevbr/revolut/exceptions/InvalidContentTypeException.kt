package com.gordeevbr.revolut.exceptions

import com.gordeevbr.revolut.web.Body
import com.gordeevbr.revolut.web.TEXT
import com.gordeevbr.revolut.web.respond
import com.sun.net.httpserver.HttpExchange

class InvalidContentTypeException(expected: String, actual: String): WebException(
        "Expected content type to be $expected, got $actual"
) {

    override fun respond(exchange: HttpExchange) = exchange.respond(400, Body(message.toByteArray(), TEXT))

}