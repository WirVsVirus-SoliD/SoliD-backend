# solid-backend project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

## Preqrequisites

A keycloak realm with name "solid" must be defined, wherein the user accounts will reside and which is used for authentication

## Configuration

The following properties must be made available via system environment

### Database configuration (database name: solid)
pguser - database user  
pgpasswd - password for database user  
pghost - database host ip  
pgport - database host port  

### Security configuration (keycloak client name: solid)
quarkus.oidc.credentials.secret - keycloak client secret  
keycloak.server.url - FQDN of keycloak server  
keycloak.useradmin.login - login for the useradmin account to create keycloak logins  
keycloak.useradmin.passwd - password for the useradmin account to create keycloak logins  

### Custom Configuration
ticket.timeout.hours - defines the default timeout for account creation tickets, default 24h

## Packaging and running the application

The application can be packaged using `./mvnw package`.
It produces the `solid-backend.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/solid-backend.jar`.

## Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/code-with-quarkus-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image-guide.