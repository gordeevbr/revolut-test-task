package com.gordeevbr.revolut.exceptions

import com.gordeevbr.revolut.web.Body
import com.gordeevbr.revolut.web.TEXT
import com.gordeevbr.revolut.web.respond
import com.sun.net.httpserver.HttpExchange

class EntityNotFoundException(entity: String): WebException("Could not find an entity $entity for the provided request") {

    override fun respond(exchange: HttpExchange) = exchange.respond(404, Body(message.toByteArray(), TEXT))

}