import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.31"
}

repositories {
    jcenter()
    mavenCentral()
}

val kotlinVersion: String by project
val wiremockVersion: String by project
val jacksonModuleVersion: String by project
val okhttpVersion: String by project
val kotlinLoggingVersion: String by project
val kotestVersion: String by project

dependencies {

    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("com.github.tomakehurst:wiremock-standalone:$wiremockVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonModuleVersion")
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")

    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
}

sourceSets {
    create("callbackTest") {
        kotlin {
            compileClasspath += main.get().output + configurations.testRuntimeClasspath
            runtimeClasspath += output + compileClasspath
        }
    }
}

val callbackTest = task<Test>("callbackTest") {
    description = "Runs the CallbackTest tests"
    group = "verification"
    testClassesDirs = sourceSets["callbackTest"].output.classesDirs
    classpath = sourceSets["callbackTest"].runtimeClasspath
}

tasks.check {
    dependsOn(callbackTest)
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "11"
}

tasks.test {
    useJUnitPlatform()
    dependsOn("cleanTest")
}
