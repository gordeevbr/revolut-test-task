package com.gordeevbr.revolut.web.dtos

import com.gordeevbr.revolut.entities.Currency
import com.gordeevbr.revolut.entities.UserBankAccount
import com.gordeevbr.revolut.kotlin.NoArg

@NoArg
data class UserBankAccountDto(
        val accountNumber: String,
        val balance: BalanceRepresentationDto,
        val currency: Currency
)

fun UserBankAccount.toDto() = UserBankAccountDto(accountNumber, balance.toDto(), currency)