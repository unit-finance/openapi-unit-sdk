
# Unit - OpenAPI

In an effort to standardize and allow for easier access to the Unit API, we offer a `OpenAPI 3.0.2` specification of our API. You may use this along with the approprite generator to auto-generate client libraries in your the coding language of your choice. Note that there is no gurantee that the generated SDK will be fully compatible with our API and may need some alterations or modifications to become operational.

Below, we have included some examples of applications of this specification in some of the more common languages, along with some tweaks we found useful. 

We encourage our SDK community to share feedback of experience with our specification to help identify and address any potential issues. Any feedback will play a crucial role in improving and refining our OpenAPI schema.

## Generating Unit Client Libraries
Below you can find examples for generating client libraries using [OpenAPI generator](https://openapi-generator.tech/). 
You may also use any other known generator, such as [Swagger Codegen](https://github.com/swagger-api/swagger-codegen#generating-a-client-from-local-files), download the jar from [here](https://mvnrepository.com/artifact/io.swagger.codegen.v3/swagger-codegen-cli)

The following examples are with the use of [openapi-generator-cli](https://github.com/OpenAPITools/openapi-generator-cli). 


### unit-java
```commandline
openapi-generator-cli generate -g java -i openapi.json -o unit
```

### unit-python
We recommend using our script which can be found in this repository:

```commandline
python "./generate.py"
```
Or generate by yourself with swagger-codegen-cli:
```
java -jar swagger-codegen-cli-3.0.47.jar generate -i openapi.json -l python -o unit
```

### unit-typescript-axios
```commandline
openapi-generator-cli generate -g typescript-axios -i openapi.json -o unit -p npmName=TypescriptUnitClient
```

### unit-ruby
```commandline
openapi-generator-cli generate -g ruby -i openapi.json -o unit
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

    String access_token = "access_token";
    ApiClient cl = new ApiClient();
    cl.setBearerToken(access_token);
    Configuration.setDefaultApiClient(cl);

    GetListRecurringPaymentsApi api = new GetListRecurringPaymentsApi();

    UnitRecurringPaymentListResponse response = api.execute();


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

## Known Generators Issues
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

### CLI version
Use the follow command to check the openapi-generator-cli version:

```openapi-generator-cli version``` 

We recommend using v7.0.1, you can change the version with the command:

```openapi-generator-cli version-manager set 7.0.1```
