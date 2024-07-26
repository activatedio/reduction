import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
    //id("net.researchgate.release")
    //id("com.diffplug.spotless")
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

/*
spotless {
  java {
    target = "src/**/*.java"
    googleJavaFormat()
  }
} 
*/

dependencies {
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("com.google.guava:guava:30.0-jre")
    testImplementation("org.assertj:assertj-core:3.15.0")
    testImplementation("org.mockito:mockito-core:3.2.4")
    testImplementation("org.mockito:mockito-junit-jupiter:3.2.4")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.6.0")
    testImplementation("ch.qos.logback:logback-classic:1.2.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

/*
release {
    afterReleaseBuild.dependsOn("publish")
}
*/

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "pipeline-" + project.name
            pom {
                name.set("Activated Reduction")
                description.set("Activated Reduction Library")
                url.set("https://github.com/activatedio/reduction")
                inceptionYear.set("2021")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("btomasini")
                        name.set("Ben Tomasini")
                    }
                }
                scm {
                    connection.set("scm:https://github.com/activatedio/reduction.git")
                    developerConnection.set("scm:git@github.com:activatedio/reduction.git")
                    url.set("https://github.com/activatedio/reduction")
                }
            }
        }
    }
}

signing {
    setRequired { !project.version.toString().endsWith("-SNAPSHOT") && !project.hasProperty("skipSigning") }
    if (project.hasProperty("signingKey")) {
        useInMemoryPgpKeys(properties["signingKey"].toString(), properties["signingPassword"].toString())
    } else {
        useGpgCmd()
    }
    sign(publishing.publications["mavenJava"])
}

tasks.javadoc {
    if(JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).apply {
            addBooleanOption("html5", true)
        }
    }
}

