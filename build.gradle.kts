import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.3.61"
	id("org.jetbrains.kotlin.plugin.noarg") version "1.3.61"
}

noArg {
	annotation("com.gordeevbr.revolut.kotlin.NoArg")
}

group = "com.gordeevbr"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	//	Kotlin
	implementation("org.jetbrains.kotlin:kotlin-reflect:1.3.61")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.61")

	// Jackson
	implementation("com.fasterxml.jackson.core:jackson-core:2.10.2")
	implementation("com.fasterxml.jackson.core:jackson-databind:2.10.2")
	implementation("com.fasterxml.jackson.core:jackson-annotations:2.10.2")

	// Logging
	implementation("io.github.microutils:kotlin-logging:1.7.8")
	implementation("ch.qos.logback:logback-core:1.2.3")
	implementation("ch.qos.logback:logback-classic:1.2.3")
	implementation("org.slf4j:slf4j-api:1.7.30")

	// Persistence
	implementation("com.h2database:h2:1.4.200")
	implementation("org.hibernate:hibernate-core:5.4.12.Final")
	implementation("org.hibernate:hibernate-validator:6.1.2.Final")
	implementation("org.glassfish:javax.el:3.0.0")

	// Feign
	implementation("io.github.openfeign:feign-core:10.8")
	implementation("io.github.openfeign:feign-jackson:10.8")

	// Testing
	testImplementation("org.junit.jupiter:junit-jupiter:5.6.0")
	testImplementation("org.assertj:assertj-core:3.15.0")

}

tasks.withType<Test> {
	useJUnitPlatform()
	testLogging {
		events("passed", "skipped", "failed")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}
