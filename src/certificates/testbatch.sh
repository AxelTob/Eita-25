
#borde lägga till så truststore skapas
#skapa CA
openssl genrsa -des3 -out CA.key 2048

#signera CA (will appear in the file)
openssl req -new -x509 -days 7305 -key CA.key -out CA.crt 

#skapa truststore för klient
keytool -import -file CA.crt -alias firstCA -keystore clienttruststore 

echo '============================'
echo 'Skapar client side nyckelpar'
echo '============================'

#skapa ett nyckelpar
keytool -genkeypair -keyalg RSA -keystore doctor1keystore -alias doctor1keystore -dname "CN=doctor1" -storepass password -keypass password
keytool -genkeypair -keyalg RSA -keystore nurse1keystore -alias nurse1keystore -dname "CN=nurse1" -storepass password -keypass password
#keytool -genkeypair -keyalg RSA -keystore patient1keystore -alias keystore -dname "CN=PATIENT1" -storepass password -keypass password
#keytool -genkeypair -keyalg RSA -keystore patient2keystore -alias keystore -dname "CN=PATIENT2" -storepass password -keypass password

#skapa en CSR för clientkeystore
keytool -certreq -keystore doctor1keystore -file D1keystore.csr -alias doctor1keystore -storepass password
keytool -certreq -keystore nurse1keystore -file N1keystore.csr -alias nurse1keystore -storepass password


echo '=============================='
echo 'Signerar client side nyckelpar'
echo '   Vad vänlig ange lösenord   '
echo '=============================='

#signera keystore med CA
#openssl x509 -req -in keystore.csr -CA CA.crt -CAkey CA.key -out keystore.crt -CAcreateserial
openssl x509 -req -in D1keystore.csr -CA CA.crt -CAkey CA.key -out D1keystore.crt -CAcreateserial
openssl x509 -req -in N1keystore.csr -CA CA.crt -CAkey CA.key -out N1keystore.crt -CAcreateserial

#importera CA 
#keytool -importcert -file CA.crt -alias rootca -keystore clientkeystore -storepass password
keytool -importcert -file CA.crt -alias rootca -keystore doctor1keystore -storepass password
keytool -importcert -file CA.crt -alias rootca -keystore nurse1keystore -storepass password

#importera sitt eget cert
#keytool -importcert -file keystore.crt -alias keystore -keystore clientkeystore -storepass password
keytool -importcert -file D1keystore.crt -alias doctor1keystore -keystore doc1keystore -storepass password
keytool -importcert -file N1keystore.crt -alias nurse1keystore -keystore nurse1keystore -storepass password

#check
#keytool -list -v -keystore clientkeystore -storepass password 
keytool -list -v -keystore doctor1keystore -storepass password 
keytool -list -v -keystore nurse1keystore -storepass password 

echo '============================'
echo 'Skapar server side nyckelpar'
echo '============================'

#skapa ett nyckelpar
keytool -genkeypair -keyalg RSA -keystore serverkeystore -alias serverkeystore -dname "CN=MyServer" -storepass password -keypass password

#skapa en CSR för keystore
keytool -certreq -keystore serverkeystore -file serverkeystore.csr -alias serverkeystore -storepass password

#skapa truststore för klient
keytool -import -file CA.crt -alias CA -keystore servertruststore 

echo '=============================='
echo 'Signerar server side nyckelpar'
echo '                              '
echo '=============================='

#signera keystore med CA
openssl x509 -req -in serverkeystore.csr -CA CA.crt -CAkey CA.key  -out serverkeystore.crt -CAcreateserial

#importera CA
keytool -importcert -file CA.crt -alias rootca -keystore serverkeystore -storepass password

#importera sitt eget cert
keytool -importcert -file serverkeystore.crt -alias serverkeystore -keystore serverkeystore -storepass password

#check
keytool -list -v -keystore serverkeystore -storepass password 

#rm *.crt
#rm *.csr

