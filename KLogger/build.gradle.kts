import org.jetbrains.kotlin.fir.declarations.builder.buildTypeAlias

plugins {
    kotlin("jvm") version "1.8.21"
    id("maven-publish")
}

group = "org.setruth.klogger"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

publishing{
    publications {
        register<MavenPublication>("release"){
            groupId="org.setruth.klogger"
            artifactId="KLogger"
            version="1.0.0"
        }
    }
}
