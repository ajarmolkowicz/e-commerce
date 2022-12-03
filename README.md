# Prerequisites
* [Java JDK 17](https://openjdk.org/projects/jdk/17/)
* [Maven 3.6.3](https://maven.apache.org/download.cgi)

# How to build the project?

```console
mvn package
```

This will result in the code being compiled and tests from `src/test` being run.
The result will be an executable file in the `target` directory, named `e-commerce-trunk.jar`

# How to run the project?

```console
java -jar e-commerce-trunk.jar
```

This will result in project being run on port `8080`.
The Swagger UI is accessible at: `http://localhost:8080/swagger-ui/index.html`.

# How to run integration tests?
```console
mvn integration-test
```

This will result in running the integration tests from folder `src/it`.
These tests check persistence mechanism and a series of http requests.

