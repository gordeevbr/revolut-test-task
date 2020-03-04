package com.gordeevbr.revolut.web.controllers

import com.gordeevbr.revolut.exceptions.MissingMandatoryQueryParameter
import com.gordeevbr.revolut.services.UserBankAccountService
import com.gordeevbr.revolut.web.*
import com.gordeevbr.revolut.web.dtos.AccountOpeningRequestDto
import com.gordeevbr.revolut.web.dtos.BalanceRepresentationDto
import com.gordeevbr.revolut.web.dtos.BankTransactionDto
import com.gordeevbr.revolut.web.dtos.toDto
import com.sun.net.httpserver.HttpExchange

@Controller
class UserBankAccountController(private val userBankAccountService: UserBankAccountService) {

    @RestMethod("/user/banking/account", method = Method.POST)
    fun openAccount(exchange: HttpExchange) {
        val request = exchange.readJson<AccountOpeningRequestDto>()
        val result = userBankAccountService.openAccount(request.email, request.currency)
        exchange.respondTyped(200, result.toDto())
    }

    @RestMethod("/user/banking/account", method = Method.GET)
    fun getAccount(exchange: HttpExchange) {
        val requestedAccount = exchange.getQueryParameter("account") ?: throw MissingMandatoryQueryParameter("account")
        val result = userBankAccountService.getAccount(requestedAccount)
        exchange.respondTyped(200, result.toDto())
    }

    @RestMethod("/user/banking/account", method = Method.DELETE)
    fun closeAccount(exchange: HttpExchange) {
        val requestedAccount = exchange.getQueryParameter("account") ?: throw MissingMandatoryQueryParameter("account")
        val result = userBankAccountService.closeAccount(requestedAccount)
        exchange.respondTyped(200, result.toDto())
    }

    @RestMethod("/user/banking/account/balance", method = Method.PUT)
    fun addCurrency(exchange: HttpExchange) {
        val request = exchange.readJson<BalanceRepresentationDto>()
        val requestedAccount = exchange.getQueryParameter("account") ?: throw MissingMandatoryQueryParameter("account")
        val result = userBankAccountService.addCurrency(requestedAccount, request.toBigDecimal())
        exchange.respondTyped(200, result.toDto())
    }

    @RestMethod("/user/banking/account/balance", method = Method.DELETE)
    fun withdrawCurrency(exchange: HttpExchange) {
        val request = exchange.readJson<BalanceRepresentationDto>()
        val requestedAccount = exchange.getQueryParameter("account") ?: throw MissingMandatoryQueryParameter("account")
        val result = userBankAccountService.withdrawCurrency(requestedAccount, request.toBigDecimal())
        exchange.respondTyped(200, result.toDto())
    }

    @RestMethod("/user/banking/transaction", method = Method.POST)
    fun transaction(exchange: HttpExchange) {
        val request = exchange.readJson<BankTransactionDto>()
        userBankAccountService.transaction(request.from, request.to, request.amount.toBigDecimal())
        exchange.respond(200)
    }

}