# voting
the rest services to vote

To build the application run the command inside the voting folder
```
./gradlew build -x test
```
Start the Java application with the following commands:
```
java -jar build/libs/voting-1.1.2.jar --server.port=8080 --spring.profiles.active=dev
```
and open [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) in your browser to connect to the vote application.

If you need to start it on a environment production:
```
java -Djavax.net.ssl.trustStore=./docker/prod/volume/cert/application.keystore -Djavax.net.ssl.trustStorePassword=password -jar build/libs/voting-1.1.2.jar --server.ssl.key-store=./docker/prod/volume/cert/application.keystore --server.ssl.key-store-password=password --server.ssl.trust-store=./docker/prod/volume/cert/application.keystore --server.ssl.trust-store-password=password --server.port=8443 --spring.profiles.active=prod
```

Add the following DNS in your /etc/hosts file:
```
$IP_ADDRESS  vota-votingpapers.vige.it
```

where in $IP_ADDRESS you must choose the ip address where is located the server
and open [https://vota-voting.vige.it:8443/swagger-ui.html](https://vota-voting.vige.it:8443/swagger-ui.html) in your browser to connect to the vote application.

## certificates

in a production environment we are using a default certificate but you could move a different ssl certificate and keys. Use this command to generate it:
```
keytool -genkey -alias voting -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore ./docker/prod/volume/cert/application.keystore -validity 3650 -dname "CN=vota-voting.vige.it, OU=Vige, O=Vige, L=Rome, S=Italy, C=IT" -storepass password -keypass password
```
You need to export the auth certificate and import it through the command:
```
keytool -import -alias auth -file ${exported_auth_certificate}.pem -keystore ./docker/prod/volume/cert/application.keystore -storepass password -keypass password
```
Same thing for the votingpapers certificate:
```
keytool -import -alias votingpapers -file ${exported_votingpapers_certificate}.pem -keystore ./docker/prod/volume/cert/application.keystore -storepass password -keypass password
```

## Test

To execute the gradle tests only start before the votingpaper server

## Eclipse

To make the project as an Eclipse project go in the root folder of the project and run the following command:
```
./gradlew eclipse
```

## Docker development

If you need a complete environment you can download docker and import the application through the command:
```
docker pull vige/vota-voting
```
To run the image use the command:
```
docker run -d --name vota-voting -p8080:8080 vige/vota-voting
```
Then open [http://vota-voting.vige.it:8080/swagger-ui/index.html](http://vota-voting.vige.it:8080/swagger-ui/index.html) to connect to the vote application

## Docker production

If you need a complete environment you can download docker and import the application through the command:
```
docker pull vige/vota-voting
```
To run the image use the command:
```
docker run -d --name vota-voting -p8443:8443 vige/vota-voting
```
Then open [https://vota-voting.vige.it:8443/swagger-ui/index.html](https://vota-voting.vige.it:8443/swagger-ui/index.html) to connect to the vote application
