package com.gordeevbr.revolut.e2e

import com.gordeevbr.revolut.AsyncServerStarter
import com.gordeevbr.revolut.apis.UserBankAccountApi
import com.gordeevbr.revolut.apis.UserProfileApi
import feign.Feign
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.util.concurrent.TimeUnit

abstract class BaseEnd2EndTest {

    protected lateinit var userProfileApi: UserProfileApi

    protected lateinit var userBankAccountApi: UserBankAccountApi

    private lateinit var server: AsyncServerStarter

    @BeforeEach
    fun setUp() {
        userProfileApi = Feign
                .builder()
                .encoder(JacksonEncoder())
                .decoder(JacksonDecoder())
                .target(UserProfileApi::class.java, "http://localhost:8080")
        userBankAccountApi = Feign
                .builder()
                .encoder(JacksonEncoder())
                .decoder(JacksonDecoder())
                .target(UserBankAccountApi::class.java, "http://localhost:8080")
        server = AsyncServerStarter().also { it.run() }
        server.startUpFinishedSync.countDown()
        if (!server.startUpFinishedSync.await(10, TimeUnit.SECONDS)) {
            throw IllegalStateException("Server did not start")
        }
    }

    @AfterEach
    fun tearDown() {
        server.runtimeFinishedSync.countDown()

        server.tearDownFinishedSync.countDown()
        if (!server.tearDownFinishedSync.await(10, TimeUnit.SECONDS)) {
            throw IllegalStateException("Server did not stop on it's own, tearing it down")
        }
    }

}