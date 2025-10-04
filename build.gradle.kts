plugins {
    kotlin("jvm") version "2.1.20"
}

group = "custom.project"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-compiler-embeddable
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:2.2.0")
    // https://mvnrepository.com/artifact/com.github.ben-manes.caffeine/caffeine
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.2")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}