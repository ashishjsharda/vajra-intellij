plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("org.jetbrains.intellij") version "1.17.0"
}

group = "com.vajra"
version = "0.2.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}

intellij {
    version.set("2023.3")
    type.set("IC")
    plugins.set(listOf())
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
    
    patchPluginXml {
        sinceBuild.set("233")
        untilBuild.set("243.*")
        
        changeNotes.set("""
            <h3>0.2.0</h3>
            <ul>
                <li>Initial release of Vajra for IntelliJ IDEA</li>
                <li>Support for GPT-5, Claude 4, Qwen3-Coder, DeepSeek, and 10+ AI providers</li>
                <li>Interactive chat interface with context awareness</li>
                <li>Code actions: Explain, Refactor, Debug, Optimize, Generate Tests</li>
                <li>Local model support via Ollama</li>
                <li>Multi-provider intelligent routing</li>
            </ul>
        """.trimIndent())
    }
    
    buildSearchableOptions {
        enabled = false
    }
}
