# unit - OpenAPI

Unit uses the `OpenAPI 3.0.2`  specification to structure our documentation and provide users with the ability to generate client libraries in various programming languages.

This approach ensures a consistent and uniform typing experience across all external interfaces. Below, we have included some examples for working with this specification.

## Generating Unit Client Libraries
Below you can find examples for generating client libraries using Swagger Generator
(for official documentation see [Swagger Codegen docs](https://github.com/swagger-api/swagger-codegen#generating-a-client-from-local-files)).
You may also use any other known generator, such as [OpenAPI generator](https://openapi-generator.tech/).

### unit-java
```
java -jar swagger-codegen-cli-3.0.36.jar generate -i openapi.json -l java -o unit
```

### unit-python
```
java -jar swagger-codegen-cli-3.0.36.jar generate -i openapi.json -l python -o unit
```

### unit-typescript-axios

an [OpenAPI generator](https://openapi-generator.tech/) execution:

```commandline
npm install @openapitools/openapi-generator-cli
openapi-generator-cli generate -g typescript-axios -i openapi.json -o unit -p npmName=TypescriptUnitClient
```

## Generated Clients Usage Examples

#### Python
``` 
    from datetime import date, timedelta
    from swagger_client import configuration, api_client, Address, CreateIndividualApplicationAttributes, FullName, Phone,
            CreateIndividualApplication, CreateBusinessApplication, CreateBusinessApplicationAttributes, BeneficialOwner, \
    Contact, Officer
    
    _configuration = configuration.Configuration()
    _configuration.api_key['Authorization'] = "access_token"
    _configuration.api_key_prefix['Authorization'] = 'Bearer'

    api_client = api_client.ApiClient(configuration=_configuration)
    
    address = Address(street="1600 Pennsylvania Avenue Northwest", city="Washington", state="CA",
                      postal_code="20500",
                      country="US")
    attr = CreateIndividualApplicationAttributes(FullName("Peter", "Parker"), "jone.doe1@unit-finance.com",
                                                 Phone("1", "2025550108"), ssn,
                                                 address=address, date_of_birth="2001-08-10", dba="Piedpiper Inc",
                                                 ein="123456789", sole_proprietorship=False,
                                                 idempotency_key=str(uuid.uuid1()),
                                                 jwt_subject="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9fQ")

    application_request = {"data": CreateIndividualApplication(attributes=attr)}
    
    application = CreateApplicationApi(api_client).execute(application_request).data
    customer_id = application.relationships.customer.data.id
        
    attributes = CreateDepositAccountAttributes("checking", {"purpose": "sdk-test"})
    relationships = CreateDepositAccountRelationships(customer={"data": {"type": "customer", "id": customer_id}})
    req = CreateDepositAccount("depositAccount", attributes, relationships)

    account = CreateAnAccountApi(api_client).execute({"data": req})    
```
you can find more examples in the unit/e2e_tests directory

#### Java

    ApplicationsBody body = new ApplicationsBody();
    CreateIndividualApplication d = new CreateIndividualApplication();
    CreateIndividualApplicationAttributes attr = new CreateIndividualApplicationAttributes();
    
    FullName fn = new FullName();
    fn.setFirst("Peter");
    fn.setLast("Parker");
    attr.setFullName(fn);
    
    Address address = new Address();
    address.setStreet("20 Ingram St");
    address.setCity("Forest Hills");
    address.setPostalCode("11375");
    address.setCountry("US");
    address.setState("NY");
    attr.setAddress(address);

    attr.setSsn("721074426");
    attr.setDateOfBirth(LocalDate.parse("2001-08-10"));
    attr.setEin("123456789");
    attr.setEmail("peter@oscorp.com");
    Phone p = new Phone();
    p.setNumber("5555555555");
    p.setCountryCode("1");
    attr.setPhone(p);
    attr.setDba("Piedpiper Inc");
    attr.setIdempotencyKey("3a1a33be-4e12-4603-9ed0-820922389fb8");
    
    d.setAttributes(attr);
    body.setData(d);

    ApiClient cl = new ApiClient();
    cl.setAccessToken("access_token");
    Configuration.setDefaultApiClient(cl);
    
    CreateApplicationApi apiClient = new CreateApplicationApi();
    UnitCreateApplicationResponse res = apiClient.execute(body);


#### Typescript

    import { GetAccountApi } from "./api";
    import { Configuration } from "./configuration";
    
    const token = "your_token"
    const con: Configuration = new Configuration({accessToken: token})
    const api: GetAccountApi = new GetAccountApi(con)
    async function exec() {
        return (await api.execute("12345")).data
    }

    exec().then((res) => {
        console.log(res)
    })

## FAQ
### Authorization Tokens
The default authorization configuration is located in components/securitySchemes/bearerAuth section in the openapi.json file and looks as follows:

    "bearerAuth": {
        "type": "http",
        "scheme": "bearer",
        "bearerFormat": "JWT"
    }

When generating a python-sdk, you should change the configurations to:

    "bearerAuth": {
        "type": "apiKey",
        "in": "header",
        "name": "Authorization"
    }