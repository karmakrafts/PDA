
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.DokkaTask
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.inputStream
import kotlin.io.path.pathString

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

plugins {
    eclipse
    idea
    `maven-publish`
    alias(libs.plugins.forgeGradle)
    alias(libs.plugins.spongeGradle)
    alias(libs.plugins.librarian)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.shadow)
    alias(libs.plugins.dokka)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

val projectPath: Path = project.projectDir.toPath()
val buildConfig: Properties = Properties().apply {
    Path("build.properties").inputStream(StandardOpenOption.READ).use(::load)
}
val modId: String = buildConfig["mod_id"] as String
val mcVersion: String = libs.versions.minecraft.get()
val buildNumber: Int = System.getenv("CI_PIPELINE_IID")?.toIntOrNull() ?: 0
val buildTime: Instant = Instant.now()

version = "${libs.versions.pda.get()}.$buildNumber"
group = buildConfig["group"] as String
base.archivesName = "$modId-$mcVersion"

// Source sets
sourceSets.main {
    java.srcDirs(projectPath / "src" / "main" / "java", projectPath / "src" / "main" / "kotlin")
    kotlin.srcDirs(java.srcDirs)
    resources.srcDirs(projectPath / "src" / "generated" / "resources", projectPath / "src" / "main" / "resources")
}
val mainSourceSet by sourceSets.main

val apiSourceSet = sourceSets.create("api") {
    java.srcDirs(projectPath / "src" / "api", projectPath / "src" / "api")
    kotlin.srcDirs(java.srcDirs)
    resources.srcDirs(projectPath / "src" / "api" / "resources")
}

val foundationSourceSet = sourceSets.create("foundation") {
    java.srcDirs(projectPath / "src" / "foundation", projectPath / "src" / "foundation")
    kotlin.srcDirs(java.srcDirs)
    resources.srcDirs(projectPath / "src" / "foundation" / "resources")
}

val composableSourceSet = sourceSets.create("composable") {
    java.srcDirs(projectPath / "src" / "composable", projectPath / "src" / "composable")
    kotlin.srcDirs(java.srcDirs)
    resources.srcDirs(projectPath / "src" / "composable" / "resources")
}

// Configs
val coreLibraryConfig = configurations.create("coreLibrary")
val libraryConfig = configurations.create("library") {
    extendsFrom(coreLibraryConfig)
}

val minecraftConfig = configurations.getByName("minecraft")

configurations {
    annotationProcessor { extendsFrom(minecraftConfig) }
    val implementation by getting { extendsFrom(coreLibraryConfig, libraryConfig) }
    val compileClasspath by getting { extendsFrom(coreLibraryConfig, libraryConfig) }
    val apiCompileOnly by getting { extendsFrom(coreLibraryConfig, minecraftConfig) }
}

val foundationCompileOnlyConfig = configurations.getByName("foundationCompileOnly") {
    extendsFrom(coreLibraryConfig, minecraftConfig)
}
val composableCompileOnlyConfig = configurations.getByName("composableCompileOnly") {
    extendsFrom(coreLibraryConfig, minecraftConfig)
}

repositories {
    mavenCentral()
    maven("https://thedarkcolour.github.io/KotlinForForge")
    maven("https://maven.covers1624.net")
    maven("https://cursemaven.com")
}

dependencies {
    minecraft(libs.minecraftForge)
    implementation(libs.kotlinForForge)

    coreLibraryConfig(libs.annotations)
    coreLibraryConfig(libs.jacksonCore)
    coreLibraryConfig(libs.jacksonAnnotationns)
    coreLibraryConfig(libs.jacksonDatabind)
    coreLibraryConfig(libs.materialColorUtils)
    coreLibraryConfig(libs.lz4j)

    libraryConfig(libs.lwjglYoga)
    libraryConfig(files(projectPath / "libs" / "lwjgl-freetype-3.3.4-SNAPSHOT.jar"))
    compileOnly(files(projectPath / "libs" / "lwjgl-freetype-3.3.4-SNAPSHOT-javadoc.jar"))
    compileOnly(files(projectPath / "libs" / "lwjgl-freetype-3.3.4-SNAPSHOT-sources.jar"))
    listOf(
        "natives-linux-arm32",
        "natives-linux-arm64",
        "natives-linux",
        "natives-macos-arm64",
        "natives-macos",
        "natives-windows-arm64",
        "natives-windows-x86",
        "natives-windows"
    ).forEach { classifier ->
        libs.lwjglYoga.get().apply {
            libraryConfig(module.group, module.name, version, classifier = classifier) {
                isTransitive = false
            }
        }
        libs.lwjglFreeType.get().apply {
            libraryConfig(module.group, module.name, version, classifier = classifier) {
                isTransitive = false
            }
        }
    }

    libraryConfig(files(projectPath / "libs" / "lwjgl-msdfgen-3.3.4-SNAPSHOT.jar"))
    compileOnly(files(projectPath / "libs" / "lwjgl-msdfgen-3.3.4-SNAPSHOT-javadoc.jar"))
    compileOnly(files(projectPath / "libs" / "lwjgl-msdfgen-3.3.4-SNAPSHOT-sources.jar"))
    listOf(
        "natives-linux",
        "natives-macos",
        "natives-macos-arm64",
        "natives-windows"
    ).forEach { classifier ->
        libraryConfig(files(projectPath / "libs" / "lwjgl-msdfgen-3.3.4-SNAPSHOT-$classifier.jar"))
    }

    compileOnly(apiSourceSet.output)

    foundationCompileOnlyConfig(apiSourceSet.output)
    compileOnly(foundationSourceSet.output)

    composableCompileOnlyConfig(apiSourceSet.output)
    composableCompileOnlyConfig(foundationSourceSet.output)
    composableCompileOnlyConfig(libs.kotlinForForge)
    compileOnly(composableSourceSet.output)
}

minecraft {
    mappings(
        buildConfig["mappings_channel"].toString(),
        "${libs.versions.mappings.get()}-$mcVersion"
    )
    accessTransformer(projectPath / "src" / "main" / "resources" / "META-INF" / "accesstransformer.cfg")
    copyIdeResources = true
    runs {
        val client by creating {
            property("forge.enabledGameTestNamespaces", modId)
        }
        val server by creating {
            property("forge.enabledGameTestNamespaces", modId)
            args("--nogui")
        }
        val gameTestServer by creating {
            property("forge.enabledGameTestNamespaces", modId)
        }
        val data by creating
        val clientAlt by creating {
            parent(client)
            args("--username", "Dev2")
        }
        configureEach {
            workingDirectory(project.file("run"))
            properties(
                mapOf(
                    "forge.logging.markers" to "SCAN,LOADING,CORE",
                    "forge.logging.console.level" to "debug",
                    "mixin.debug" to "true",
                    "mixin.debug.dumpTargetOnFailure" to "true",
                    "mixin.debug.verbose" to "true",
                    "mixin.env.remapRefFile" to "true",
                    "mixin.env.refMapRemappingFile" to (projectPath / "build" / "createSrgToMcp" / "output.srg").pathString
                )
            )
            mods {
                create(modId) {
                    sources(
                        mainSourceSet,
                        apiSourceSet,
                        foundationSourceSet,
                        composableSourceSet
                    )
                }
            }
            lazyToken("minecraft_classpath") {
                libraryConfig.copyRecursive().resolve().joinToString(File.pathSeparator) { it.absolutePath }
            }
        }
    }
}

mixin {
    add(mainSourceSet, "mixins.$modId.refmap.json")
    config("mixins.$modId.client.json")
    config("mixins.$modId.common.json")
}

fun Manifest.applyCommonManifest() {
    attributes.apply {
        this["MixinConfigs"] = "mixins.$modId.client.json,mixins.$modId.common.json"
        this["Specification-Title"] = modId
        this["Specification-Vendor"] = "Karma Krafts"
        this["Specification-Version"] = version
        this["Implementation-Title"] = modId
        this["Implementation-Vendor"] = "Karma Krafts"
        this["Implementation-Version"] = version
        this["Implementation-Timestamp"] = SimpleDateFormat.getDateTimeInstance().format(Date.from(buildTime))
    }
}

fun Provider<MinimalExternalModuleDependency>.toShadowInclude(): String {
    return get().let { "${group}:${name}" }
}

tasks {
    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        val forgeVersion = libs.versions.forge.get()
        val properties = mapOf(
            "minecraft_version" to mcVersion,
            "minecraft_version_range" to "[$mcVersion]",
            "forge_version" to forgeVersion,
            "forge_version_range" to "[$forgeVersion,)",
            "loader_version_range" to forgeVersion.substringBefore("."),
            "mod_id" to modId,
            "mod_name" to buildConfig["mod_name"] as String,
            "mod_license" to "Apache 2.0",
            "mod_version" to version,
            "mod_authors" to "Karma Krafts",
            "mod_description" to "Working pocket computers for Minecraft."
        )
        inputs.properties(properties)
        filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta")) {
            expand(properties)
        }
    }
    dokkaHtml {
        dokkaSourceSets {
            configureEach {
                reportUndocumented = false
                jdkVersion = java.toolchain.languageVersion.get().asInt()
                noAndroidSdkLink = true
                externalDocumentationLink("https://docs.karmakrafts.dev/pda")
                perPackageOption {
                    matchingRegex = "(.*)$group\\.(.*)"
                    suppress = true
                }
            }
        }
    }
}

val dokkaHtmlTask = tasks.getByName<DokkaTask>("dokkaHtml")
val classesTask = tasks.getByName("classes")

val jarTask = tasks.getByName<Jar>("jar") {
    archiveClassifier = "slim"
    manifest.applyCommonManifest()
    finalizedBy("reobfJar") // Lazy forward dependency
    from(
        mainSourceSet.output,
        apiSourceSet.output,
        foundationSourceSet.output,
        composableSourceSet.output
    )
}

val shadowJarTask = tasks.getByName<ShadowJar>("shadowJar") {
    from(jarTask.source)
    archiveClassifier = ""
    manifest.applyCommonManifest()
    finalizedBy("reobfShadowJar") // Lazy forward dependency
    dependencies {
        include(dependency(libs.annotations.toShadowInclude()))
        include(dependency(libs.jacksonCore.toShadowInclude()))
        include(dependency(libs.jacksonAnnotationns.toShadowInclude()))
        include(dependency(libs.jacksonDatabind.toShadowInclude()))
        include(dependency(libs.materialColorUtils.toShadowInclude()))
        include(dependency(libs.lz4j.toShadowInclude()))
        include(dependency(libs.lwjglYoga.toShadowInclude()))
        include(dependency(libs.lwjglFreeType.toShadowInclude()))
    }
}
val reobfShadowJarTask = reobf.create("shadowJar")

val sourcesJarTask = tasks.create<Jar>("sourcesJar") {
    dependsOn(classesTask)
    archiveClassifier = "sources"
    from(
        mainSourceSet.allSource,
        apiSourceSet.allSource,
        foundationSourceSet.allSource,
        composableSourceSet.allSource
    )
}

val apiJarTask = tasks.create<ShadowJar>("apiJar") {
    from(apiSourceSet.output)
    archiveClassifier = "api"
    configurations.addAll(shadowJarTask.configurations)
    finalizedBy("reobfApiJar") // Lazy forward dependency
}
val reobfApiJarTask = reobf.create("apiJar")

val apiSourcesJarTask = tasks.create<Jar>("apiSourcesJar") {
    from(apiSourceSet.allSource)
    archiveClassifier = "api-sources"
}

val apiJavadocJarTask = tasks.create<Jar>("apiJavadocJar") {
    dependsOn(dokkaHtmlTask)
    mustRunAfter(dokkaHtmlTask)
    from(dokkaHtmlTask.outputs)
    archiveClassifier = "api-javadoc"
}

artifacts {
    archives(jarTask)
    archives(shadowJarTask)
    archives(sourcesJarTask)
    archives(apiJarTask)
    archives(apiSourcesJarTask)
    archives(apiJavadocJarTask)
}