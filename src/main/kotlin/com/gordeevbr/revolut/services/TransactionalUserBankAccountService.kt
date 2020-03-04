package com.gordeevbr.revolut.services

import com.gordeevbr.revolut.entities.Currency
import com.gordeevbr.revolut.entities.UserBankAccount
import org.hibernate.Session
import java.math.BigDecimal

class TransactionalUserBankAccountService(
        private val session: Session,
        private val delegate: UserBankAccountService
): UserBankAccountService {

    override fun openAccount(userEmail: String, currency: Currency): UserBankAccount
            = session.runInTransaction { delegate.openAccount(userEmail, currency) }

    override fun addCurrency(account: String, amount: BigDecimal): UserBankAccount
            = session.runInTransaction { delegate.addCurrency(account, amount) }

    override fun withdrawCurrency(account: String, amount: BigDecimal): UserBankAccount
            = session.runInTransaction { delegate.withdrawCurrency(account, amount) }

    override fun transaction(from: String, to: String, amount: BigDecimal)
            = session.runInTransaction { delegate.transaction(from, to, amount) }

    override fun closeAccount(account: String): UserBankAccount
            = session.runInTransaction { delegate.closeAccount(account) }

    override fun getAccount(account: String): UserBankAccount
            = session.runInTransaction { delegate.getAccount(account) }
}