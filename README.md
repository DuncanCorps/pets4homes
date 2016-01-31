# pets4homes
Utilities for interacting with Pets4Homes

## refresh
Refreshes all adverts for the specified account.

### Build
    mvn clean compile assembly:single

### Run
    java -jar target/pets4homes-refresh-1.0.0-jar-with-dependencies.jar <emailAddress> <password>