plugins {
  id("com.netflix.dgs.codegen") version "5.0.5"
}

dependencies {
  implementation(project(":micronaut-cart"))
  implementation("org.slf4j:slf4j-api:1.7.30")
  implementation("com.google.guava:guava:30.0-jre")
  testImplementation("org.assertj:assertj-core:3.15.0")
  testImplementation("org.mockito:mockito-core:3.2.4")
  testImplementation("org.mockito:mockito-junit-jupiter:3.2.4")
  testImplementation("org.junit.jupiter:junit-jupiter-params:5.6.0")
  testImplementation("ch.qos.logback:logback-classic:1.2.3")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
  testImplementation(project(":test"))
  testImplementation(project(":core"))
  testImplementation("io.micronaut:micronaut-http-server:" + findProperty("micronautVersion"))
  testImplementation("io.micronaut:micronaut-runtime:" + findProperty("micronautVersion}"))
  runtimeOnly("ch.qos.logback:logback-classic")
}

tasks.withType<com.netflix.graphql.dgs.codegen.gradle.GenerateJavaTask> {
  generateClient = true
  packageName = "io.activated.pipeline.micronaut.cart"
  schemaPaths = mutableListOf(projectDir.path + "/src/main/resources/schema")
  typeMapping = mutableMapOf("BigDecimal" to "java.math.BigDecimal")
}

tasks.withType<JavaCompile> {
  dependsOn("generateJava")
}


tasks.withType<Test> {
    useJUnitPlatform()
}

group = "io.activated.reduction"

repositories {
    mavenCentral()
}
