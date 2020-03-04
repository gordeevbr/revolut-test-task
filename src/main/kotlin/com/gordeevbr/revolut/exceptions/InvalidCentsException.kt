package com.gordeevbr.revolut.exceptions

import com.gordeevbr.revolut.web.Body
import com.gordeevbr.revolut.web.TEXT
import com.gordeevbr.revolut.web.respond
import com.sun.net.httpserver.HttpExchange

class InvalidCentsException(cents: Int): WebException("Cents can only be in the range between 0 and 99, was: $cents") {

    override fun respond(exchange: HttpExchange) = exchange.respond(400, Body(message.toByteArray(), TEXT))

}