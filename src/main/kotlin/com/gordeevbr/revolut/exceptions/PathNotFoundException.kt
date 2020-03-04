package com.gordeevbr.revolut.exceptions

import com.gordeevbr.revolut.web.*
import com.sun.net.httpserver.HttpExchange

class PathNotFoundException(method: RestMethodDescriptor): WebException("Not Found: ${method.method.value} ${method.path}") {

    override fun respond(exchange: HttpExchange) = exchange.respond(404, Body(message.toByteArray(), TEXT))

}