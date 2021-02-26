import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.30"
}

repositories {
    jcenter()
    mavenCentral()
}

val kotlinVersion = "1.4.30"
val wiremockVersion = "2.27.1"
val jacksonModuleVersion = "2.11.1"
val okhttpVersion = "4.8.0"
val kotlinLoggingVersion = "1.7.9"
val kotestVersion = "4.2.6"

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

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "11"
}

tasks.test {
    useJUnitPlatform()
    dependsOn("cleanTest")
}
