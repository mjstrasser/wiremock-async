import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.0"
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    val kotlinVersion = "1.4.0"
    val wiremockVersion = "2.27.1"
    val jacksonModuleVersion = "2.11.1"
    val okhttpVersion = "4.8.0"
    val kotlinLoggingVersion = "1.7.9"
    val junit5Version = "5.6.0"
    val spekVersion = "2.0.12"
    val assertkVersion = "0.22"

    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(group = "org.jetbrains.kotlin", name = "kotlin-stdlib-jdk8", version = kotlinVersion)

    implementation(group = "com.github.tomakehurst", name = "wiremock-standalone", version = wiremockVersion)
    implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin", version = jacksonModuleVersion)
    implementation(group = "com.squareup.okhttp3", name = "okhttp", version = okhttpVersion)
    implementation(group = "io.github.microutils", name = "kotlin-logging", version = kotlinLoggingVersion)
    implementation(group = "com.benasher44", name="uuid", version="0.2.1")


    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = junit5Version)
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = junit5Version)

    testImplementation(group = "com.willowtreeapps.assertk", name = "assertk-jvm", version = assertkVersion)

    testImplementation(group = "org.spekframework.spek2", name = "spek-dsl-jvm", version = spekVersion)
    testRuntimeOnly(group = "org.spekframework.spek2", name = "spek-runner-junit5", version = spekVersion)
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.test {
    useJUnitPlatform()
    dependsOn("cleanTest")
}
