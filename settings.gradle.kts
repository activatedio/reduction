pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}

rootProject.name = "reduction"

include("core", "micronaut", "test", "micronaut-cart", "micronaut-e2e")
