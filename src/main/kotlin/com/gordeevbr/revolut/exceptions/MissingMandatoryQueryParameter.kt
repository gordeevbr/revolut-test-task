package com.gordeevbr.revolut.exceptions

import com.gordeevbr.revolut.web.Body
import com.gordeevbr.revolut.web.TEXT
import com.gordeevbr.revolut.web.respond
import com.sun.net.httpserver.HttpExchange

class MissingMandatoryQueryParameter(
        parameterName: String
): WebException("Missing required query parameter: $parameterName") {

    override fun respond(exchange: HttpExchange) = exchange.respond(400, Body(message.toByteArray(), TEXT))

}