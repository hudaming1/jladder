#!/bin/bash

# create dir certs db private crl newcerts under rootca dir.
if [ ! -d rootca/certs ]; then
    mkdir -p rootca/certs
fi

if [ ! -d rootca/db ]; then
    mkdir -p rootca/db
    touch rootca/db/index
    openssl rand -hex 16 > rootca/db/serial
    echo 1001 > rootca/db/crlnumber
fi

if [ ! -d rootca/private ]; then
    mkdir -p rootca/private
    chmod 700 rootca/private
fi

if [ ! -d rootca/csr ]; then
    mkdir -p rootca/csr
fi

if [ ! -d rootca/newcerts ]; then
    mkdir -p rootca/newcerts
fi
