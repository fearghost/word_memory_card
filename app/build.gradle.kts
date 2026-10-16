1|1|plugins {
2|2|    id("com.android.application")
3|3|    id("org.jetbrains.kotlin.android")
4|4|    id("com.google.devtools.ksp")
5|5|    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.24"
6|6|}
7|7|
8|8|android {
9|9|    namespace = "com.example.wordmemory"
10|10|    compileSdk = 34
11|11|
12|12|    defaultConfig {
13|13|        applicationId = "com.example.wordmemory"
14|14|        minSdk = 26
15|15|        targetSdk = 34
16|16|        versionCode = 1
17|17|        versionName = "1.0"
18|18|
        val geminiKey = System.getenv("GEMINI_API_KEY")?.let { "\"$it\"" } ?: "\"YOUR_GEMINI_API_KEY\""
20|20|        buildConfigField("String", "GEMINI_API_KEY", geminiKey)
21|21|    }
22|22|
23|23|    buildTypes {
24|24|        release {
25|25|            isMinifyEnabled = false
26|26|        }
27|27|    }
28|28|
29|29|    buildFeatures {
30|30|        compose = true
31|31|        buildConfig = true
32|32|    }
33|33|
34|34|    composeOptions {
35|35|        kotlinCompilerExtensionVersion = "1.5.14"
36|36|    }
37|37|
38|38|    compileOptions {
39|39|        sourceCompatibility = JavaVersion.VERSION_11
40|40|        targetCompatibility = JavaVersion.VERSION_11
41|41|    }
42|42|
43|43|    kotlinOptions {
44|44|        jvmTarget = "11"
45|45|    }
46|46|}
47|47|
48|48|dependencies {
49|49|    implementation("androidx.core:core-ktx:1.12.0")
50|50|    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
51|51|    implementation("androidx.activity:activity-compose:1.8.0")
52|52|    
53|53|    // Compose BoM
54|54|    val composeBom = platform("androidx.compose:compose-bom:2024.05.00")
55|55|    implementation(composeBom)
56|56|    implementation("androidx.compose.ui:ui")
57|57|    implementation("androidx.compose.ui:ui-graphics")
58|58|    implementation("androidx.compose.material3:material3")
59|59|    implementation("androidx.compose.material:material-icons-extended")
60|60|    implementation("androidx.compose.runtime:runtime")
61|61|    implementation("androidx.compose.runtime:runtime-livedata")
62|62|    
63|63|    implementation("com.google.android.material:material:1.11.0")
64|64|    
65|65|    // Room
66|66|    implementation("androidx.room:room-runtime:2.6.0")
67|67|    implementation("androidx.room:room-ktx:2.6.0")
68|68|    ksp("androidx.room:room-compiler:2.6.0")
69|69|    
70|70|    // Navigation
71|71|    implementation("androidx.navigation:navigation-compose:2.7.5")
72|72|    
73|73|    // ML Kit (Bundled version for Korean support)
74|74|    implementation("com.google.mlkit:text-recognition-korean:16.0.1")
75|75|    implementation("com.google.mlkit:vision-common:16.0.1")
76|76|    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
77|77|    
78|78|    // Testing & LLM
79|79|    testImplementation("junit:junit:4.13.2")
80|80|    testImplementation("androidx.test.ext:junit-ktx:1.1.5")
81|81|    testImplementation("androidx.test:core-ktx:1.5.0")
82|82|    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
83|83|    testImplementation("io.mockk:mockk:1.13.8")
84|84|    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
85|85|    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
86|86|}