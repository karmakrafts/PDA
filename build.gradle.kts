
import java.nio.file.StandardOpenOption
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.inputStream

/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

plugins {
    `eclipse`
    `idea`
    `maven-publish`
    `java`
    alias(libs.plugins.forgeGradle)
    //alias(libs.plugins.spongeGradle)
    //alias(libs.plugins.librarian)
    //alias(libs.plugins.shadow)
    //alias(libs.plugins.kotlin)
    //alias(libs.plugins.dokka)
}

java.toolchain.languageVersion = JavaLanguageVersion.of(17)

val buildConfig = Properties().apply {
    Path("build.properties").inputStream(StandardOpenOption.READ).use {
        load(it)
    }
}

repositories {
    mavenCentral()
    maven(url = "https://thedarkcolour.github.io/KotlinForForge/")
    maven(url = "https://maven.covers1624.net")
    maven(url = "https://www.cursemaven.com/")
}

dependencies {
    minecraft(libs.minecraftForge)
}

minecraft {
    mappings(channel = buildConfig["mappings_channel"], version = "${libs.versions.mappings.get()}-${libs.versions.minecraft.get()}")
}