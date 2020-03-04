package com.gordeevbr.revolut.services

import com.gordeevbr.revolut.entities.UserProfile

interface UserProfileService {

    fun createProfile(firstName: String, secondName: String, email: String): UserProfile

    fun getProfile(email: String): UserProfile

    fun deleteProfile(email: String): UserProfile

}