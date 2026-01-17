#!/bin/bash
# ============================================================================
# Generate GitHub Secrets for BikeRideDetection CI/CD
# ============================================================================

set -e

echo "=============================================="
echo "GitHub Secrets Generator"
echo "=============================================="
echo ""

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

mkdir -p .secrets

echo -e "${YELLOW}Generating secret values...${NC}"
echo ""

# 1. KEYSTORE_BASE64
if [ -f "keystores/bikeride-release.jks" ]; then
    base64 -i keystores/bikeride-release.jks > .secrets/KEYSTORE_BASE64.txt
    echo -e "${GREEN}✓ KEYSTORE_BASE64${NC} → .secrets/KEYSTORE_BASE64.txt"
else
    echo "✗ Keystore not found"
fi

# 2. GOOGLE_SERVICES_JSON
if [ -f "app/google-services.json" ]; then
    base64 -i app/google-services.json > .secrets/GOOGLE_SERVICES_JSON.txt
    echo -e "${GREEN}✓ GOOGLE_SERVICES_JSON${NC} → .secrets/GOOGLE_SERVICES_JSON.txt"
    
    # Extract FIREBASE_APP_ID
    APP_ID=$(grep -o '"mobilesdk_app_id": *"[^"]*"' app/google-services.json | head -1 | cut -d'"' -f4)
    echo "$APP_ID" > .secrets/FIREBASE_APP_ID.txt
    echo -e "${GREEN}✓ FIREBASE_APP_ID${NC} → .secrets/FIREBASE_APP_ID.txt (${APP_ID})"
else
    echo "✗ google-services.json not found - download from Firebase Console"
fi

# 3. Read from keystore.properties if it exists
if [ -f "keystore.properties" ]; then
    STORE_PASSWORD=$(grep "storePassword" keystore.properties | cut -d'=' -f2)
    KEY_ALIAS_VAL=$(grep "keyAlias" keystore.properties | cut -d'=' -f2)
    KEY_PASSWORD_VAL=$(grep "keyPassword" keystore.properties | cut -d'=' -f2)

    echo "$STORE_PASSWORD" > .secrets/KEYSTORE_PASSWORD.txt
    echo -e "${GREEN}✓ KEYSTORE_PASSWORD${NC} → .secrets/KEYSTORE_PASSWORD.txt"

    echo "$KEY_ALIAS_VAL" > .secrets/KEY_ALIAS.txt
    echo -e "${GREEN}✓ KEY_ALIAS${NC} → .secrets/KEY_ALIAS.txt"

    echo "$KEY_PASSWORD_VAL" > .secrets/KEY_PASSWORD.txt
    echo -e "${GREEN}✓ KEY_PASSWORD${NC} → .secrets/KEY_PASSWORD.txt"
else
    echo -e "${YELLOW}! keystore.properties not found - create it first${NC}"
fi

echo ""
echo "=============================================="
echo -e "${BLUE}Instructions:${NC}"
echo "=============================================="
echo ""
echo "1. Go to: https://github.com/AdityaMalu/BikeRideDetection/settings/secrets/actions"
echo ""
echo "2. For each file in .secrets/, create a new repository secret:"
echo "   - Secret name: filename (without .txt)"
echo "   - Secret value: contents of the file"
echo ""
echo "3. For FIREBASE_SERVICE_ACCOUNT_JSON:"
echo "   - Go to Firebase Console → Project Settings → Service accounts"
echo "   - Click 'Generate new private key'"
echo "   - Copy the entire JSON content as the secret value"
echo ""
echo "=============================================="
echo -e "${GREEN}Quick copy commands:${NC}"
echo "=============================================="
echo ""
echo "# Copy each secret to clipboard (run one at a time):"
echo "cat .secrets/KEYSTORE_BASE64.txt | pbcopy"
echo "cat .secrets/GOOGLE_SERVICES_JSON.txt | pbcopy"
echo "cat .secrets/FIREBASE_APP_ID.txt | pbcopy"
echo "cat .secrets/KEYSTORE_PASSWORD.txt | pbcopy"
echo "cat .secrets/KEY_ALIAS.txt | pbcopy"
echo "cat .secrets/KEY_PASSWORD.txt | pbcopy"
echo ""

# Add .secrets to gitignore if not already there
if ! grep -q "^\.secrets/$" .gitignore 2>/dev/null; then
    echo ".secrets/" >> .gitignore
    echo -e "${YELLOW}Added .secrets/ to .gitignore${NC}"
fi

echo "=============================================="
echo -e "${GREEN}Done!${NC}"
echo "=============================================="

