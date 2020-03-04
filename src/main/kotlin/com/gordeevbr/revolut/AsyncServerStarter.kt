package com.gordeevbr.revolut

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors


// A convenience class to synchronize with the server running in another thread
class AsyncServerStarter {

    val startUpFinishedSync = CountDownLatch(2)

    val runtimeFinishedSync = CountDownLatch(2)

    val tearDownFinishedSync = CountDownLatch(2)

    fun run() {
        val executor = Executors.newSingleThreadExecutor {
            val thread = Thread(it, "server-thread")
            thread.isDaemon = true
            thread
        }
        executor.submit {
            val context = ContextCreator().createContext()

            val applicationServer = ApplicationServer(context)
            applicationServer.start()
            startUpFinishedSync.countDown()
            startUpFinishedSync.await()

            runtimeFinishedSync.countDown()
            runtimeFinishedSync.await()

            applicationServer.close()
            tearDownFinishedSync.countDown()
            tearDownFinishedSync.await()
        }
        executor.shutdown()
    }

}