plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("org.jetbrains.intellij") version "1.17.0"
}

group = "com.vajra"
version = "0.2.5"

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
        untilBuild.set("253.*")  // FIXED: Restored to 253.*
        
        changeNotes.set("""
            <h3>0.2.5 - Inline Experience & Bug Fixes 🎉</h3>
            <ul>
                <li><strong>NEW:</strong> Inline AI suggestions like Cursor and GitHub Copilot!</li>
                <li><strong>NEW:</strong> Ghost text for code suggestions (press Tab to accept)</li>
                <li><strong>NEW:</strong> Inline diff view with Accept/Reject buttons</li>
                <li><strong>NEW:</strong> Smart explanation popups right next to your code</li>
                <li><strong>FIXED:</strong> Threading issues with read/write actions</li>
                <li><strong>FIXED:</strong> Timeout errors (increased to 60s for cloud, 120s for Ollama)</li>
                <li><strong>FIXED:</strong> Markdown formatting in AI responses</li>
                <li><strong>IMPROVED:</strong> No more popup dialogs breaking your flow!</li>
                <li>All AI responses now appear inline - stay in context while coding</li>
                <li>Added proper plugin icon</li>
            </ul>
            
            <h3>0.2.4</h3>
            <ul>
                <li>Fixed compatibility range (now supports up to 253.*)</li>
                <li>Internal improvements</li>
            </ul>
            
            <h3>0.2.3</h3>
            <ul>
                <li>Internal improvements and bug fixes</li>
            </ul>
            
            <h3>0.2.1</h3>
            <ul>
                <li>Updated to use gpt-4o as default model</li>
                <li>Improved error handling with detailed error messages from API</li>
                <li>Updated Anthropic Claude model names to proper API format</li>
            </ul>
            
            <h3>0.2.0</h3>
            <ul>
                <li>Initial release of Vajra for IntelliJ IDEA</li>
                <li>Support for 10+ AI providers</li>
                <li>Interactive chat interface</li>
                <li>Code actions and local model support</li>
            </ul>
        """.trimIndent())
    }
    
    buildSearchableOptions {
        enabled = false
    }
}