plugins {
	java
	id("org.springframework.boot") version "3.2.5"
	id("io.spring.dependency-management") version "1.1.4"
}
// fast1dev
group = "lac-manufacture-plant"
version = "v1"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	//implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.session:spring-session-core")

	implementation("org.springframework.boot:spring-boot-starter-validation")

	compileOnly("org.projectlombok:lombok")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	//testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.register<JavaExec>("buildWithoutTest") {
	group = "build"
	description = "Builds the project excluding tests"

	mainClass.set("org.gradle.wrapper.GradleWrapperMain")
	classpath = files("gradle/wrapper/gradle-wrapper.jar")
	args = listOf("build", "-x", "test")
}

tasks.register<Copy>("generateTargetJar") {
	dependsOn("buildWithoutTest")

	doFirst{
		delete(file("target/app.jar"))
	}

	from("build/libs") {
		include("*.jar")
		rename { "app.jar" }
	}
	into("target")
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
