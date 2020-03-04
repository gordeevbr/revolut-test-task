package com.gordeevbr.revolut.services

import org.hibernate.Session

fun <T> Session.runInTransaction(block: () -> T): T {
    val currentTransaction = transaction
    return if (currentTransaction.isActive) {
        block()
    } else {
        runCatching {
            currentTransaction.begin()
            val res = block()
            flush()
            currentTransaction.commit()
            res
        }.getOrElse {
            runCatching { currentTransaction.rollback() }
            throw it
        }
    }
}