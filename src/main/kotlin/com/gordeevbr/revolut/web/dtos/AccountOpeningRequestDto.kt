package com.gordeevbr.revolut.web.dtos

import com.gordeevbr.revolut.entities.Currency
import com.gordeevbr.revolut.kotlin.NoArg

@NoArg
data class AccountOpeningRequestDto(
        val email: String,
        val currency: Currency
)