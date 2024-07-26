plugins {
    `library-publishing-conventions`
}

dependencies {
    api("io.lettuce:lettuce-core:6.1.0.RELEASE") {
        exclude(group = "io.netty")
    }
    api("com.fasterxml.jackson.core:jackson-databind:2.12.2")
    api("com.flipkart.zjsonpatch:zjsonpatch:0.4.11")
    api("org.reactivestreams:reactive-streams:1.0.3")
    api("io.activated.objectdiff:objectdiff:0.0.11")
    api("javax.validation:validation-api:2.0.1.Final")
    testImplementation("org.hibernate.validator:hibernate-validator:6.0.23.Final")
    testImplementation("org.glassfish:javax.el:3.0.0")
    testImplementation("io.projectreactor:reactor-core:3.4.10")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testImplementation("io.netty:netty-common:4.1.60.Final")
    testImplementation("io.netty:netty-handler:4.1.60.Final")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

