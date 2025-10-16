#!/bin/bash
set -e

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 Hiroaki Release Script${NC}"
echo ""

# Check if version is provided
if [ -z "$1" ]; then
    echo -e "${YELLOW}Usage: ./release.sh <version>${NC}"
    echo "Example: ./release.sh 1.0.0"
    exit 1
fi

VERSION=$1
TAG="v${VERSION}"

# Validate version format (basic semver check)
if ! [[ $VERSION =~ ^[0-9]+\.[0-9]+\.[0-9]+(-[a-zA-Z0-9.-]+)?$ ]]; then
    echo -e "${RED}❌ Invalid version format!${NC}"
    echo "Version must follow semver format: MAJOR.MINOR.PATCH (e.g., 1.0.0)"
    echo "Optional pre-release suffix allowed (e.g., 1.0.0-beta.1)"
    exit 1
fi

# Verify we're in a git repository
if ! git rev-parse --git-dir > /dev/null 2>&1; then
    echo -e "${RED}❌ Not a git repository!${NC}"
    exit 1
fi

# Check if tag already exists
if git rev-parse "$TAG" >/dev/null 2>&1; then
    echo -e "${RED}❌ Tag ${TAG} already exists!${NC}"
    echo "To delete it: git tag -d ${TAG}"
    exit 1
fi

# Check if remote 'origin' exists
if ! git remote get-url origin > /dev/null 2>&1; then
    echo -e "${YELLOW}⚠️  Remote 'origin' not configured${NC}"
fi

echo -e "${GREEN}📋 Release Checklist${NC}"
echo ""

# 1. Run tests
echo -e "${BLUE}1. Running tests...${NC}"
./gradlew test
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Tests failed! Fix them before releasing.${NC}"
    exit 1
fi
echo -e "${GREEN}✅ All tests passed!${NC}"
echo ""

# 2. Clean and build project
echo -e "${BLUE}2. Building project...${NC}"
./gradlew clean build -x test
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Build failed!${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Build successful!${NC}"
echo ""

# 2.1 Verify library modules can be published (without actually publishing)
echo -e "${BLUE}2.1. Verifying library modules...${NC}"
echo "     - hiroaki-core (will be published)"
echo "     - hiroaki-android (will be published)"
echo "     - app (demo only, will NOT be published)"
if ! ./gradlew :hiroaki-core:assemble :hiroaki-android:assemble -x test > /dev/null 2>&1; then
    echo -e "${RED}❌ Library modules build failed!${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Library modules ready for publication!${NC}"
echo ""

# 3. Check git status
echo -e "${BLUE}3. Checking git status...${NC}"
if [[ -n $(git status -s) ]]; then
    echo -e "${YELLOW}⚠️  You have uncommitted changes. Please commit them first.${NC}"
    git status -s
    exit 1
fi
echo -e "${GREEN}✅ Working directory clean!${NC}"
echo ""

# 4. Create tag
echo -e "${BLUE}4. Creating git tag ${TAG}...${NC}"
git tag -a "${TAG}" -m "Release version ${VERSION}

Modernized Hiroaki fork:
- Gradle 8.10
- Kotlin 2.0.20
- Android SDK 35
- Package: io.github.ymaniz09.hiroaki
- All 64 tests passing
"
echo -e "${GREEN}✅ Tag created!${NC}"
echo ""

# 5. Show next steps
echo -e "${GREEN}✅ Release prepared!${NC}"
echo ""
echo -e "${BLUE}Next steps:${NC}"
echo "1. Push the tag:"
echo -e "   ${YELLOW}git push origin ${TAG}${NC}"
echo ""
echo "2. Create GitHub release:"
echo -e "   ${YELLOW}https://github.com/ymaniz09/hiroaki/releases/new?tag=${TAG}${NC}"
echo ""
echo "3. Trigger JitPack build:"
echo -e "   ${YELLOW}https://jitpack.io/#ymaniz09/hiroaki/${TAG}${NC}"
echo ""
echo -e "${BLUE}To undo this tag (if needed):${NC}"
echo -e "   ${YELLOW}git tag -d ${TAG}${NC}"
echo ""
echo -e "${GREEN}🎉 Ready to release!${NC}"

