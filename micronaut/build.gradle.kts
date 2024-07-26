plugins {
  `library-publishing-conventions`
  id("io.micronaut.library") version "2.0.2"
}

val provided by configurations.creating

sourceSets {
    main {
        compileClasspath += provided
        runtimeClasspath += provided
    }
    test {
        compileClasspath += provided
        runtimeClasspath += provided
    }
}

micronaut {
}

dependencies {
    api(project(":core"))
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.2")
    implementation("com.graphql-java:graphql-java:16.2")
    implementation("io.micronaut.graphql:micronaut-graphql:2.3.1")
    provided("io.micronaut:micronaut-http-server:" + findProperty("micronautVersion"))
    implementation("io.projectreactor:reactor-core:3.4.10")
    testImplementation("io.micronaut.test:micronaut-test-junit5")
}
