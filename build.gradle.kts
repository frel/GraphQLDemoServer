import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.subgarden"
version = "0.5-SNAPSHOT"

val kotlinVersion = extra["kotlinVersion"] as String
val ktorVersion = extra["ktorVersion"] as String
val graphqlJavaVersion = extra["graphqlJavaVersion"] as String

plugins {
    kotlin("jvm") version "1.3.72"
    application
    java
}

@Suppress("UNUSED_VARIABLE")
buildscript {

    val ktorVersion by extra { "1.3.2" }
    val kotlinVersion by extra { "1.3.72" }
    val graphqlJavaVersion by extra { "14.0" }

    repositories {
        maven { url = uri("https://dl.bintray.com/kotlin/ktor") }
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "com.subgarden.backend.MainKt"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    experimental.coroutines = Coroutines.ENABLE
}

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("https://dl.bintray.com/kotlin/ktor") }
}


dependencies {
    implementation(kotlin("stdlib-jdk8", kotlinVersion))
    implementation("com.graphql-java:graphql-java:$graphqlJavaVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-gson:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.2.1")
    testImplementation("junit:junit:4.12")
}
