package com.gordeevbr.revolut.entities

import com.gordeevbr.revolut.kotlin.NoArg
import javax.persistence.*
import javax.validation.constraints.Email

@Entity
@Table(
        name = "user_profile",
        uniqueConstraints = [UniqueConstraint(name = "unique_email", columnNames = ["email"])]
)
@NoArg
data class UserProfile(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long,

        val name: String,

        val secondName: String,

        @field:Email
        val email: String,

        @OneToMany(mappedBy = "profile", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        val bankAccounts: MutableList<UserBankAccount>
)