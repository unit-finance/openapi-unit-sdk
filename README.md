# unit - OpenAPI

Unit uses the `OpenAPI 3.0.2`  specification to structure our documentation and provide users with the ability to generate client libraries in various programming languages.<br>This approach ensures a consistent and uniform typing experience across all external interfaces. Below, we have included some examples and challenges that we have encountered during the iterative process of working with this specification.


run with:
java -jar swagger-codegen-cli-3.0.36.jar generate -i openapi.json -l java -o unit

-i input
-l language
-o output directory
