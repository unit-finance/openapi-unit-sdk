
# Unit OpenAPI v3 - for developers

you can find Unit json schemas at https://docs.unit.co/#json-schema,
right now the schemas aren't openapi v3 structure
you can use a converter to save time and still use an existing json file, for example:
https://transform.tools/json-schema-to-openapi-schema

as a linter we are using:
https://redocly.com/docs/cli/commands/lint/
```commandline
redocly lint openapi.json --generate-ignore-file
```