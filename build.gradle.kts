import com.matthewprenger.cursegradle.CurseArtifact
import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import net.fabricmc.loom.task.RunGameTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm").version("1.5.10")
    kotlin("plugin.serialization").version("1.5.10")

    id("fabric-loom").version("0.8-SNAPSHOT")
    id("com.matthewprenger.cursegradle").version("1.4.0")
    id("maven-publish")
}

val prop = Property()
val env: Map<String, String> = System.getenv()

version = env["MOD_VERSION"] ?: "local"

sourceSets {
    val main by getting
    create("dev") {
        compileClasspath += main.compileClasspath
        runtimeClasspath += main.runtimeClasspath
    }
}

repositories {
    maven("https://maven.shedaniel.me/")
    maven("https://maven.terraformersmc.com/releases")
}

dependencies {
    val devImplementation by configurations.getting

    minecraft("com.mojang:minecraft:${prop["minecraft"]}")
    mappings("net.fabricmc:yarn:${prop["yarn"]}:v2")

    modImplementation("net.fabricmc:fabric-loader:${prop["fabricLoader"]}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${prop["fabricApi"]}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${prop["fabricKotlin"]}")

    devImplementation(sourceSets["main"].output)
}

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "16"
    }
}

tasks.withType<RunGameTask> {
    classpath = sourceSets["dev"].runtimeClasspath
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

curseforge {
    env["CURSEFORGE_API"]?.let { CURSEFORGE_API ->
        apiKey = CURSEFORGE_API
        project(closureOf<CurseProject> {
            id = "391014"
            releaseType = "release"

            changelogType = "markdown"
            changelog = "https://github.com/badasintended/slotlink/releases/tag/${project.version}"

            mainArtifact(tasks["remapJar"], closureOf<CurseArtifact> {
                displayName = "[${prop["minecraft"]}] v${project.version}"
            })

            addGameVersion("Fabric")
            addGameVersion(prop["minecraft"])

            relations(closureOf<CurseRelation> {
                requiredDependency("fabric-api")
                requiredDependency("fabric-language-kotlin")
                optionalDependency("roughly-enough-items")
            })

            uploadTask.dependsOn("build")
        })
    }
}

class Property {

    operator fun get(name: String) = project.property(name).toString()

}
