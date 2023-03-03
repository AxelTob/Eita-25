
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
keytool -genkeypair -keyalg RSA -keystore patient1keystore -alias patient1keystore -dname "CN=patient1" -storepass password -keypass password
keytool -genkeypair -keyalg RSA -keystore patient2keystore -alias patient2keystore -dname "CN=patient2" -storepass password -keypass password

keytool -genkeypair -keyalg RSA -keystore doctor1keystore -alias doctor1keystore -dname "CN=doctor1" -storepass password -keypass password
keytool -genkeypair -keyalg RSA -keystore doctor2keystore -alias doctor2keystore -dname "CN=doctor2" -storepass password -keypass password
keytool -genkeypair -keyalg RSA -keystore doctor3keystore -alias doctor3keystore -dname "CN=doctor3" -storepass password -keypass password

keytool -genkeypair -keyalg RSA -keystore nurse1keystore -alias nurse1keystore -dname "CN=nurse1" -storepass password -keypass password
keytool -genkeypair -keyalg RSA -keystore nurse2keystore -alias nurse2keystore -dname "CN=nurse2" -storepass password -keypass password
keytool -genkeypair -keyalg RSA -keystore governmentkeystore -alias governmentkeystore -dname "CN=government" -storepass password -keypass password

#skapa en CSR för clientkeystore
keytool -certreq -keystore patient1keystore -file P1keystore.csr -alias patient1keystore -storepass password
keytool -certreq -keystore patient2keystore -file P2keystore.csr -alias patient2keystore -storepass password

keytool -certreq -keystore doctor1keystore -file D1keystore.csr -alias doctor1keystore -storepass password
keytool -certreq -keystore doctor2keystore -file D2keystore.csr -alias doctor2keystore -storepass password
keytool -certreq -keystore doctor3keystore -file D3keystore.csr -alias doctor3keystore -storepass password

keytool -certreq -keystore nurse1keystore -file N1keystore.csr -alias nurse1keystore -storepass password
keytool -certreq -keystore nurse2keystore -file D2keystore.csr -alias nurse2keystore -storepass password
keytool -certreq -keystore governmentkeystore -file Gkeystore.csr -alias governmentkeystore -storepass password


echo '=============================='
echo 'Signerar client side nyckelpar'
echo '   Vad vänlig ange lösenord   '
echo '=============================='

#signera keystore med CA
#openssl x509 -req -in keystore.csr -CA CA.crt -CAkey CA.key -out keystore.crt -CAcreateserial
openssl x509 -req -in P1keystore.csr -CA CA.crt -CAkey CA.key -out P1keystore.crt -CAcreateserial
openssl x509 -req -in P2keystore.csr -CA CA.crt -CAkey CA.key -out P2keystore.crt -CAcreateserial
openssl x509 -req -in D1keystore.csr -CA CA.crt -CAkey CA.key -out D1keystore.crt -CAcreateserial
openssl x509 -req -in D2keystore.csr -CA CA.crt -CAkey CA.key -out D2keystore.crt -CAcreateserial
openssl x509 -req -in D3keystore.csr -CA CA.crt -CAkey CA.key -out D3keystore.crt -CAcreateserial
openssl x509 -req -in N1keystore.csr -CA CA.crt -CAkey CA.key -out N1keystore.crt -CAcreateserial
openssl x509 -req -in N2keystore.csr -CA CA.crt -CAkey CA.key -out N2keystore.crt -CAcreateserial
openssl x509 -req -in Gkeystore.csr -CA CA.crt -CAkey CA.key -out Gkeystore.crt -CAcreateserial

#importera CA
#keytool -importcert -file CA.crt -alias rootca -keystore clientkeystore -storepass password

keytool -importcert -file CA.crt -alias rootca -keystore patient1keystore -storepass password
keytool -importcert -file CA.crt -alias rootca -keystore patient2keystore -storepass password
keytool -importcert -file CA.crt -alias rootca -keystore doctor1keystore -storepass password
keytool -importcert -file CA.crt -alias rootca -keystore doctor2keystore -storepass password
keytool -importcert -file CA.crt -alias rootca -keystore doctor3keystore -storepass password
keytool -importcert -file CA.crt -alias rootca -keystore nurse1keystore -storepass password
keytool -importcert -file CA.crt -alias rootca -keystore nurse2keystore -storepass password
keytool -importcert -file CA.crt -alias rootca -keystore governmentkeystore -storepass password

#importera sitt eget cert
#keytool -importcert -file keystore.crt -alias keystore -keystore clientkeystore -storepass password

keytool -importcert -file P1keystore.crt -alias patient1keystore -keystore patient1keystore -storepass password
keytool -importcert -file P2keystore.crt -alias patient2keystore -keystore patient2keystore -storepass password
keytool -importcert -file D1keystore.crt -alias doctor1keystore -keystore doctor1keystore -storepass password
keytool -importcert -file D2keystore.crt -alias doctor2keystore -keystore doctor2keystore -storepass password
keytool -importcert -file D3keystore.crt -alias doctor3keystore -keystore doctor3keystore -storepass password
keytool -importcert -file N1keystore.crt -alias nurse1keystore -keystore nurse1keystore -storepass password
keytool -importcert -file N2keystore.crt -alias nurse2keystore -keystore nurse2keystore -storepass password
keytool -importcert -file Gkeystore.crt -alias governmentkeystore -keystore governmentkeystore -storepass password


#check
#keytool -list -v -keystore clientkeystore -storepass password 
#keytool -list -v -keystore doctor1keystore -storepass password 
#keytool -list -v -keystore nurse1keystore -storepass password 

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

rm *.crt
rm *.csr

