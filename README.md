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
java -Djavax.net.ssl.trustStore=./application.keystore -Djavax.net.ssl.trustStorePassword=password -jar build/libs/voting-1.0.0-SNAPSHOT.jar --server.ssl.key-store=./application.keystore --server.ssl.key-store-password=password --server.ssl.trust-store=./application.keystore --server.ssl.trust-store-password=password --server.port=8443 --spring.profiles.active=prod
```

Add the following DNS in your /etc/hosts file:
```
$IP_ADDRESS  vota-votingpapers.vige.it
```

where in $IP_ADDRESS you must choose the ip address where is located the server
and open https://vota-voting.vige.it:8443/swagger-ui.html in your browser to connect to the vote application.

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
