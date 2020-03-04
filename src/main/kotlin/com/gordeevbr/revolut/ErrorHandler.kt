package com.gordeevbr.revolut

import com.gordeevbr.revolut.web.Body
import com.gordeevbr.revolut.web.TEXT
import com.gordeevbr.revolut.web.respond
import com.gordeevbr.revolut.exceptions.WebException
import com.sun.net.httpserver.HttpExchange
import mu.KLogging
import org.hibernate.exception.ConstraintViolationException

class ErrorHandler {

    private companion object: KLogging()

    fun handle(exchange: HttpExchange, error: Throwable) {
        when (error) {
            is WebException -> error.respond(exchange)
            is ConstraintViolationException -> {
                exchange.respond(400, Body("This entity already exists".toByteArray(), TEXT))
            }
            is Exception -> {
                logger.error(error) { "An unknown exception had been caught when handling a request: $error" }
                exchange.respond(500, Body("A server error has occurred, please try again later".toByteArray(), TEXT))
            }
            else -> throw error
        }
    }

}