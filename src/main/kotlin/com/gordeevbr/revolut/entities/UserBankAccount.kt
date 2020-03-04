package com.gordeevbr.revolut.entities

import com.gordeevbr.revolut.kotlin.NoArg
import java.math.BigDecimal
import javax.persistence.*
import javax.validation.constraints.PositiveOrZero

@Entity
@Table(
        name = "user_bank_account",
        uniqueConstraints = [
                UniqueConstraint(name = "unique_account_number", columnNames = ["accountNumber"])
        ]
)
@NoArg
data class UserBankAccount(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long,

        // something like an IBAN
        val accountNumber: String,

        @ManyToOne(fetch = FetchType.EAGER, optional = false)
        val profile: UserProfile,

        @field:PositiveOrZero
        var balance: BigDecimal,

        @Enumerated(EnumType.STRING)
        val currency: Currency
)