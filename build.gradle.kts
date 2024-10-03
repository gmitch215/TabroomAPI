plugins {
    kotlin("multiplatform") version "2.0.20"
    id("org.jetbrains.kotlin.native.cocoapods") version "2.0.20"
    id("org.jetbrains.dokka") version "1.9.20"
    id("com.android.library") version "8.2.2"

    `maven-publish`
}

val v = "0.1.0"

group = "xyz.gmitch215"
version = if (project.hasProperty("snapshot")) "$v-SNAPSHOT" else v
description = "Multiplatform API for Tabroom.com"

repositories {
    mavenCentral()
    mavenLocal()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    jvm()
    js {
        browser {
            testTask {
                useMocha()
            }
        }

        generateTypeScriptDefinitions()
    }

    mingwX64()
    macosX64()
    macosArm64()
    linuxX64()
    linuxArm64()

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    androidTarget {
        publishAllLibraryVariants()
    }

    sourceSets {
        val ktorVersion = "3.0.0-rc-2"

        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
            implementation("io.ktor:ktor-client-core:$ktorVersion")
        }

        jvmMain.dependencies {
            implementation("org.jsoup:jsoup:1.18.1")
            implementation("io.ktor:ktor-client-apache5:$ktorVersion")
        }

        androidMain.dependencies {
            implementation("io.ktor:ktor-client-android:$ktorVersion")
        }

        mingwMain.dependencies {
            implementation("io.ktor:ktor-client-winhttp:$ktorVersion")
        }

        appleMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:$ktorVersion")
        }

        linuxMain.dependencies {
            implementation("io.ktor:ktor-client-cio:$ktorVersion")
        }

        jsMain.dependencies {
            implementation("io.ktor:ktor-client-js:$ktorVersion")
        }
    }
}

android {
    compileSdk = 33
    namespace = "xyz.gmitch215.tabroom-api"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks {
    clean {
        delete("kotlin-js-store")
    }
}

publishing {
    publications {
        filterIsInstance<MavenPublication>().forEach {
            it.apply {
                pom {
                    name = "TabroomAPI"

                    licenses {
                        license {
                            name = "MIT License"
                            url = "https://opensource.org/licenses/MIT"
                        }
                    }

                    scm {
                        connection = "scm:git:git://github.com/gmitch215/TabroomAPI.git"
                        developerConnection = "scm:git:ssh://github.com/gmitch215/TabroomAPI.git"
                        url = "https://github.com/gmitch215/TabroomAPI"
                    }
                }
            }
        }
    }
}