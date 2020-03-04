package com.gordeevbr.revolut.services

import com.gordeevbr.revolut.entities.UserProfile
import com.gordeevbr.revolut.exceptions.CannotDeleteException
import com.gordeevbr.revolut.exceptions.EntityNotFoundException
import com.gordeevbr.revolut.exceptions.InvalidValueException
import mu.KLogging
import org.hibernate.Session
import javax.validation.Validator

class SessionUserProfileService(
        private val session: Session,
        private val validator: Validator
): UserProfileService {

    private companion object : KLogging()

    override fun createProfile(firstName: String, secondName: String, email: String): UserProfile {
        val profile = UserProfile(0, firstName, secondName, email, mutableListOf())
        validate(profile)
        session.saveOrUpdate(profile)
        return profile
    }

    override fun getProfile(email: String): UserProfile = session
            .createQuery("SELECT p FROM UserProfile p WHERE p.email LIKE :email")
            .setParameter("email", email)
            .resultStream
            .findFirst()
            .map { it as UserProfile }
            .orElse(null) ?: throw EntityNotFoundException("profile")

    override fun deleteProfile(email: String): UserProfile {
        val profile = getProfile(email)
        if (profile.bankAccounts.isNotEmpty()) {
            throw CannotDeleteException()
        }
        session.delete(profile)
        return profile
    }

    private fun <T> validate(entity: T) {
        val result = validator.validate(entity)
        result.forEach {
            logger.debug { "Validation error: $it" }
            throw InvalidValueException(it.invalidValue.toString())
        }
    }
}