package com.gordeevbr.revolut.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.gordeevbr.revolut.exceptions.InvalidBodyException
import com.gordeevbr.revolut.exceptions.InvalidContentTypeException
import com.sun.net.httpserver.HttpExchange
import mu.KLogging
import java.io.IOException

val MAPPER = ObjectMapper()

object JsonReader: KLogging()

inline fun <reified T> HttpExchange.readJson(): T {
    val contentHeader = requestHeaders[CONTENT_TYPE]?.firstOrNull()

    if (contentHeader?.startsWith(JSON, true) != true) {
        throw InvalidContentTypeException(JSON, contentHeader ?: "")
    }

    val rawBody = requestBody?.readAllBytes()?.let { String(it) } ?: throw InvalidBodyException("null")

    return runCatching { MAPPER.readValue(rawBody, T::class.java) }
            .recover {
                if (it is IOException) {
                    JsonReader.logger.debug(it) { "Failed to read json: $rawBody" }
                    throw InvalidBodyException(rawBody)
                }
                throw it
            }.getOrThrow()
}

fun HttpExchange.respond(code: Int, body: Body? = null) {
    this.sendResponseHeaders(code, body?.content?.size?.toLong() ?: 0)
    body?.let {
        this.responseBody.write(it.content)
        this.responseHeaders.add(CONTENT_TYPE, it.contentType)
    }
}

fun HttpExchange.respondTyped(code: Int, body: Any) =
        respond(code, Body(MAPPER.writeValueAsBytes(body), JSON))

class Body(
        val content: ByteArray,
        val contentType: String
)

fun HttpExchange.getQueryParameter(key: String): String? =
        requestURI.query?.split('&')
                ?.filter { it.split('=').first() == key }
                ?.map { it.split('=').last() }
                ?.firstOrNull()