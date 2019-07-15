# backend
the rest services to vote

To build the application run the command inside the backend folder
```
./gradlew build
```
Start the Java application with the following commands:
```
./gradlew startMongoDb
```
to start a MongoDB instance. Then:
```
java -jar build/libs/vota-0.0.1-SNAPSHOT.jar --server.port=443
```
and open `https://localhost/swagger-ui.html` in your browser to connect to the vote application.

If you need to start it on a environment production:
```
java -jar build/libs/vota-0.0.1-SNAPSHOT.jar --server.port=443 --server.ssl.key-store=/${your_path}/keystore.p12 --server.ssl.key-store-password=secret --server.ssl.keyStoreType=PKCS12 --server.ssl.keyAlias=tomcat
```
Before to start the HTTPS you need to create a keystore. You can use the following sample:
```
keytool -genkey -alias tomcat -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore /${your_path}/keystore.p12 -validity 3650 -dname "CN=school.vige.it, OU=Vige, O=Vige, L=Rome, S=Italy, C=IT" -storepass secret -keypass secret
```
moving the ${your_path} variable to your preferred path where put the keystore

## Docker

If you need a complete environment you can download docker and import the application through the command:
```
docker pull vige/vota
```
To run the image use the command:
```
docker run -d --name vota -p443:8443 vige/vota
```
Then open `https://localhost` to connect to the vote application
