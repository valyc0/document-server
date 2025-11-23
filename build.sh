#!/bin/bash

echo "======================================"
echo "📦 Building Document Server Projects"
echo "======================================"
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

BUILD_DIR=$(pwd)

# Build all modules with parent POM
build_all_modules() {
    echo -e "${YELLOW}📦 Building all modules from parent POM (with Vaadin production mode)...${NC}"
    cd "$BUILD_DIR/server"
    
    if mvn clean package -Pproduction -DskipTests; then
        echo -e "${GREEN}✅ All modules built successfully${NC}"
        echo ""
        return 0
    else
        echo -e "${RED}❌ Failed to build modules${NC}"
        echo ""
        return 1
    fi
}

# Function to build a Maven project (kept for compatibility)
build_maven_project() {
    local project_name=$1
    local project_dir=$2
    local extra_args=$3
    
    echo -e "${YELLOW}📦 Building $project_name...${NC}"
    cd "$BUILD_DIR/$project_dir"
    
    if mvn clean package -DskipTests $extra_args; then
        echo -e "${GREEN}✅ $project_name built successfully${NC}"
        echo ""
        return 0
    else
        echo -e "${RED}❌ Failed to build $project_name${NC}"
        echo ""
        return 1
    fi
}

# Build using multi-module Maven project
build_all_modules || exit 1

cd "$BUILD_DIR"

echo "======================================"
echo -e "${GREEN}✅ All projects built successfully!${NC}"
echo "======================================"
echo ""
echo "JARs created:"
echo "  - server/orchestrator-service/target/orchestrator-service-1.0.0.jar"
echo "  - server/extraction-service/target/extraction-service-1.0.0.jar"
echo "  - server/indexing-service/target/indexing-service-1.0.0.jar"
echo "  - server/ui-service/target/ui-service-1.0.0.jar"
echo ""
echo "Ready to start with: docker compose up -d"
