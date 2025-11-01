plugins {
    application
    id("org.beryx.jlink") version "3.1.4-rc"
}

repositories {
    mavenCentral()
}

application {
    mainClass = "by.forwardsystem.Main"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
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