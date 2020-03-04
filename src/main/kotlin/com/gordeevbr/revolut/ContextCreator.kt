package com.gordeevbr.revolut

import com.gordeevbr.revolut.entities.UserBankAccount
import com.gordeevbr.revolut.entities.UserProfile
import com.gordeevbr.revolut.services.SessionUserBankAccountService
import com.gordeevbr.revolut.services.SessionUserProfileService
import com.gordeevbr.revolut.services.TransactionalUserBankAccountService
import com.gordeevbr.revolut.services.TransactionalUserProfileService
import com.gordeevbr.revolut.web.controllers.UserBankAccountController
import com.gordeevbr.revolut.web.controllers.UserProfileController
import org.h2.Driver
import org.h2.tools.Server
import org.hibernate.Session
import org.hibernate.dialect.H2Dialect
import org.hibernate.jpa.HibernatePersistenceProvider
import java.io.Closeable
import java.net.URL
import java.util.*
import javax.persistence.SharedCacheMode
import javax.persistence.ValidationMode
import javax.persistence.spi.ClassTransformer
import javax.persistence.spi.PersistenceUnitInfo
import javax.persistence.spi.PersistenceUnitTransactionType
import javax.sql.DataSource
import javax.validation.Validation


class ContextCreator {

    private companion object {

        const val DB_PORT = 9123

        val DB_DRIVER = Driver::class.java

        val DB_PARAMETERS = arrayOf("-tcpPort", DB_PORT.toString(), "-tcpAllowOthers")

        const val DB_URL = "jdbc:h2:mem:"

        const val DB_USERNAME = "sa"

        const val DB_PASSWORD = ""

        const val HIBERNATE_UNIT_NAME = "com.gordeevbr.revolut"

        val HIBERNATE_DIALECT = H2Dialect::class.java

        const val HIBERNATE_DDL_AUTO = "create-drop"

        val MANAGED_CLASSES = listOf(UserProfile::class.java, UserBankAccount::class.java)

        const val SHOW_SQL = true
    }

    // I don't use any DI framework, therefore I am constructing all the beans manually in this method
    fun createContext(): Collection<Any> {
        val server = Server.createTcpServer(*DB_PARAMETERS).start()

        val entityManagerFactory = HibernatePersistenceProvider().createContainerEntityManagerFactory(PersistenceUnitImpl(), mapOf(
                "hibernate.hbm2ddl.auto" to HIBERNATE_DDL_AUTO,
                "hibernate.connection.driver_class" to DB_DRIVER.name,
                "hibernate.connection.url" to DB_URL,
                "hibernate.connection.username" to DB_USERNAME,
                "hibernate.connection.password" to DB_PASSWORD,
                "hibernate.dialect" to HIBERNATE_DIALECT.name,
                "hibernate.show_sql" to SHOW_SQL.toString()

        ))
        val session = entityManagerFactory.createEntityManager() as Session

        val factory = Validation.buildDefaultValidatorFactory()
        val validator = factory.validator

        val profileService = TransactionalUserProfileService(
                session,
                SessionUserProfileService(session, validator)
        )

        val bankAccountService = TransactionalUserBankAccountService(
                session,
                SessionUserBankAccountService(session, profileService)
        )

        val userProfileController = UserProfileController(profileService)

        val userBankAccountController = UserBankAccountController(bankAccountService)

        return listOf(
                server,
                Closeable { server.stop() },
                session,
                entityManagerFactory,
                factory,
                validator,
                profileService,
                bankAccountService,
                userProfileController,
                userBankAccountController
        )
    }

    private class PersistenceUnitImpl: PersistenceUnitInfo {

        override fun getPersistenceUnitRootUrl(): URL? = null
        override fun getJtaDataSource(): DataSource? = null
        override fun getMappingFileNames(): List<String> = emptyList()
        override fun getNewTempClassLoader(): ClassLoader? = null
        override fun getPersistenceUnitName(): String = HIBERNATE_UNIT_NAME
        override fun getSharedCacheMode(): SharedCacheMode = SharedCacheMode.UNSPECIFIED
        override fun getClassLoader(): ClassLoader? = null
        override fun getTransactionType(): PersistenceUnitTransactionType = PersistenceUnitTransactionType.RESOURCE_LOCAL
        override fun getProperties(): Properties = Properties()
        override fun getPersistenceXMLSchemaVersion(): String? = null
        override fun addTransformer(transformer: ClassTransformer?) = Unit
        override fun getManagedClassNames(): List<String> = MANAGED_CLASSES.map { it.name }
        override fun getJarFileUrls(): List<URL> = ContextCreator::class.java.classLoader.getResources("").toList()
        override fun getPersistenceProviderClassName(): String = HibernatePersistenceProvider::class.java.name
        override fun getNonJtaDataSource(): DataSource? = null
        override fun excludeUnlistedClasses(): Boolean = false
        override fun getValidationMode(): ValidationMode = ValidationMode.AUTO

    }

}