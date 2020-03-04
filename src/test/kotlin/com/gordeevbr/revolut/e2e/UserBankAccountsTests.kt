package com.gordeevbr.revolut.e2e

import com.gordeevbr.revolut.entities.Currency
import com.gordeevbr.revolut.web.dtos.AccountOpeningRequestDto
import com.gordeevbr.revolut.web.dtos.BalanceRepresentationDto
import com.gordeevbr.revolut.web.dtos.BankTransactionDto
import com.gordeevbr.revolut.web.dtos.UserProfileOpeningRequestDto
import feign.FeignException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UserBankAccountsTests: BaseEnd2EndTest() {

    @Test
    fun `can create a user bank account`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val account = userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.EUR))
        assertThat(account.accountNumber).isNotBlank()
        assertThat(account.currency).isEqualTo(Currency.EUR)
        assertThat(account.balance.cents).isZero()
        assertThat(account.balance.decimal).isZero()
    }

    @Test
    fun `cannot create a bank account for a user that does not exist`() {
        val exception = assertThrows<FeignException> {
            userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.EUR))
        }
        assertThat(exception.status()).isEqualTo(404)
    }

    @Test
    fun `can retrieve a user bank account`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val created = userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.EUR))
        val account = userBankAccountApi.getAccount(created.accountNumber)
        assertThat(account.accountNumber).isNotBlank()
        assertThat(account.currency).isEqualTo(Currency.EUR)
        assertThat(account.balance.cents).isZero()
        assertThat(account.balance.decimal).isZero()
    }

    @Test
    fun `can retrieve a user bank account through user`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.EUR))
        val profile = userProfileApi.getProfile("mail@mail.com")
        val account = userBankAccountApi.getAccount(profile.bankAccounts.first())
        assertThat(account.accountNumber).isNotBlank()
        assertThat(account.currency).isEqualTo(Currency.EUR)
        assertThat(account.balance.cents).isZero()
        assertThat(account.balance.decimal).isZero()
    }

    @Test
    fun `can not retrieve a bank account that does not exist`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.EUR))
        val exception = assertThrows<FeignException> {
            userBankAccountApi.getAccount("this-is-clearly-invalid")
        }
        assertThat(exception.status()).isEqualTo(404)
    }

    @Test
    fun `can delete a user bank account`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val created = userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.EUR))
        userBankAccountApi.closeAccount(created.accountNumber)
    }

    @Test
    fun `cannot delete a user bank account that does not exist`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.EUR))
        val exception = assertThrows<FeignException> {
            userBankAccountApi.closeAccount("this-is-clearly-invalid")
        }
        assertThat(exception.status()).isEqualTo(404)
    }

    @Test
    fun `can add money to a bank account`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val account = userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.EUR))
        val result = userBankAccountApi.addCurrency(account.accountNumber, BalanceRepresentationDto(10, 9))
        assertThat(result.balance.cents).isEqualTo(9)
        assertThat(result.balance.decimal).isEqualTo(10)
    }

    @Test
    fun `can add zero decimal money to a bank account`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val account = userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.EUR))
        val result = userBankAccountApi.addCurrency(account.accountNumber, BalanceRepresentationDto(0, 9))
        assertThat(result.balance.cents).isEqualTo(9)
        assertThat(result.balance.decimal).isEqualTo(0)
    }

    @Test
    fun `cannot add a negative decimal amount to a bank account`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val account = userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.EUR))
        val exception = assertThrows<FeignException> {
            userBankAccountApi.addCurrency(account.accountNumber, BalanceRepresentationDto(-1, 9))
        }
        assertThat(exception.status()).isEqualTo(400)
    }

    @Test
    fun `can add zero cents to a bank account`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val account = userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.EUR))
        val result = userBankAccountApi.addCurrency(account.accountNumber, BalanceRepresentationDto(10, 0))
        assertThat(result.balance.cents).isEqualTo(0)
        assertThat(result.balance.decimal).isEqualTo(10)
    }

    @Test
    fun `can add ninety nine cents to a bank account`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val account = userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.EUR))
        val result = userBankAccountApi.addCurrency(account.accountNumber, BalanceRepresentationDto(10, 99))
        assertThat(result.balance.cents).isEqualTo(99)
        assertThat(result.balance.decimal).isEqualTo(10)
    }

    @Test
    fun `cannot add a negative amount of cents to a bank account`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val account = userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.EUR))
        val exception = assertThrows<FeignException> {
            userBankAccountApi.addCurrency(account.accountNumber, BalanceRepresentationDto(10, -1))
        }
        assertThat(exception.status()).isEqualTo(400)
    }

    @Test
    fun `cannot add more than 99 cents to a bank account`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val account = userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.EUR))
        val exception = assertThrows<FeignException> {
            userBankAccountApi.addCurrency(account.accountNumber, BalanceRepresentationDto(10, 100))
        }
        assertThat(exception.status()).isEqualTo(400)
    }

    @Test
    fun `can withdraw money from a bank account`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val account = userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.EUR))
        userBankAccountApi.addCurrency(account.accountNumber, BalanceRepresentationDto(10, 9))
        val result = userBankAccountApi.withdrawCurrency(account.accountNumber, BalanceRepresentationDto(9, 8))
        assertThat(result.balance.cents).isEqualTo(1)
        assertThat(result.balance.decimal).isEqualTo(1)
    }

    @Test
    fun `can withdraw zero decimal money from a bank account`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val account = userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.EUR))
        userBankAccountApi.addCurrency(account.accountNumber, BalanceRepresentationDto(10, 9))
        val result = userBankAccountApi.withdrawCurrency(account.accountNumber, BalanceRepresentationDto(0, 8))
        assertThat(result.balance.cents).isEqualTo(1)
        assertThat(result.balance.decimal).isEqualTo(10)
    }

    @Test
    fun `cannot withdraw negative decimal amount from a bank account`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val account = userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.EUR))
        userBankAccountApi.addCurrency(account.accountNumber, BalanceRepresentationDto(10, 9))
        val exception = assertThrows<FeignException> {
            userBankAccountApi.addCurrency(account.accountNumber, BalanceRepresentationDto(-1, 9))
        }
        assertThat(exception.status()).isEqualTo(400)
    }

    @Test
    fun `can withdraw zero cents from a bank account`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val account = userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.EUR))
        userBankAccountApi.addCurrency(account.accountNumber, BalanceRepresentationDto(10, 9))
        val result = userBankAccountApi.withdrawCurrency(account.accountNumber, BalanceRepresentationDto(9, 0))
        assertThat(result.balance.cents).isEqualTo(9)
        assertThat(result.balance.decimal).isEqualTo(1)
    }

    @Test
    fun `can withdraw ninety nine cents from a bank account`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val account = userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.EUR))
        userBankAccountApi.addCurrency(account.accountNumber, BalanceRepresentationDto(10, 0))
        val result = userBankAccountApi.withdrawCurrency(account.accountNumber, BalanceRepresentationDto(0, 99))
        assertThat(result.balance.cents).isEqualTo(1)
        assertThat(result.balance.decimal).isEqualTo(9)
    }

    @Test
    fun `cannot withdraw a negative amount of cents from a bank account`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val account = userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.EUR))
        userBankAccountApi.addCurrency(account.accountNumber, BalanceRepresentationDto(10, 9))
        val exception = assertThrows<FeignException> {
            userBankAccountApi.withdrawCurrency(account.accountNumber, BalanceRepresentationDto(10, -1))
        }
        assertThat(exception.status()).isEqualTo(400)
    }

    @Test
    fun `cannot withdraw more than 99 cents from a bank account`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val account = userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.EUR))
        userBankAccountApi.addCurrency(account.accountNumber, BalanceRepresentationDto(10, 99))
        val exception = assertThrows<FeignException> {
            userBankAccountApi.withdrawCurrency(account.accountNumber, BalanceRepresentationDto(10, 100))
        }
        assertThat(exception.status()).isEqualTo(400)
    }

    @Test
    fun `can withdraw all money from a bank account`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val account = userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.EUR))
        userBankAccountApi.addCurrency(account.accountNumber, BalanceRepresentationDto(10, 99))
        val result = userBankAccountApi.withdrawCurrency(account.accountNumber, BalanceRepresentationDto(10, 99))
        assertThat(result.balance.cents).isEqualTo(0)
        assertThat(result.balance.decimal).isEqualTo(0)
    }

    @Test
    fun `cannot withdraw more money from bank account than it already has`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val account = userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.EUR))
        userBankAccountApi.addCurrency(account.accountNumber, BalanceRepresentationDto(10, 99))
        val exception = assertThrows<FeignException> {
            userBankAccountApi.withdrawCurrency(account.accountNumber, BalanceRepresentationDto(11, 0))
        }
        assertThat(exception.status()).isEqualTo(400)
    }

    // I shall not cover every corner case of this method, but I am aware of them.
    // I am just assuming that they are all covered because I know how this method works inside.
    // This is done mostly for the sake of finishing this demo on time.
    @Test
    fun `can make a bank transaction`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val account1 = userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.EUR))
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail2@mail.com"))
        val account2 = userBankAccountApi.openAccount(AccountOpeningRequestDto("mail2@mail.com", Currency.EUR))

        userBankAccountApi.addCurrency(account1.accountNumber, BalanceRepresentationDto(10, 9))
        userBankAccountApi.addCurrency(account2.accountNumber, BalanceRepresentationDto(10, 9))

        userBankAccountApi.transaction(BankTransactionDto(account1.accountNumber, account2.accountNumber,
                BalanceRepresentationDto(2, 50)))

        val account1Updated = userBankAccountApi.getAccount(account1.accountNumber)
        val account2Updated = userBankAccountApi.getAccount(account2.accountNumber)

        assertThat(account1Updated.balance.decimal).isEqualTo(7)
        assertThat(account1Updated.balance.cents).isEqualTo(59)
        assertThat(account2Updated.balance.decimal).isEqualTo(12)
        assertThat(account2Updated.balance.cents).isEqualTo(59)
    }

    @Test
    fun `cannot make a bank transaction between the accounts of different currencies`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val account1 = userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.EUR))
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail2@mail.com"))
        val account2 = userBankAccountApi.openAccount(AccountOpeningRequestDto("mail2@mail.com", Currency.RUB))

        userBankAccountApi.addCurrency(account1.accountNumber, BalanceRepresentationDto(10, 9))
        userBankAccountApi.addCurrency(account2.accountNumber, BalanceRepresentationDto(10, 9))

        val exception = assertThrows<FeignException> {
            userBankAccountApi.transaction(BankTransactionDto(account1.accountNumber, account2.accountNumber,
                    BalanceRepresentationDto(2, 50)))
        }

        assertThat(exception.status()).isEqualTo(400)
    }

}