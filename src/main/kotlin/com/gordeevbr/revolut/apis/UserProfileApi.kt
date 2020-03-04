package com.gordeevbr.revolut.apis

import com.gordeevbr.revolut.web.dtos.UserProfileDto
import com.gordeevbr.revolut.web.dtos.UserProfileOpeningRequestDto
import feign.Headers
import feign.Param
import feign.RequestLine

interface UserProfileApi {

    @RequestLine("POST /user/profile")
    @Headers("Content-Type: application/json")
    fun createProfile(openingRequestDto: UserProfileOpeningRequestDto): UserProfileDto

    @RequestLine("GET /user/profile?email={email}")
    fun getProfile(@Param("email") email: String): UserProfileDto

    @RequestLine("DELETE /user/profile?email={email}")
    fun deleteProfile(@Param("email") email: String): UserProfileDto
}