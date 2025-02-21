plugins {
    java
    id("com.gradleup.shadow") version ("9.0.0-beta6")
}

group = "me.xginko"
version = "1.6.0"
description = "Craft invisible item frames in-game."

repositories {
    mavenCentral()

    maven("https://ci.pluginwiki.us/plugin/repository/everything/") {
        name = "configmaster-repo"
    }

    maven("https://repo.codemc.io/repository/maven-releases/") {
        name = "codemc-repo"
    }

    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }

    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }

    maven("https://mvn-repo.arim.space/lesser-gpl3/") {
        name = "arim-mvn-lgpl3"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.github.retrooper:packetevents-spigot:2.7.0")
    compileOnly("org.apache.logging.log4j:log4j-core:2.24.3")

    implementation("org.bstats:bstats-bukkit:3.0.2")

    implementation("com.github.thatsmusic99:ConfigurationMaster-API:v2.0.0-rc.3")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("org.reflections:reflections:0.10.2")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    build.configure {
        dependsOn("shadowJar")
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                mapOf(
                    "name" to project.name,
                    "version" to project.version,
                    "description" to project.description!!.replace('"'.toString(), "\\\""),
                    "url" to "https://github.com/xGinko"
                )
            )
        }
    }

    shadowJar {
        archiveFileName.set("CraftInvisFrames-${version}.jar")

        relocate("com.github.benmanes.caffeine", "me.xginko.craftinvisframes.libs.caffeine")
        relocate("io.github.thatsmusic99.configurationmaster", "me.xginko.craftinvisframes.libs.configmaster")
        relocate("org.reflections", "me.xginko.craftinvisframes.libs.reflections")
        relocate("org.bstats", "me.xginko.craftinvisframes.libs.bstats")
    }
}