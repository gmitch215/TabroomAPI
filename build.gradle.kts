@file:OptIn(ExperimentalWasmDsl::class, ExperimentalDistributionDsl::class)

import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JsModuleKind
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrLink
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackOutput

plugins {
    kotlin("multiplatform") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    id("org.jetbrains.kotlin.native.cocoapods") version "2.1.20"
    id("org.jetbrains.dokka") version "2.0.0"
    id("com.android.library") version "8.9.2"
    id("com.vanniktech.maven.publish") version "0.31.0"
    id("dev.petuska.npm.publish") version "3.5.3"

    `maven-publish`
    jacoco
    signing
}

val v = "0.3.0"

group = "dev.gmitch215"
version = "${if (project.hasProperty("snapshot")) "$v-SNAPSHOT" else v}${project.findProperty("suffix")?.toString()?.run { "-${this}" } ?: ""}"
val desc = "Multiplatform API for Tabroom.com"
description = desc

repositories {
    google()
    mavenCentral()
    mavenLocal()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    configureSourceSets()
    applyDefaultHierarchyTemplate()
    withSourcesJar()

    jvm()
    js {
        browser {
            webpackTask {
                mainOutputFileName = "${project.name}-${project.version}.js"
                output.library = "tabroom"
            }

            testTask {
                useMocha {
                    timeout = "10m"
                }
            }
        }

        binaries.library()
        binaries.executable()
        generateTypeScriptDefinitions()
    }

    mingwX64()
    macosX64()
    macosArm64()
    linuxX64()

    iosX64()
    iosArm64()
    androidTarget {
        publishAllLibraryVariants()
    }

    tvosArm64()
    tvosX64()
    watchosArm32()
    watchosArm64()

    sourceSets {
        val ktorVersion = "3.1.2"
        val ksoupVersion = "0.2.0"

        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
            implementation("io.ktor:ktor-client-core:$ktorVersion")
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
        }

        jvmMain.dependencies {
            implementation("org.jsoup:jsoup:1.19.1")
            implementation("io.ktor:ktor-client-java:$ktorVersion")
            implementation("ch.qos.logback:logback-classic:1.5.18")
        }

        androidMain.dependencies {
            implementation("org.jsoup:jsoup:1.19.1")
            implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
        }

        nativeMain.dependencies {
            implementation("com.fleeksoft.ksoup:ksoup-lite:$ksoupVersion")
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
            implementation("com.fleeksoft.ksoup:ksoup-lite:$ksoupVersion")
        }
    }
}

fun KotlinMultiplatformExtension.configureSourceSets() {
    sourceSets
        .matching { it.name !in listOf("main", "test") }
        .all {
            val srcDir = if ("Test" in name) "test" else "main"
            val resourcesPrefix = if (name.endsWith("Test")) "test-" else ""
            val platform = when {
                (name.endsWith("Main") || name.endsWith("Test")) && "android" !in name -> name.dropLast(4)
                else -> name.substringBefore(name.first { it.isUpperCase() })
            }

            kotlin.srcDir("src/$platform/$srcDir")
            resources.srcDir("src/$platform/${resourcesPrefix}resources")

            languageSettings.apply {
                progressiveMode = true
            }
        }
}

android {
    compileSdk = 33
    namespace = "dev.gmitch215.tabroomapi"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

tasks {
    clean {
        delete("kotlin-js-store")
    }

    named("jsBrowserProductionLibraryDistribution") {
        dependsOn("jsProductionExecutableCompileSync")
    }

    named("jsBrowserProductionWebpack") {
        dependsOn("jsProductionLibraryCompileSync")
    }

    register("jvmJacocoTestReport", JacocoReport::class) {
        dependsOn("jvmTest")

        classDirectories.setFrom(layout.buildDirectory.file("classes/kotlin/jvm/"))
        sourceDirectories.setFrom("src/common/main/", "src/jvm/main/")
        executionData.setFrom(layout.buildDirectory.files("jacoco/jvmTest.exec"))

        reports {
            xml.required.set(true)
            xml.outputLocation.set(layout.buildDirectory.file("jacoco.xml"))

            html.required.set(true)
            html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project

    if (signingKey != null && signingPassword != null)
        useInMemoryPgpKeys(signingKey, signingPassword)

    sign(publishing.publications)
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

                    developers {
                        developer {
                            id = "gmitch215"
                            name = "Gregory Mitchell"
                            email = "me@gmitch215.xyz"
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

    repositories {
        maven {
            name = "CalculusGames"
            credentials {
                username = System.getenv("NEXUS_USERNAME")
                password = System.getenv("NEXUS_PASSWORD")
            }

            val releases = "https://repo.calcugames.xyz/repository/maven-releases/"
            val snapshots = "https://repo.calcugames.xyz/repository/maven-snapshots/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshots else releases)
        }

        if (!version.toString().endsWith("SNAPSHOT")) {
            maven {
                name = "GithubPackages"
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }

                url = uri("https://maven.pkg.github.com/gmitch215/TabroomAPI")
            }
        }
    }
}

mavenPublishing {
    coordinates(project.group.toString(), project.name, project.version.toString())

    pom {
        name.set("TabroomAPI")
        description.set(desc)
        url.set("https://github.com/gmitch215/TabroomAPI")
        inceptionYear.set("2024")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        developers {
            developer {
                id = "gmitch215"
                name = "Gregory Mitchell"
                email = "me@gmitch215.xyz"
            }
        }

        scm {
            connection = "scm:git:git://github.com/gmitch215/TabroomAPI.git"
            developerConnection = "scm:git:ssh://github.com/gmitch215/TabroomAPI.git"
            url = "https://github.com/gmitch215/TabroomAPI"
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, true)
    signAllPublications()
}

npmPublish {
    readme = file("README.md")

    packages.forEach {
        it.packageJson {
            name = "@gmitch215/${project.name}"
            version = project.version.toString()
            description = desc
            license = "MIT"
            homepage = "https://github.com/gmitch215/TabroomAPI"

            types = "${project.name}.d.ts"

            author {
                name = "Gregory Mitchell"
                email = "me@gmitch215.xyz"
            }

            repository {
                type = "git"
                url = "git+https://github.com/gmitch215/TabroomAPI.git"
            }

            keywords = listOf("tabroom", "api", "kotlin", "multiplatform")
        }
    }

    registries {
        register("npmjs") {
            uri.set("https://registry.npmjs.org")
            authToken.set(System.getenv("NPM_TOKEN"))
        }

        register("GithubPackages") {
            uri.set("https://npm.pkg.github.com/gmitch215")
            authToken.set(System.getenv("GITHUB_TOKEN"))
        }

        register("CalculusGames") {
            val releases = "https://repo.calcugames.xyz/repository/npm-releases"
            val snapshots = "https://repo.calcugames.xyz/repository/npm-snapshots"

            uri.set(if (project.version.toString().endsWith("SNAPSHOT")) snapshots else releases)
            username.set(System.getenv("NEXUS_USERNAME"))
            password.set(System.getenv("NEXUS_PASSWORD"))
        }
    }
}