package com.gordeevbr.revolut.services

import com.gordeevbr.revolut.entities.Currency
import com.gordeevbr.revolut.entities.UserBankAccount
import java.math.BigDecimal

interface UserBankAccountService {

    fun openAccount(userEmail: String, currency: Currency): UserBankAccount

    fun addCurrency(account: String, amount: BigDecimal): UserBankAccount

    fun withdrawCurrency(account: String, amount: BigDecimal): UserBankAccount

    fun transaction(from: String, to: String, amount: BigDecimal)

    fun closeAccount(account: String): UserBankAccount

    fun getAccount(account: String): UserBankAccount

}