plugins {
    id("io.micronaut.application") version "1.5.3"
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("io.activated.pipeline.micronaut.cart.*")
    }
}

group = "io.activated.reduction"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    implementation(project(":micronaut"))
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("com.google.guava:guava:30.0-jre")
    testImplementation("org.assertj:assertj-core:3.15.0")
    testImplementation("org.mockito:mockito-core:3.2.4")
    testImplementation("org.mockito:mockito-junit-jupiter:3.2.4")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.6.0")
    testImplementation("ch.qos.logback:logback-classic:1.2.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    implementation("io.micronaut.graphql:micronaut-graphql:2.3.1")
    implementation("io.micronaut:micronaut-runtime")
    implementation("javax.annotation:javax.annotation-api")
    runtimeOnly("ch.qos.logback:logback-classic")
    implementation("io.micronaut:micronaut-validation")
    implementation("io.micronaut:micronaut-http-server:" + findProperty("micronautVersion"))
}

application {
    mainClass.set("io.activated.pipeline.micronaut.cart.Application")
}

task("exportSchema", JavaExec::class) {
  group = "Execution"
  description = "Run the schema exporter"
  classpath = sourceSets.getByName("main").runtimeClasspath
  main = "io.activated.pipeline.micronaut.cart.SchemaExporter"
  args(listOf(rootDir.path + "/micronaut-e2e/src/main/resources/schema/cart.schema"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}

