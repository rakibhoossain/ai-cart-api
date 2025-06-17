#!/bin/bash
# Generate a new private key
openssl genrsa -out src/main/resources/privateKey.pem 2048

# Generate a public key from the private key
openssl rsa -in src/main/resources/privateKey.pem -pubout -out src/main/resources/publicKey.pem

echo "New JWT keys generated successfully"