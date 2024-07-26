plugins {
    `library-publishing-conventions`
}

dependencies {
  api("com.netflix.graphql.dgs:graphql-dgs-client:4.4.0")
  api("org.springframework:spring-web:5.3.9")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
  testImplementation("org.assertj:assertj-core:3.15.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
