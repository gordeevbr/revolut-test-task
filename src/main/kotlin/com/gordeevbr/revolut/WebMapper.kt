package com.gordeevbr.revolut

import com.gordeevbr.revolut.web.Controller
import com.gordeevbr.revolut.web.Method
import com.gordeevbr.revolut.web.RestMethod
import com.gordeevbr.revolut.web.RestMethodDescriptor
import com.gordeevbr.revolut.exceptions.PathNotFoundException
import com.gordeevbr.revolut.exceptions.UnsupportedMethodException
import com.sun.net.httpserver.HttpExchange
import mu.KLogging
import java.lang.reflect.InvocationTargetException

class WebMapper(applicationContext: Collection<Any>) {

    private companion object: KLogging()

    private val controllerMethods: Map<RestMethodDescriptor, (HttpExchange) -> Unit> = applicationContext
            .asSequence()
            .filter { bean -> bean.javaClass.annotations.any { it is Controller } }
            .flatMap { bean -> bean.javaClass.methods.asSequence().filter { method ->
                method.annotations.any { it is RestMethod }
            }.map { it to bean } }
            .groupBy(
                    { (method, _) -> ((method.annotations.first { it is RestMethod }) as RestMethod).let {
                        restMethod -> RestMethodDescriptor(restMethod.path.trimEnd('/'), restMethod.method)
                    } },
                    { (method, bean) ->  { exchange: HttpExchange ->
                        runCatching { method.invoke(bean, exchange) }
                                .recover {
                                    if (it is InvocationTargetException && it.cause is Exception) {
                                        throw it.cause as Exception
                                    }
                                }
                        Unit
                    } }
            )
            .mapValues { (path, methods) ->
                if (methods.size > 1) {
                    throw IllegalStateException("More than one handler for path $path")
                }

                methods.first()
            }

    fun call(exchange: HttpExchange) {
        if (Method.values().none { it.value == exchange.requestMethod }) {
            throw UnsupportedMethodException(exchange.requestMethod)
        }

        val method = Method.valueOf(exchange.requestMethod)
        val uri = exchange.requestURI.path.trimEnd('/')
        val restMethod = RestMethodDescriptor(uri, method)

        logger.debug { "Handling a request for ${restMethod.method.value} ${restMethod.path}" }

        controllerMethods[restMethod]?.invoke(exchange) ?: throw PathNotFoundException(restMethod)
    }
}