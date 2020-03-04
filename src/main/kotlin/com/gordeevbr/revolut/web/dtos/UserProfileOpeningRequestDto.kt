package com.gordeevbr.revolut.web.dtos

import com.gordeevbr.revolut.kotlin.NoArg

@NoArg
data class UserProfileOpeningRequestDto(
        val name: String,
        val secondName: String,
        val email: String
)