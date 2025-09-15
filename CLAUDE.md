# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 3.5.5 chat application boilerplate using Java 17 and Gradle. The project follows standard Spring Boot structure with minimal dependencies - just the core Spring Boot starter and test dependencies.

## Development Commands

### Build and Run
- `./gradlew build` - Build the project (compile, test, package)
- `./gradlew bootRun` - Run the Spring Boot application
- `./gradlew clean` - Clean build artifacts

### Testing
- `./gradlew test` - Run all tests
- `./gradlew bootTestRun` - Run application with test runtime classpath

### Common Development Tasks
- `./gradlew classes` - Compile main classes only
- `./gradlew testClasses` - Compile test classes only
- `./gradlew bootJar` - Create executable JAR
- `./gradlew bootBuildImage` - Build OCI container image

## Project Structure

```
src/
├── main/java/com/eomyoosang/chat/
│   └── ChatApplication.java          # Main Spring Boot application class
├── main/resources/
│   └── application.properties        # Application configuration (minimal)
└── test/java/com/eomyoosang/chat/
    └── ChatApplicationTests.java     # Basic context loading test
```

## Configuration

- Application name: "chat" (configured in application.properties)
- Java version: 17 (toolchain configured in build.gradle)
- Package structure: `com.eomyoosang.chat`
- Test framework: JUnit Platform with Spring Boot Test

## Architecture Notes

This is a minimal Spring Boot application currently containing only:
- Main application class with `@SpringBootApplication`
- Basic context loading test
- Standard Gradle wrapper setup
- Standard Spring Boot project structure

The application is ready for expansion with additional Spring Boot features like web controllers, data access, security, etc.

## Development Guidelines

### Library Documentation Verification

**MANDATORY**: Before writing any code that uses libraries or frameworks, ALWAYS use the context7 MCP to verify:
- Current library version compatibility
- Correct API usage and syntax
- Up-to-date documentation and examples
- Best practices for the specific version being used

This ensures code compatibility and follows current library conventions, preventing version-related issues and deprecated API usage.