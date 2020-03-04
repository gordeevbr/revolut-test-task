package com.gordeevbr.revolut.apis

import com.gordeevbr.revolut.web.dtos.AccountOpeningRequestDto
import com.gordeevbr.revolut.web.dtos.BalanceRepresentationDto
import com.gordeevbr.revolut.web.dtos.BankTransactionDto
import com.gordeevbr.revolut.web.dtos.UserBankAccountDto
import feign.Headers
import feign.Param
import feign.RequestLine

interface UserBankAccountApi {

    @RequestLine("POST /user/banking/account")
    @Headers("Content-Type: application/json")
    fun openAccount(accountOpeningRequestDto: AccountOpeningRequestDto): UserBankAccountDto

    @RequestLine("GET /user/banking/account?account={account}")
    fun getAccount(@Param("account") account: String): UserBankAccountDto

    @RequestLine("DELETE /user/banking/account?account={account}")
    // I am assuming that closing an account also returns user all funds, therefore there is no balance check here
    fun closeAccount(@Param("account") account: String): UserBankAccountDto

    @RequestLine("PUT /user/banking/account/balance?account={account}")
    @Headers("Content-Type: application/json")
    fun addCurrency(@Param("account") account: String, amount: BalanceRepresentationDto): UserBankAccountDto

    @RequestLine("DELETE /user/banking/account/balance?account={account}")
    @Headers("Content-Type: application/json")
    fun withdrawCurrency(@Param("account") account: String, amount: BalanceRepresentationDto): UserBankAccountDto

    @RequestLine("POST /user/banking/transaction")
    @Headers("Content-Type: application/json")
    fun transaction(transactionDto: BankTransactionDto)

}