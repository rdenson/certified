#!/bin/bash
KEYSTORE=$1
KEYSTORE_PASSWORD=$2
TARGET_ENTRY_ALIAS=$3
LOG_PREFIX="%%%%%>  "

function printLogEntry {
  echo "$LOG_PREFIX$1"
}

if [ -z $TARGET_ENTRY_ALIAS ]; then
  printLogEntry "what is the alias of the keystore entry that you need?"
  exit 1
fi
ALIAS_CHECK=$(keytool -list -keystore $KEYSTORE.jks -storepass $KEYSTORE_PASSWORD | grep -cE "^$TARGET_ENTRY_ALIAS,")
if [ $ALIAS_CHECK -lt 1 ]; then
  printLogEntry "cannot find $TARGET_ENTRY_ALIAS in $KEYSTORE"
  exit 1
fi

printLogEntry "converting \"$TARGET_ENTRY_ALIAS\" in $KEYSTORE to a PKCS #12 bundle (archive)"
keytool \-importkeystore \-srckeystore $KEYSTORE.jks \-srcstorepass $KEYSTORE_PASSWORD \-srcalias $TARGET_ENTRY_ALIAS \-destkeystore $KEYSTORE.p12 \-deststoretype PKCS12 \-deststorepass changeit \-destkeypass changeit
openssl pkcs12 \-in $KEYSTORE.p12 \-passin pass:changeit \-nokeys \-out $TARGET_ENTRY_ALIAS.crt
openssl pkcs12 \-in $KEYSTORE.p12 \-passin pass:changeit \-nodes \-nocerts \-out $TARGET_ENTRY_ALIAS.key
