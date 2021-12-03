import com.matthewprenger.cursegradle.CurseArtifact
import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import com.modrinth.minotaur.TaskModrinthUpload
import groovy.json.JsonGenerator
import groovy.json.JsonSlurper
import net.fabricmc.loom.task.RunGameTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm").version("1.6.0")
    kotlin("plugin.serialization").version("1.6.0")

    id("fabric-loom").version("0.10.+")
    id("com.matthewprenger.cursegradle").version("1.4.0")
    id("com.modrinth.minotaur").version("1.1.0")
    id("maven-publish")
}

val prop = Property()
val env: Map<String, String> = System.getenv()

version = env["MOD_VERSION"] ?: "local"

repositories {
    maven("https://maven.shedaniel.me/")
}

dependencies {
    minecraft("com.mojang:minecraft:${prop["minecraft"]}")
    mappings("net.fabricmc:yarn:${prop["yarn"]}:v2")

    modImplementation("net.fabricmc:fabric-loader:${prop["fabricLoader"]}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${prop["fabricApi"]}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${prop["fabricKotlin"]}")

    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:${prop["rei"]}")
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-default-plugin-fabric:${prop["rei"]}")
    modRuntimeOnly("me.shedaniel:RoughlyEnoughItems-fabric:${prop["rei"]}")
}

sourceSets {
    val main by getting
    create("dev") {
        compileClasspath += main.compileClasspath + main.output
        runtimeClasspath += main.runtimeClasspath
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
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

    doLast {
        val slurper = JsonSlurper()
        val json = JsonGenerator.Options()
            .disableUnicodeEscaping()
            .build()
        fileTree(outputs.files.asPath) {
            include("**/*.json")
            forEach {
                val mini = json.toJson(slurper.parse(it, "UTF-8"))
                it.writeText(mini)
            }
        }
    }
}

curseforge {
    env["CURSEFORGE_API"]?.let { CURSEFORGE_API ->
        apiKey = CURSEFORGE_API
        project(closureOf<CurseProject> {
            id = prop["cf.projectId"]
            releaseType = prop["cf.releaseType"]

            changelogType = "markdown"
            changelog = "https://github.com/badasintended/slotlink/releases/tag/${project.version}"

            mainArtifact(tasks["remapJar"], closureOf<CurseArtifact> {
                displayName = "[${prop["minecraft"]}] v${project.version}"
            })

            addGameVersion("Fabric")
            prop["cf.gameVersion"].split(", ").forEach {
                addGameVersion(it)
            }

            relations(closureOf<CurseRelation> {
                prop["cf.require"].split(", ").forEach {
                    requiredDependency(it)
                }
                prop["cf.optional"].split(", ").forEach {
                    optionalDependency(it)
                }
            })

            afterEvaluate {
                uploadTask.dependsOn("build")
            }
        })
    }
}

task<TaskModrinthUpload>("modrinth") {
    group = "upload"
    onlyIf { env.contains("MODRINTH_TOKEN") }
    dependsOn("build")

    token = env["MODRINTH_TOKEN"]
    projectId = prop["mr.projectId"]
    versionNumber = version.toString()
    uploadFile = tasks["remapJar"]
    releaseType = prop["mr.releaseType"]
    addLoader("fabric")

    prop["mr.gameVersion"].split(", ").forEach {
        addGameVersion(it)
    }
}

class Property {

    operator fun get(name: String) = project.property(name).toString()

}
