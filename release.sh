#!/bin/bash
set -e

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
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

echo -e "${GREEN}📋 Release Checklist${NC}"
echo ""

# 1. Run tests
echo -e "${BLUE}1. Running tests...${NC}"
./gradlew test
if [ $? -ne 0 ]; then
    echo -e "${YELLOW}❌ Tests failed! Fix them before releasing.${NC}"
    exit 1
fi
echo -e "${GREEN}✅ All tests passed!${NC}"
echo ""

# 2. Build project
echo -e "${BLUE}2. Building project...${NC}"
./gradlew build -x test
if [ $? -ne 0 ]; then
    echo -e "${YELLOW}❌ Build failed!${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Build successful!${NC}"
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
echo -e "${BLUE}4. Creating git tag v${VERSION}...${NC}"
git tag -a "${VERSION}" -m "Release version ${VERSION}

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
echo -e "   ${YELLOW}git push origin ${VERSION}${NC}"
echo ""
echo "2. Create GitHub release:"
echo -e "   ${YELLOW}https://github.com/ymaniz09/hiroaki/releases/new${NC}"
echo ""
echo "3. Trigger JitPack build:"
echo -e "   ${YELLOW}https://jitpack.io/#ymaniz09/hiroaki${NC}"
echo ""
echo -e "${BLUE}To undo this tag (if needed):${NC}"
echo -e "   ${YELLOW}git tag -d ${VERSION}${NC}"
echo ""
echo -e "${GREEN}🎉 Ready to release!${NC}"

