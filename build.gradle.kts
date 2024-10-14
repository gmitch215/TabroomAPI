plugins {
    kotlin("multiplatform") version "2.0.20"
    id("org.jetbrains.kotlin.native.cocoapods") version "2.0.20"
    id("org.jetbrains.dokka") version "1.9.20"
    id("com.android.library") version "8.2.2"

    `maven-publish`
    jacoco
}

val v = "0.1.0"

group = "xyz.gmitch215"
version = if (project.hasProperty("snapshot")) "$v-SNAPSHOT" else v
description = "Multiplatform API for Tabroom.com"

repositories {
    google()
    mavenCentral()
    mavenLocal()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    applyDefaultHierarchyTemplate()

    jvm()
    js {
        nodejs()
        browser {
            testTask {
                enabled = false
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
        val ktorVersion = "3.0.0"

        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
            implementation("io.ktor:ktor-client-core:$ktorVersion")
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
        }

        jvmMain.dependencies {
            implementation("org.jsoup:jsoup:1.18.1")
            implementation("io.ktor:ktor-client-jetty:$ktorVersion")
        }

        androidMain.dependencies {
            implementation("org.jsoup:jsoup:1.18.1")
            implementation("io.ktor:ktor-client-android:$ktorVersion")
        }

        nativeMain.dependencies {
            implementation("com.fleeksoft.ksoup:ksoup-lite:0.1.9")
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
            implementation("com.fleeksoft.ksoup:ksoup-lite:0.1.9")
        }
    }
}

android {
    compileSdk = 33
    namespace = "xyz.gmitch215.tabroomapi"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

tasks {
    clean {
        delete("kotlin-js-store")
    }

    create("jvmJacocoTestReport", JacocoReport::class) {
        dependsOn("jvmTest")

        classDirectories.setFrom(layout.buildDirectory.file("classes/kotlin/jvm/"))
        sourceDirectories.setFrom("src/commonMain/kotlin/", "src/jvmMain/kotlin/")
        executionData.setFrom(layout.buildDirectory.files("jacoco/jvmTest.exec"))

        reports {
            xml.required.set(true)
            xml.outputLocation.set(layout.buildDirectory.file("jacoco.xml"))

            html.required.set(true)
            html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
        }
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
