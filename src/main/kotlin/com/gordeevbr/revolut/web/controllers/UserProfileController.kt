package com.gordeevbr.revolut.web.controllers

import com.gordeevbr.revolut.exceptions.MissingMandatoryQueryParameter
import com.gordeevbr.revolut.services.UserProfileService
import com.gordeevbr.revolut.web.*
import com.gordeevbr.revolut.web.dtos.UserProfileOpeningRequestDto
import com.gordeevbr.revolut.web.dtos.toDto
import com.sun.net.httpserver.HttpExchange

@Controller
class UserProfileController(private val profileService: UserProfileService) {

    @RestMethod("/user/profile", method = Method.POST)
    fun createProfile(exchange: HttpExchange) {
        val request = exchange.readJson<UserProfileOpeningRequestDto>()
        val result = profileService.createProfile(request.name, request.secondName, request.email)
        exchange.respondTyped(200, result.toDto())
    }

    @RestMethod("/user/profile", method = Method.GET)
    fun getProfile(exchange: HttpExchange) {
        val requestedEmail = exchange.getQueryParameter("email") ?: throw MissingMandatoryQueryParameter("email")
        val result = profileService.getProfile(requestedEmail)
        exchange.respondTyped(200, result.toDto())
    }

    @RestMethod("/user/profile", method = Method.DELETE)
    fun deleteProfile(exchange: HttpExchange) {
        val requestedEmail = exchange.getQueryParameter("email") ?: throw MissingMandatoryQueryParameter("email")
        val result = profileService.deleteProfile(requestedEmail)
        exchange.respondTyped(200, result.toDto())
    }

}