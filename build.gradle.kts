import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.subgarden"
version = "0.6-SNAPSHOT"

val kotlinVersion = extra["kotlinVersion"] as String
val ktorVersion = extra["ktorVersion"] as String
val graphqlJavaVersion = extra["graphqlJavaVersion"] as String

plugins {
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.serialization").version("1.8.22")
    application
    java
}

@Suppress("UNUSED_VARIABLE")
buildscript {

    val ktorVersion by extra { "2.3.4" }
    val kotlinVersion by extra { "1.8.22" }
    val graphqlJavaVersion by extra { "21.1" }

    repositories {
        maven { url = uri("https://dl.bintray.com/kotlin/ktor") }
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("com.subgarden.backend.MainKt")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    maven { url = uri("https://dl.bintray.com/kotlin/ktor") }
}


dependencies {
    implementation(kotlin("stdlib-jdk8", kotlinVersion))
    implementation("com.graphql-java:graphql-java:$graphqlJavaVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-gson:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.2.1")
    testImplementation("junit:junit:4.12")
}
