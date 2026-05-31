plugins {
    id("java")
    `maven-publish`
}

version = "${property("mod.version")}+${stonecutter.current.version}"
base.archivesName = property("mod.id") as String
val isMinecraft26 = stonecutter.current.version == "26.1"

apply(plugin = if (isMinecraft26) "net.fabricmc.fabric-loom" else "fabric-loom")

repositories {
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }

    strictMaven("https://maven.isxander.dev/releases", "Xander Maven", "dev.isxander")
    strictMaven("https://maven.terraformersmc.com/", "Terraformers", "com.terraformersmc")
    strictMaven("https://maven.quiltmc.org/repository/release", "Quilt", "org.quiltmc.parsers")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
    strictMaven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1", "DevAuth", "me.djtheredstoner")
}

dependencies {
    add("minecraft", "com.mojang:minecraft:${stonecutter.current.version}")

    if (isMinecraft26) {
        add("implementation", "net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
        add("implementation", "net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
        add("implementation", create("maven.modrinth:yacl:${property("yacl_version")}") {
            exclude(group = "net.fabricmc.fabric-api")
        })
        add("implementation", "com.terraformersmc:modmenu:${property("modmenu_version")}")
        add("runtimeOnly", "me.djtheredstoner:DevAuth-fabric:${property("devauth_version")}")
    } else {
        add("mappings", "net.fabricmc:yarn:${property("deps.yarn")}:v2")
        add("modImplementation", "net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
        add("modImplementation", "net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
        add("modImplementation", create("dev.isxander:yet-another-config-lib:${property("yacl_version")}") {
            exclude(group = "net.fabricmc.fabric-api")
        })
        add("modImplementation", "com.terraformersmc:modmenu:${property("modmenu_version")}")
        add("modRuntimeOnly", "me.djtheredstoner:DevAuth-fabric:${property("devauth_version")}")
    }

    add("testImplementation", "net.fabricmc:fabric-loader-junit:${property("deps.fabric_loader")}")
}

sourceSets {
    main {
        if (isMinecraft26) {
            java.setSrcDirs(listOf(rootProject.file("src/26.1/java")))
            resources.setSrcDirs(listOf(rootProject.file("src/26.1/resources")))
        }
    }
    test {
        if (isMinecraft26) {
            java.setSrcDirs(listOf(rootProject.file("src/26.1/test/java")))
        }
    }
}

extensions.configure<net.fabricmc.loom.api.LoomGradleExtensionAPI>("loom") {
    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1")
    }

    runConfigs.named("client") {
        ideConfigGenerated(true)
        runDir = "../../run"
        vmArg("-Ddevauth.enabled=${providers.gradleProperty("devauth.enabled").get()}")
    }
}

java {
    withSourcesJar()
    sourceCompatibility = if (isMinecraft26) JavaVersion.VERSION_25 else JavaVersion.VERSION_21
    targetCompatibility = if (isMinecraft26) JavaVersion.VERSION_25 else JavaVersion.VERSION_21
}

tasks {
    processResources {
        val props = mapOf(
            "id" to project.property("mod.id"),
            "name" to project.property("mod.name"),
            "version" to project.property("mod.version"),
            "description" to project.property("mod.description"),
            "minecraft" to project.property("mod.mc_dep"),
            "yacl" to project.property("deps.yacl")
        )

        inputs.properties(props)
        filesMatching("fabric.mod.json") { expand(props) }
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        if (isMinecraft26) {
            from(jar.map { it.archiveFile }, named<Jar>("sourcesJar").map { it.archiveFile })
        } else {
            from(named("remapJar"), named("remapSourcesJar"))
        }
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }

    test {
        useJUnitPlatform()
    }
}
