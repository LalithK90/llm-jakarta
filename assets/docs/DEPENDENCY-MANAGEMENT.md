# Dependency Management Strategy

This document outlines the dependency management strategy implemented in the llm-jakarta project.

## Overview

The project uses a centralized dependency management approach to ensure consistency across all modules. This approach:

1. Standardizes dependency versions across modules
2. Reduces duplication and potential version conflicts
3. Makes it easier to upgrade dependencies
4. Provides vulnerability scanning capabilities

## Implementation Details

### Parent POM Configuration

The root `pom.xml` file contains:

1. **Properties Section**: Defines versions for all dependencies and plugins used across modules
2. **DependencyManagement Section**: Declares all dependencies with their versions
3. **PluginManagement Section**: Standardizes plugin configurations
4. **Vulnerability Scanning**: Includes the OWASP Dependency Check plugin

### Module POM Configuration

Each module's `pom.xml` file:

1. References the parent POM
2. Declares dependencies without version information (versions are inherited from the parent)
3. References plugins without version or configuration information (inherited from the parent)
4. Includes only module-specific configuration

## Adding New Dependencies

When adding a new dependency:

1. If it's a common dependency that might be used in multiple modules:
   - Add it to the `dependencyManagement` section in the root `pom.xml`
   - Define its version in the `properties` section
   
2. If it's a module-specific dependency:
   - If the dependency is already defined in the parent's `dependencyManagement`, simply add it to the module without a version
   - If it's a completely new dependency, consider adding it to the parent's `dependencyManagement` first

## Vulnerability Scanning

The project includes the OWASP Dependency Check plugin to scan for known vulnerabilities:

1. Run a vulnerability scan with:
   ```
   mvn dependency-check:check -P dependency-check
   ```

2. If false positives or known issues are found, they can be suppressed in the `dependency-check-suppressions.xml` file

## Upgrading Dependencies

To upgrade dependencies:

1. Update the version property in the root `pom.xml`
2. Run tests to ensure compatibility
3. If issues are found, either:
   - Fix the issues
   - Revert to the previous version
   - Create a plan to address the issues in a future update

## Managing Conflicts

If dependency conflicts occur:

1. Use the `mvn dependency:tree` command to identify the conflict
2. Resolve by:
   - Excluding the conflicting transitive dependency
   - Upgrading to a compatible version
   - Using a different dependency that doesn't have the conflict