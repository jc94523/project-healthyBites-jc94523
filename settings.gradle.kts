pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }

}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        //mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url =uri("https://maven.aliyun.com/repository/google") }
        maven { url =uri("https://maven.aliyun.com/repository/central") }
        maven { url =uri("https://maven.aliyun.com/repository/public") }
    }
}

rootProject.name = "HealthyBites"
include(":app")
 