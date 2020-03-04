package com.gordeevbr.revolut.services

import com.gordeevbr.revolut.entities.UserProfile
import org.hibernate.Session

class TransactionalUserProfileService(
        private val session: Session,
        private val delegate: UserProfileService
): UserProfileService {

    override fun createProfile(firstName: String, secondName: String, email: String): UserProfile
            = session.runInTransaction { delegate.createProfile(firstName, secondName, email) }

    override fun getProfile(email: String): UserProfile
            = session.runInTransaction { delegate.getProfile(email) }

    override fun deleteProfile(email: String)
            = session.runInTransaction { delegate.deleteProfile(email) }
}