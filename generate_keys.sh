#!/bin/sh
mkdir -p ./server/src/main/resources/certs
cd ./server/src/main/resources/certs || exit
openssl genrsa -out keypair.pem 4096
openssl rsa -in keypair.pem -pubout -out public.pem
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out private.pem
rm keypair.pem
cd - || exit