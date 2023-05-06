# unit - OpenAPI

Unit uses the `OpenAPI 3.0.2`  specification to structure our documentation and provide users with the ability to generate client libraries in various programming languages.<br>This approach ensures a consistent and uniform typing experience across all external interfaces. Below, we have included some examples for working with this specification.

## Using the OpenAPI generator
You can find examples on the official [Swagger Codegen docs](https://github.com/swagger-api/swagger-codegen#generating-a-client-from-local-files).

### unit-java
```
java -jar swagger-codegen-cli-3.0.36.jar generate -i openapi.json -l java -o unit-java
```

### unit-python
```
java -jar swagger-codegen-cli-3.0.36.jar generate -i openapi.json -l python -o unit-python
```

