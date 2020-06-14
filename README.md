# employee-management

## Build and run

```shell script
./gradlew bootRun
```
or
```shell script
./gradlew clean build
java -jar build/libs/employee-management-0.0.1-SNAPSHOT.jar
```

## Design

### Functional

The requirement doesn't specify any decimal places of the salary column. Considering real world use case and Singapore dollar in mind, we use 2 decimal places.

### Technology

* Java
* Spring Boot
* H2
* Thymeleaf
* Bootstrap
* Flyway
* Gradle

### Internationalization (i18n)

Spring Boot has built-in i18n feature.

### Database

For proof-of-concept, quick to start application, we use H2 database.
For production application, we would choose PostgreSQL.

### Database Migration

We use Flyway for database source control.

### UI

We use Thymeleaf for this project. Bootstrap for responsive web.
For full-blown UI, we might choose React.
