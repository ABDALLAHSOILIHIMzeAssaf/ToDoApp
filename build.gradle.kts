plugins {
<<<<<<< HEAD
    kotlin("jvm") version "2.3.21"
    kotlin("plugin.spring") version "2.3.21"
    id("org.springframework.boot") version "4.1.2"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "2.3.21"
=======
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "1.9.25"
>>>>>>> 589103cd0de44b8b8b0d87d73f12befa1a43b2e0
}

group = "org.ldv"
version = "0.0.1-SNAPSHOT"
<<<<<<< HEAD
description = "TodoApp2"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
=======
description = "TodoApp"

java {
    toolchain {
        // Aligné sur Java 17
        languageVersion.set(JavaLanguageVersion.of(17))
>>>>>>> 589103cd0de44b8b8b0d87d73f12befa1a43b2e0
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
<<<<<<< HEAD
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-thymeleaf-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
=======
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
>>>>>>> 589103cd0de44b8b8b0d87d73f12befa1a43b2e0
}

kotlin {
    compilerOptions {
<<<<<<< HEAD
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
=======
        freeCompilerArgs.addAll("-Xjsr305=strict")
        // Aligne automatiquement le compilateur Kotlin sur la version de la toolchain Java (17)
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
>>>>>>> 589103cd0de44b8b8b0d87d73f12befa1a43b2e0
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
