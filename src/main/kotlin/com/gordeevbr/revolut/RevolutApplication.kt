package com.gordeevbr.revolut

import mu.KLogging
import java.util.concurrent.TimeUnit


private object RevolutApplication : KLogging()

fun main(args: Array<String>) {
	val serverStarter = AsyncServerStarter().also { it.run() }
	serverStarter.startUpFinishedSync.countDown()
	if (!serverStarter.startUpFinishedSync.await(10, TimeUnit.SECONDS)) {
		throw IllegalStateException("Server did not start")
	}

	RevolutApplication.logger.info { "Inputting anything and pressing <ENTER> would stop the server" }
	readLine()
	serverStarter.runtimeFinishedSync.countDown()

	serverStarter.tearDownFinishedSync.countDown()
	if (!serverStarter.tearDownFinishedSync.await(10, TimeUnit.SECONDS)) {
		throw IllegalStateException("Server did not stop on it's own, tearing it down")
	}
}
