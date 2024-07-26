plugins {
  id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
//  id("com.diffplug.spotless") version "7.0.0.BETA1"
}


nexusPublishing {
    repositories {
        sonatype {}
    }
}

//do not generate extra load on Nexus with new staging repository if signing fails
val initializeSonatypeStagingRepository by tasks.existing
subprojects {
    initializeSonatypeStagingRepository {
        shouldRunAfter(tasks.withType<Sign>())
    }
}
