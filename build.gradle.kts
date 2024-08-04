plugins {
    java
    war
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.kjbank.chat"
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

extra["springModulithVersion"] = "1.2.1"

dependencies {
    implementation("org.apache.tomcat.embed:tomcat-embed-jasper")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.session:spring-session-core")
    // JSP support
    implementation("org.apache.tomcat.embed:tomcat-embed-jasper")
	implementation("org.glassfish.web:jakarta.servlet.jsp.jstl:3.0.1")
    implementation("org.apache.taglibs:taglibs-standard-impl:1.2.5")
    implementation("org.apache.taglibs:taglibs-standard-spec:1.2.5")

    // Optional: If using Thymeleaf
    // implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    
    compileOnly("javax.servlet:javax.servlet-api:4.0.1")
    compileOnly("org.projectlombok:lombok")
    
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.modulith:spring-modulith-bom:${property("springModulithVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
