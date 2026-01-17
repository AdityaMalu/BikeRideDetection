#!/bin/bash
# ============================================================================
# Firebase Setup Helper Script for BikeRideDetection
# ============================================================================

set -e

echo "=============================================="
echo "BikeRideDetection Firebase Setup Helper"
echo "=============================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Check if google-services.json exists
if [ -f "app/google-services.json" ]; then
    echo -e "${GREEN}✓ google-services.json found${NC}"
    
    # Validate it's not the template
    if grep -q "YOUR_PROJECT_ID" app/google-services.json 2>/dev/null; then
        echo -e "${RED}✗ google-services.json appears to be the template. Please download from Firebase Console.${NC}"
        exit 1
    fi
    
    # Extract project info
    PROJECT_ID=$(grep -o '"project_id": *"[^"]*"' app/google-services.json | head -1 | cut -d'"' -f4)
    echo -e "${BLUE}  Project ID: ${PROJECT_ID}${NC}"
else
    echo -e "${RED}✗ google-services.json not found${NC}"
    echo ""
    echo "Please follow these steps:"
    echo "1. Go to https://console.firebase.google.com"
    echo "2. Create or select your project"
    echo "3. Add Android app with package: com.example.bikeridedetection"
    echo "4. Add Android app with package: com.example.bikeridedetection.debug"
    echo "5. Download google-services.json and place in app/ directory"
    exit 1
fi

echo ""
echo "=============================================="
echo "Keystore Information"
echo "=============================================="

# Check release keystore
if [ -f "keystores/bikeride-release.jks" ]; then
    echo -e "${GREEN}✓ Release keystore found${NC}"
    
    # Get SHA fingerprints
    echo ""
    echo -e "${BLUE}Release Keystore Fingerprints:${NC}"
    keytool -list -v -keystore keystores/bikeride-release.jks -alias bikeride-release -storepass BikeRide2024! 2>/dev/null | grep -E "(SHA1|SHA256)" | head -2
else
    echo -e "${YELLOW}! Release keystore not found. Run: ./scripts/create-keystore.sh${NC}"
fi

# Check debug keystore
if [ -f ~/.android/debug.keystore ]; then
    echo ""
    echo -e "${BLUE}Debug Keystore Fingerprints:${NC}"
    keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android 2>/dev/null | grep -E "(SHA1|SHA256)" | head -2
fi

echo ""
echo "=============================================="
echo "GitHub Secrets Required"
echo "=============================================="
echo ""
echo "Add these secrets to your GitHub repository:"
echo "(Settings → Secrets and variables → Actions → New repository secret)"
echo ""

echo -e "${YELLOW}1. GOOGLE_SERVICES_JSON${NC}"
echo "   Value: Base64 encoded google-services.json"
if [ -f "app/google-services.json" ]; then
    echo "   Command: base64 -i app/google-services.json | pbcopy"
fi

echo ""
echo -e "${YELLOW}2. KEYSTORE_BASE64${NC}"
echo "   Value: Base64 encoded release keystore"
if [ -f "keystores/keystore-base64.txt" ]; then
    echo "   File ready: keystores/keystore-base64.txt"
fi

echo ""
echo -e "${YELLOW}3. KEYSTORE_PASSWORD${NC}"
echo "   Value: BikeRide2024!"

echo ""
echo -e "${YELLOW}4. KEY_ALIAS${NC}"
echo "   Value: bikeride-release"

echo ""
echo -e "${YELLOW}5. KEY_PASSWORD${NC}"
echo "   Value: BikeRide2024!"

echo ""
echo -e "${YELLOW}6. FIREBASE_APP_ID${NC}"
echo "   Value: Your Firebase App ID (from Firebase Console → Project Settings → Your apps)"
if [ -f "app/google-services.json" ]; then
    APP_ID=$(grep -o '"mobilesdk_app_id": *"[^"]*"' app/google-services.json | head -1 | cut -d'"' -f4)
    echo -e "   ${GREEN}Found: ${APP_ID}${NC}"
fi

echo ""
echo -e "${YELLOW}7. FIREBASE_SERVICE_ACCOUNT_JSON${NC}"
echo "   Value: Firebase service account JSON (for App Distribution)"
echo "   Get from: Firebase Console → Project Settings → Service accounts → Generate new private key"

echo ""
echo "=============================================="
echo -e "${GREEN}Setup Complete!${NC}"
echo "=============================================="

