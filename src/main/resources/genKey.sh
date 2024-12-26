#!/usr/bin/env bash

mkdir jwt
openssl genrsa -out rsaPrivateKey.pem 2048
openssl rsa -pubout -in rsaPrivateKey.pem -out jwt/publicKey.pem

openssl pkcs8 -topk8 -nocrypt -inform pem -in rsaPrivateKey.pem -outform pem -out jwt/privateKey.pem

rm rsaPrivateKey.pem
