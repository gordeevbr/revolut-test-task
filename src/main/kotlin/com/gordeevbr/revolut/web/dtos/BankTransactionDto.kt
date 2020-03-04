package com.gordeevbr.revolut.web.dtos

import com.gordeevbr.revolut.kotlin.NoArg

@NoArg
data class BankTransactionDto(
        val from: String,
        val to: String,
        val amount: BalanceRepresentationDto
)