

#skapa truststore för klient
#keytool -import -file CA.crt -alias firstCA -keystore clienttruststore 

echo '============================'
echo 'Skapar client side nyckelpar'
echo '============================'

KEYSTORE_FILE="keystore"
TRUSTSTORE_FILE="truststore"
KEYSTORE_PASS=
NAME=
PASSWD=
TRUST_PASSWD=

#stty -echo
read -p "Password for keystore, thanks: " -r PASSWD
#stty echo
echo
read -p "Password for truststore, thanks: " -r TRUST_PASSWD
echo
read -p "Name: " -r NAME
echo

CA_CRT = "../certificates/CA.crt";
#skapa truststore för klient
keytool -import -file CA_CERT -alias $NAME -keystore $NAME/$TRUSTSTORE_FILE 

#skapa ett nyckelpar
keytool -genkeypair -keyalg RSA -keystore $NAME/$KEYSTORE_FILE -alias $NAME -dname "CN=DOC1" -storepass $PASSWD -keypass $PASSWD

#skapa en CSR för keystore
keytool -certreq -keystore $NAME/$KEYSTORE_FILE -file "$NAME/$NAME.csr" -alias $NAME -storepass $PASSWD

#CREATE USERS SEPARATELY BY CHANGING KEYSTORES NAMES MANUALLY 

echo '=============================='
echo 'Signerar client side nyckelpar'
echo '   Vad vänlig ange lösenord   '
echo '=============================='

#CA_CRT = "src/certificates/CA.crt";
#CA_KEY = "src/certificates/CA.key";
#signera keystore med CA
openssl x509 -req -in "$NAME/$NAME.csr" -CA src/certificates/CA.crt -CAkey src/certificates/CA.key -out "$NAME/$NAME.crt" -CAcreateserial

#importera CA
keytool -importcert -file src/certificates/CA.crt -alias rootca -keystore $NAME/$KEYSTORE_FILE -storepass $PASSWD

#importera sitt eget cert
keytool -importcert -file keystore.crt -alias keystore -keystore $NAME/$KEYSTORE_FILE -storepass $PASSWD

#check
keytool -list -v -keystore $NAME/$KEYSTORE_FILE -storepass password 

echo '============================'
echo 'Skapar server side nyckelpar'
echo '============================'

#skapa ett nyckelpar
#keytool -genkeypair -keyalg RSA -keystore serverkeystore -alias serverkeystore -dname "CN=MyServer" -storepass password -keypass password

#skapa en CSR för keystore
#keytool -certreq -keystore serverkeystore -file serverkeystore.csr -alias serverkeystore -storepass password

#skapa truststore för klient
#keytool -import -file CA.crt -alias CA -keystore servertruststore 

echo '=============================='
echo 'Signerar server side nyckelpar'
echo '   Vad vänlig ange lösenord   '
echo '=============================='

#signera keystore med CA
#openssl x509 -req -in serverkeystore.csr -CA CA.crt -CAkey CA.key  -out serverkeystore.crt -CAcreateserial

#importera CA
#keytool -importcert -file CA.crt -alias rootca -keystore serverkeystore -storepass password

#importera sitt eget cert
#keytool -importcert -file serverkeystore.crt -alias serverkeystore -keystore serverkeystore -storepass password

#check
#keytool -list -v -keystore serverkeystore -storepass password 

#rm *.crt
#rm *.csr

