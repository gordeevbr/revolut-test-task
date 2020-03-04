package com.gordeevbr.revolut.services

import com.gordeevbr.revolut.entities.Currency
import com.gordeevbr.revolut.entities.UserBankAccount
import com.gordeevbr.revolut.exceptions.DifferentCurrenciesException
import com.gordeevbr.revolut.exceptions.EntityNotFoundException
import com.gordeevbr.revolut.exceptions.InsufficientBalanceException
import org.hibernate.Session
import java.math.BigDecimal
import java.util.*

class SessionUserBankAccountService(
        private val session: Session,
        private val profileService: UserProfileService
): UserBankAccountService {

    override fun openAccount(userEmail: String, currency: Currency): UserBankAccount {
        val profile = profileService.getProfile(userEmail)
        val account = UserBankAccount(0, UUID.randomUUID().toString(), profile, BigDecimal.ZERO, currency)
        profile.bankAccounts.add(account)
        session.update(profile)
        return account
    }

    override fun addCurrency(account: String, amount: BigDecimal): UserBankAccount {
        val bankAccount = getAccount(account)
        bankAccount.balance = bankAccount.balance.add(amount)
        session.update(bankAccount)
        return bankAccount
    }

    override fun withdrawCurrency(account: String, amount: BigDecimal): UserBankAccount {
        val bankAccount = getAccount(account)
        bankAccount.balance = bankAccount.balance.subtract(amount)
        if (bankAccount.balance.signum() < 0) {
            throw InsufficientBalanceException(account)
        }
        session.update(bankAccount)
        return bankAccount
    }

    override fun transaction(from: String, to: String, amount: BigDecimal) {
        val firstAccount = getAccount(from)
        val secondAccount = getAccount(to)
        if (firstAccount.currency != secondAccount.currency) {
            throw DifferentCurrenciesException()
        }
        withdrawCurrency(from, amount)
        addCurrency(to, amount)
    }

    override fun closeAccount(account: String): UserBankAccount {
        val bankAccount = getAccount(account)
        val profile = bankAccount.profile
        profile.bankAccounts.remove(bankAccount)
        session.update(profile)
        return bankAccount
    }

    override fun getAccount(account: String): UserBankAccount = session
            .createQuery("SELECT a FROM UserBankAccount a WHERE a.accountNumber LIKE :account")
            .setParameter("account", account)
            .resultStream
            .findFirst()
            .map { it as UserBankAccount }
            .orElse(null) ?: throw EntityNotFoundException("bank account")

}