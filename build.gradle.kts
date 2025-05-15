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
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.session:spring-session-core")

	// added for endpoint that implements bulk load reading uploaded excel file
	implementation("org.apache.poi:poi-ooxml:5.4.0")

	runtimeOnly("org.postgresql:postgresql")

	implementation("org.springframework.boot:spring-boot-starter-validation")

	compileOnly("org.projectlombok:lombok")
	//runtimeOnly("com.mysql:mysql-connector-j") // ya no uso mysql sino postgres pq es la disponible en render
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	//testImplementation("org.springframework.security:spring-security-test")

	// Added line for Spring Boot DevTools
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// for email sending capabilities
	implementation("org.springframework.boot:spring-boot-starter-mail")

	// para escanear las clases, se usa en backend endpoins information
	implementation("org.reflections:reflections:0.10.2")
}


// tambien como ya no usare multicontainer app, posiblemente estos task aca abajo ya no sean necesarios pero
// igual los dejo de referencia.

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
