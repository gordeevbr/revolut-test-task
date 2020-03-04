package com.gordeevbr.revolut.web.dtos

import com.gordeevbr.revolut.entities.UserProfile
import com.gordeevbr.revolut.kotlin.NoArg

@NoArg
data class UserProfileDto(
        val name: String,
        val secondName: String,
        val email: String,
        val bankAccounts: List<String>
)

fun UserProfile.toDto() = UserProfileDto(name, secondName, email, bankAccounts.map { it.accountNumber })