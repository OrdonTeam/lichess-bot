import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    application
}

group = "me.ordon"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":common"))
    implementation("com.github.bhlangonijr:chesslib:1.2.5")
    testImplementation(kotlin("test-junit"))
    testImplementation("org.easytesting:fest-assert-core:2.0M10")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "MainKt"
}