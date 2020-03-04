package com.gordeevbr.revolut.e2e

import com.gordeevbr.revolut.entities.Currency
import com.gordeevbr.revolut.web.dtos.AccountOpeningRequestDto
import com.gordeevbr.revolut.web.dtos.UserProfileOpeningRequestDto
import feign.FeignException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class UserProfilesTests: BaseEnd2EndTest() {

    @Test
    fun `can create a profile`() {
        val profile = userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        assertThat(profile.name).isEqualTo("name")
        assertThat(profile.secondName).isEqualTo("secondName")
        assertThat(profile.email).isEqualTo("mail@mail.com")
        assertThat(profile.bankAccounts).isEmpty()
    }

    @Test
    fun `cannot create the same profile twice`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val exception = assertThrows<FeignException> {
            userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        }
        assertThat(exception.status()).isEqualTo(400)
    }

    @Test
    fun `cannot create a profile with invalid email`() {
        val exception = assertThrows<FeignException> {
            userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "invalid"))
        }
        assertThat(exception.status()).isEqualTo(400)
    }

    @Test
    fun `can retrieve the created profile (with no bank accounts)`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val profile = userProfileApi.getProfile("mail@mail.com")
        assertThat(profile.name).isEqualTo("name")
        assertThat(profile.secondName).isEqualTo("secondName")
        assertThat(profile.email).isEqualTo("mail@mail.com")
        assertThat(profile.bankAccounts).isEmpty()
    }

    @Test
    fun `can retrieve the created profile (with existing bank accounts)`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val account = userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.RUB))
        val profile = userProfileApi.getProfile("mail@mail.com")
        assertThat(profile.name).isEqualTo("name")
        assertThat(profile.secondName).isEqualTo("secondName")
        assertThat(profile.email).isEqualTo("mail@mail.com")
        assertThat(profile.bankAccounts).containsExactlyInAnyOrder(account.accountNumber)
    }
private
    @Test
    fun `cannot retrieve a profile that doesn't exist`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val exception = assertThrows<FeignException> {

    @Test
    fun `cannot delete a profile that doesn't exist`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val exception = assertThrows<FeignException> {
            userProfileApi.deleteProfile("mail2@mail.com")
        }
        assertThat(exception.status()).isEqualTo(404)
    }
            userProfileApi.getProfile("mail2@mail.com")
        }
        assertThat(exception.status()).isEqualTo(404)
    }

    @Test
    fun `can delete a profile`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail2@mail.com"))
        userProfileApi.deleteProfile("mail2@mail.com")
        val exception = assertThrows<FeignException> {
            userProfileApi.getProfile("mail2@mail.com")
        }
        assertThat(exception.status()).isEqualTo(404)
    }

    @Test
    fun `cannot delete a profile that doesn't exist`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        val exception = assertThrows<FeignException> {
            userProfileApi.deleteProfile("mail2@mail.com")
        }
        assertThat(exception.status()).isEqualTo(404)
    }

    @Test
    fun `cannot delete a profile if it has unclosed bank accounts`() {
        userProfileApi.createProfile(UserProfileOpeningRequestDto("name", "secondName", "mail@mail.com"))
        userBankAccountApi.openAccount(AccountOpeningRequestDto("mail@mail.com", Currency.RUB))
        val exception = assertThrows<FeignException> {
            userProfileApi.deleteProfile("mail@mail.com")
        }
        assertThat(exception.status()).isEqualTo(400)
    }

}