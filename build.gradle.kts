plugins {
    java
    application
    id("org.beryx.jlink") version "3.1.4-rc"
    id("org.openjfx.javafxplugin") version "0.0.13"
}

repositories {
    mavenCentral()
}

application {
    mainClass = "by.forwardsystem.Main"
    version = "1.0.0"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

javafx {
    version = "21.0.6"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.web", "javafx.swing")
}

dependencies {
    implementation("org.controlsfx:controlsfx:11.2.1")
    implementation("org.kordamp.ikonli:ikonli-javafx:12.3.1")
    implementation("org.kordamp.bootstrapfx:bootstrapfx-core:0.4.0")

    implementation("eu.hansolo:tilesfx:21.0.9") {
        exclude(group = "org.openjfx")
    }

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    implementation("org.slf4j:slf4j-api:1.7.28")
    implementation("org.slf4j:slf4j-simple:1.7.28")

    implementation("org.apache.commons:commons-lang3:3.19.0")
    implementation("org.postgresql:postgresql:42.7.8")
    implementation("com.google.code.gson:gson:2.13.2")
}

jlink {
    jpackage {
        installerType = when {
            org.gradle.internal.os.OperatingSystem.current().isWindows -> "msi"
            org.gradle.internal.os.OperatingSystem.current().isLinux -> "deb"
            org.gradle.internal.os.OperatingSystem.current().isMacOsX -> "dmg"
            else -> error("Unsupported OS")
        }

        val additionalOptions = when {
            org.gradle.internal.os.OperatingSystem.current().isWindows -> listOf("--win-shortcut", "--win-menu")
            org.gradle.internal.os.OperatingSystem.current().isLinux -> listOf("--linux-shortcut")
            org.gradle.internal.os.OperatingSystem.current().isMacOsX -> listOf("--mac-sign")
            else -> error("Unsupported OS")
        }

        installerOptions.addAll(additionalOptions)
    }
}