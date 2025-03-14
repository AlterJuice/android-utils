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
version = "v1.0.0"

publishing {
    publications {
        create<MavenPublication>("release") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = "str-core"
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
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}