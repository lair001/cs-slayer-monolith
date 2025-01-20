plugins {
    id("java")
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.liquibase.gradle") version "2.2.2"
}

group = "com.cs_slayer"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val intTestSourceSet = sourceSets.create("intTest") {
    java.srcDirs("src/intTest/java")

    compileClasspath += sourceSets.main.get().output
    compileClasspath += configurations.testCompileClasspath.get()
    compileClasspath += configurations["intTestRuntimeClasspath"]

    runtimeClasspath += sourceSets.main.get().output
    runtimeClasspath += configurations.testRuntimeClasspath.get()
    runtimeClasspath += configurations["intTestRuntimeClasspath"]
}

val intTestImplementation = configurations.get(intTestSourceSet.implementationConfigurationName)

val unitTestSourceSet = sourceSets.create("unitTest") {
    java.srcDirs("src/unitTest/java")

    compileClasspath += sourceSets.main.get().output
    compileClasspath += configurations.testCompileClasspath.get()
    compileClasspath += configurations["unitTestCompileClasspath"]

    runtimeClasspath += sourceSets.main.get().output
    runtimeClasspath += configurations.testRuntimeClasspath.get()
    runtimeClasspath += configurations["unitTestRuntimeClasspath"]
}

val unitTestImplementation = configurations.get(unitTestSourceSet.implementationConfigurationName)

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.4.1")
    implementation("org.springframework.boot:spring-boot-starter-security:3.4.1")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.4.1")
    implementation("org.springframework.boot:spring-boot-starter-web:3.4.1")
    implementation("org.apache.logging.log4j:log4j-core:2.24.3")
//    compileOnly("org.projectlombok:lombok:1.18.36")
//    annotationProcessor("org.projectlombok:lombok:1.18.36")
    runtimeOnly("org.postgresql:postgresql:42.7.5")
    unitTestImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    unitTestImplementation("org.mockito:mockito-junit-jupiter:5.15.2")
    intTestImplementation("org.springframework.boot:spring-boot-starter-test:3.4.1")
    intTestImplementation("org.springframework.security:spring-security-test:6.4.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
    liquibaseRuntime("org.liquibase:liquibase-core:4.31.0")
    liquibaseRuntime("org.postgresql:postgresql:42.7.5")
    liquibaseRuntime("info.picocli:picocli:4.7.6")
}

val unitTest = task<Test>("unitTest") {
    group = "Verification"
    description = "Runs the unit tests."
    testClassesDirs = sourceSets["unitTest"].output.classesDirs
    classpath = sourceSets["unitTest"].runtimeClasspath
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.test {
    dependsOn(unitTest)
}

val baseline = task("baseline") {
    group = "Liquibase"
    description = "Baselines the database by running dropAll followed by update."
    dependsOn(tasks.dropAll)
    dependsOn(tasks.update)
}

fun connectToMainDb() {
    liquibase {
        activities {
            register("main") {
                this.arguments = mapOf(
                    "changelogFile" to "src/main/resources/rdbms/changelog.yaml",
                    "url" to "jdbc:" + System.getenv("CSSM_RDBMS_PROTOCOL") + "://localhost:" + System.getenv("CSSM_RDBMS_PORT") + "/cssm",
                    "username" to System.getenv("CSSM_RDBMS_USER"),
                    "password" to System.getenv("CSSM_RDBMS_PWD")
                )
            }
        }
    }
}

// We need to create tasks that connect to the db or the int test db for every liquibase task we want to run.
task("updateMainDb") {
    group = "Liquibase Main Database"
    description = "Runs update on the main database."
    doFirst {
        connectToMainDb()
    }
    finalizedBy(tasks.update)
}

// dropAll should not be allowed in production
val checkWhetherDropAllIsAllowed = task("checkWhetherDropAllIsAllowed") {
    doFirst {
        if (System.getenv("ALLOW_DROP_ALL") != "true") {
            throw GradleException("dropAll is not allowed in this environment.")
        }
    }
}

tasks.dropAll {
    dependsOn(checkWhetherDropAllIsAllowed)
}

task("dropAllMainDb") {
    group = "Liquibase Main Database"
    description = "Runs dropAll on the main database."
    doFirst {
        connectToMainDb()
    }
    finalizedBy(tasks.dropAll)
}

task("baselineMainDb") {
    group = "Liquibase Main Database"
    description = "Runs baseline on the main database."
    doFirst {
        connectToMainDb()
    }
    finalizedBy(baseline)
}

fun connectToIntTestDb() {
    liquibase {
        activities {
            register("main") {
                this.arguments = mapOf(
                    "changelogFile" to "src/main/resources/rdbms/changelog.yaml",
                    "url" to "jdbc:" + System.getenv("CSSM_RDBMS_PROTOCOL") + "://localhost:" + System.getenv("CSSM_RDBMS_PORT") + "/cssm-int-test",
                    "username" to System.getenv("CSSM_INT_TEST_RDBMS_USER"),
                    "password" to System.getenv("CSSM_INT_TEST_RDBMS_PWD")
                )
            }
        }
    }
}

val updateIntTestDb = task("updateIntTestDb") {
    group = "Liquibase Integration Test Database"
    description = "Runs update on the integration test database."
    doFirst {
        connectToIntTestDb()
    }
    finalizedBy(tasks.update)
}

val dropAllIntTestDb = task("dropAllIntTestDb") {
    group = "Liquibase Integration Test Database"
    description = "Runs dropAll on the integration test database."
    doFirst {
        connectToIntTestDb()
    }
    finalizedBy(tasks.dropAll)
}

val baselineIntTestDb = task("baselineIntTestDb") {
    group = "Liquibase Integration Test Database"
    description = "Runs baseline on the integration test database."
    doFirst {
        connectToIntTestDb()
    }
    finalizedBy(baseline)
}

val intTest = task<Test>("intTest") {
    group = "Verification"
    description = "Runs the integration tests."
    dependsOn(baselineIntTestDb)
    testClassesDirs = sourceSets["intTest"].output.classesDirs
    classpath = sourceSets["intTest"].runtimeClasspath
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.test {
    dependsOn(intTest)
}
