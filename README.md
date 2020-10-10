# voting
the rest services to vote

To build the application run the command inside the voting folder
```
./gradlew build -x test
```
Start the Java application with the following commands:
```
java -jar build/libs/voting-1.0.0-SNAPSHOT.jar --server.port=8080 --spring.profiles.active=dev
```
and open http://localhost:8080/swagger-ui/index.html in your browser to connect to the vote application.

If you need to start it on a environment production:
```
java -jar build/libs/voting-1.0.0-SNAPSHOT.jar --server.port=8443 --server.ssl.key-store=/${your_path}/keystore.p12 --server.ssl.key-store-password=secret --server.ssl.keyStoreType=PKCS12 --server.ssl.keyAlias=tomcat --spring.profiles.active=prod
```
Before to start the HTTPS you need to create a keystore. You can use the following sample:
```
keytool -genkey -alias tomcat -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore /${your_path}/keystore.p12 -validity 3650 -dname "CN=vota-voting.vige.it, OU=Vige, O=Vige, L=Rome, S=Italy, C=IT" -storepass secret -keypass secret
```
moving the ${your_path} variable to your preferred path where put the keystore.

Add the following DNS in your /etc/hosts file:
```
$IP_ADDRESS  vota-votingpapers.vige.it
```

where in $IP_ADDRESS you must choose the ip address where is located the server

## Test

To execute the gradle tests only start before the votingpaper server

## Eclipse

To make the project as an Eclipse project go in the root folder of the project and run the following command:
```
./gradlew eclipse
```

## Docker

If you need a complete environment you can download docker and import the application through the command:
```
docker pull vige/vota-voting
```
To run the image use the command:
```
docker run -d --name vota-voting -p8443:8443 vige/vota-voting
```
Then open https://vota-voting.vige.it:8443/swagger-ui/index.html to connect to the vote application
