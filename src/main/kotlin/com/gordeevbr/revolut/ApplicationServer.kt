package com.gordeevbr.revolut

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import mu.KLogging
import java.io.Closeable
import java.net.InetSocketAddress
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class ApplicationServer(
        private val applicationContext: Collection<Any>,
        private val port: Int = 8080,
        private val threadPool: ExecutorService = provideThreadPool(),
        private val errorHandler: ErrorHandler = ErrorHandler(),
        private val webMapper: WebMapper = WebMapper(applicationContext)
): Closeable {

    private companion object: KLogging() {

        fun provideThreadPool(): ExecutorService {
            val counter = AtomicInteger(0)
            return Executors.newCachedThreadPool {
                val thread = Thread(it, "web-thread-${counter.getAndIncrement()}")
                thread.isDaemon = true
                thread
            }
        }

    }

    private val server: HttpServer by lazy { HttpServer.create(InetSocketAddress("127.0.0.1", port), 0) }

    fun start() {
        server.createContext("/", this::handle)
        server.executor = threadPool
        server.start()
        logger.info { "The application server is working on port $port" }
    }

    private fun handle(exchange: HttpExchange) {
        runCatching { webMapper.call(exchange) }
                .recover { errorHandler.handle(exchange, it) }
        exchange.close()
    }

    override fun close() {
        server.stop(0)
        threadPool.shutdownNow()
        applicationContext.filterIsInstance<AutoCloseable>().forEach {
            runCatching { it.close() }
                    .recover { logger.error(it) { "Error when closing resource" } }
        }
        logger.info { "The application server was stopped" }
    }

}