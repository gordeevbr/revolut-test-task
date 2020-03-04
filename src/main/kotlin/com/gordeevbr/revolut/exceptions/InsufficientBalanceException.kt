package com.gordeevbr.revolut.exceptions

import com.gordeevbr.revolut.web.Body
import com.gordeevbr.revolut.web.TEXT
import com.gordeevbr.revolut.web.respond
import com.sun.net.httpserver.HttpExchange

class InsufficientBalanceException(account: String): WebException("Insufficient balance on account $account") {

    override fun respond(exchange: HttpExchange) = exchange.respond(400, Body(message.toByteArray(), TEXT))

}