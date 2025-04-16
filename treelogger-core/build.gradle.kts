plugins {
    id("java")
    id("maven-publish")
    kotlin("jvm")
}
java {
    withSourcesJar()
    withJavadocJar()
}

group = "com.github.AlterJuice"
version = "1.0.6"

publishing {
    publications {
        create<MavenPublication>("release") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = "treelogger-core"
            version = project.version.toString()
        }
    }
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

kotlin {
    jvmToolchain(11)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}