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

#### Salary decimal places

The requirement doesn't specify any decimal places of the salary column. Considering real world use case and Singapore dollar in mind, we use 2 decimal places.

#### Sort parameter value

For the sort parameter value, we use the default [Spring Data REST](https://docs.spring.io/spring-data/rest/docs/current/reference/html/) convention (regex:`($propertyname,)+[asc|desc]`), for example:
```
&sort=name,asc
```
which is different from the requirement:
```
&sort=+name
```
To avoid re-inventing the wheel, this is the trade-off we choose. If we had chosen to implement the requirement as-is, we'd write custom code instead of using Spring Data REST, but sacrifices Spring Data REST features.

#### Default hidden id

By default, Spring Data REST hides id field in json response. For example, instead of:
```json
{
  "id": "...",
  "login": "...",
  "name": "...",
  "salary": 1234.56
}
```
We get:
```json
{
  "login": "...",
  "name": "...",
  "salary": 1234.56,
  "_links": {
    "self": {
      "href": "http://localhost:8080/users/e0001"
    },
    "employee": {
      "href": "http://localhost:8080/users/e0001{?projection}",
      "templated": true
    }
  }
}
```
To retrieve users with id, we provide projection parameter, for example:
```
/users/e0001?projection=employeeWithId
```
Spring Data REST provides Resource Discoverability feature by default through the publication of links that point to the available resources, see the `_links` in the example json.

#### Search url

In Spring Data REST, all query method resources are exposed under the `search` resource. For example:
```
/users/search/findBySalaryBetween?minSalary=0&maxSalary=4000&offset=0&limit=30&sort=id,asc
```
instead of:
```
/users?minSalary=0&maxSalary=4000&offset=0&limit=30&sort=name,asc
```

#### Optional pagination and sort parameters

The requirement is to return HTTP 400 if any of request params is missing. In our case, we support optional pagination (`offset`, `limit`) and `sort` parameters.

However, if either `minSalary` or `maxSalary` parameter is missing, we return HTTP 400 as expected.

#### Internationalization (i18n)

We use `Accept-Language` header, for example:
```shell script
curl -i -H "Accept-Language: id" "http://localhost:8080/"
```

### Technology

* Java
* Spring Boot
* H2
* Thymeleaf
* Bootstrap
* Flyway
* Gradle

#### Automated tests

We don't provide any test cases for Spring Data REST features like CRUD, search, pagination, since it is a well-known library which has extensive automated tests.
We do provide automated tests for the custom API codes that we have written.
By using standard technology, our codebase becomes more concise and readable, as well as achieving business requirement. 

#### Database

For proof-of-concept, quick to start application, we use H2 database.
For production application, we would choose PostgreSQL.

#### Database Migration

We use Flyway for database source control.

#### UI

We use Thymeleaf, Bootstrap 4 for responsive UI.
For full-blown UI in the future, we might choose React.
