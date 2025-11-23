# Migration to Multi-Module Maven Structure

## 📋 Changes Summary

The project has been reorganized into a multi-module Maven structure for better dependency management and build consistency.

## 🏗️ New Structure

```
document-server/
├── server/                          # NEW: Multi-module parent
│   ├── pom.xml                     # Parent POM (packaging: pom)
│   ├── orchestrator-service/       # Module 1
│   │   ├── pom.xml                # Child POM
│   │   ├── src/
│   │   └── target/
│   ├── extraction-service/         # Module 2
│   ├── indexing-service/           # Module 3
│   └── ui-service/                 # Module 4
├── docker-compose.yml              # UPDATED: paths to server/*/target/*.jar
├── build.sh                        # UPDATED: builds from server/
├── start.sh                        # UPDATED: checks server/*/target/*.jar
└── README.md                       # UPDATED: documentation
```

## ✅ Modified Files

### 1. **server/pom.xml** (NEW - Parent POM)
- Packaging: `pom`
- Modules: orchestrator, extraction, indexing, ui-service
- Centralized dependency management:
  - Vaadin 24.4.4
  - MinIO 8.5.7
  - Tika 2.9.1
  - Camel 4.4.0

### 2. **server/*/pom.xml** (Module POMs)
- Parent: `com.example:document-server:1.0.0`
- Removed `<version>` and `<groupId>` (inherited)
- Removed version numbers from managed dependencies
- `<relativePath>../pom.xml</relativePath>`

### 3. **docker-compose.yml**
- Updated volume paths:
  - `./orchestrator-service/target/` → `./server/orchestrator-service/target/`
  - `./extraction-service/target/` → `./server/extraction-service/target/`
  - `./indexing-service/target/` → `./server/indexing-service/target/`
  - `./ui-service/target/` → `./server/ui-service/target/`

### 4. **build.sh**
- Now builds all modules with single command: `cd server && mvn clean package`
- Removed individual module builds
- Faster and consistent builds

### 5. **start.sh**
- Updated JAR existence checks to `server/*/target/*.jar`

### 6. **README.md**
- Updated project structure documentation
- Updated build commands

## 🚀 Build & Run

### Build All Modules
```bash
# From root directory
./build.sh

# Or manually
cd server
mvn clean package -DskipTests
```

### Build Single Module
```bash
cd server/orchestrator-service
mvn clean package
```

### Start Services
```bash
./start.sh
# or
docker compose up -d
```

## ✅ Verification

### Build Status
✅ All modules compile successfully
✅ Spring Boot repackaging works correctly
✅ JARs are executable with `java -jar`
✅ BOOT-INF structure is present in all JARs
✅ Main-Class: org.springframework.boot.loader.launch.JarLauncher

### JAR Locations
✅ server/orchestrator-service/target/orchestrator-service-1.0.0.jar (79 MB)
✅ server/extraction-service/target/extraction-service-1.0.0.jar (82 MB)
✅ server/indexing-service/target/indexing-service-1.0.0.jar (61 MB)
✅ server/ui-service/target/ui-service-1.0.0.jar (67 MB)

### Docker Compose
✅ Volume mounts point to correct paths
✅ All services reference server/*/target/*.jar

## 🎯 Benefits

1. **Single Build Command**: `mvn clean package` builds all modules
2. **Consistent Versions**: Dependencies managed centrally in parent POM
3. **Faster Builds**: Maven reactor optimizes build order
4. **Better IDE Support**: IntelliJ/Eclipse recognize multi-module structure
5. **Dependency Inheritance**: Child modules inherit from parent automatically
6. **Version Control**: Single source of truth for dependency versions

## 📝 Developer Notes

- Always build from `server/` directory or use `./build.sh`
- Docker Compose automatically uses JARs from `server/*/target/`
- Individual module builds still work: `cd server/module && mvn clean package`
- Parent POM defines versions, child POMs just declare dependencies
