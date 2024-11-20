# Unit - OpenAPI

In an effort to standardize and allow for easier access to the Unit API, we offer a `OpenAPI 3.0.2` specification of our API. You may use this along with the approprite generator to auto-generate client libraries in your the coding language of your choice. Note that there is no gurantee that the generated SDK will be fully compatible with our API and may need some alterations or modifications to become operational.

Below, we have included some examples of applications of this specification in some of the more common languages, along with some tweaks we found useful.

We encourage our SDK community to share feedback of experience with our specification to help identify and address any potential issues. Any feedback will play a crucial role in improving and refining our OpenAPI schema.

The project is wrapped with npm, we strongly advice to install Node LTS to access preconfigured commands, install packages and the overall ease of usage

## Generating Unit Client Libraries

Below you can find examples for generating client libraries using [OpenAPI generator](https://openapi-generator.tech/).

The following examples are with the use of [openapi-generator-cli](https://github.com/OpenAPITools/openapi-generator-cli).

## npm commands

### bundle

```commandline
Bundles the open api specification into one file inside the dist folder
```

### generate-java

```commandline
Runs the bundle command and generates java sdk inside the dist folder.
```

### generate-node

```commandline
Runs the bundle command and generates node sdk inside the dist folder.
```

### generate-ruby

```commandline
Runs the bundle command and generates ruby sdk inside the dist folder.
```

### generate-python

```commandline
Runs the bundle command and generates python sdk inside the dist folder.
```

### lint

```commandline
Runs redocly lint for schemas to detect any potential issues
```

### format

```commandline
Applies prettier formatting to the schemas
```

## Generated Clients Usage Examples

#### Java

    String access_token = "access_token";
    ApiClient cl = new ApiClient();
    ObjectMapper mapper = cl.getObjectMapper();
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false); // To allow certain requests with empty bodies
    cl.setObjectMapper(mapper);
    cl.setRequestInterceptor(r -> {
        r.header("Authorization", "Bearer " + access_token);
    });
    UnitApi unitApi = new UnitApi(cl);

    CreateBusinessApplication createBusinessApplication = new CreateBusinessApplication();
        CreateBusinessApplicationAttributes attr = new CreateBusinessApplicationAttributes();

        attr.setName("Peter Parker");

        Address address = new Address();
        address.setStreet("20 Ingram St");
        address.setCity("Forest Hills");
        address.setPostalCode("11375");
        address.setCountry("US");
        address.setState("NY");
        attr.setAddress(address);


        Phone p = new Phone();
        p.setNumber("5555555555");
        p.setCountryCode("1");
        attr.setPhone(p);

        attr.setStateOfIncorporation("DE");
        attr.setEin("123456789");
        attr.setEntityType(EntityType.CORPORATION);
        attr.setIp("127.0.0.1");
        attr.setAnnualRevenue(BusinessAnnualRevenue.BETWEEN250K_AND500K);
        attr.setNumberOfEmployees(BusinessNumberOfEmployees.BETWEEN100_AND500);
        attr.setCashFlow(CashFlow.PREDICTABLE);
        attr.setYearOfIncorporation("1999");
        List<String> countriesOfOperation = new ArrayList<String>();
        countriesOfOperation.add("US");
        countriesOfOperation.add("CA");

        attr.setCountriesOfOperation(countriesOfOperation);

        attr.setWebsite(null);

        String email = "richard@piedpiper.com";
        Contact contact = new Contact();
        contact.setEmail(email);
        contact.setPhone(p);
        FullName fn = new FullName();
        fn.setFirst("Peter");
        fn.setLast("Parker");
        contact.setFullName(fn);
        attr.setContact(contact);


        CreateOfficer officer = new CreateOfficer();
        officer.setAnnualIncome(AnnualIncome.BETWEEN50K_AND100K);
        officer.setFullName(fn);
        officer.setAddress(address);
        officer.setEmail(email);
        officer.setPhone(p);
        LocalDate dateOfBirh = LocalDate.of(1997, 11, 1);
        officer.setDateOfBirth(dateOfBirh);
        officer.setTitle(CreateOfficer.TitleEnum.CEO);
        officer.setOccupation(Occupation.ARCHITECT_OR_ENGINEER);
        officer.setSourceOfIncome(SourceOfIncome.BUSINESS_OWNERSHIP_INTERESTS);
        officer.setSsn("123456789");

        attr.setOfficer(officer);
        attr.setBusinessVertical(BusinessVertical.ARTS_ENTERTAINMENT_AND_RECREATION);

        List<CreateBeneficialOwner> beneficialOwners = new ArrayList<CreateBeneficialOwner>();
        CreateBeneficialOwner beneficialOwner = new CreateBeneficialOwner();
        beneficialOwner.setAddress(address);
        beneficialOwner.setFullName(fn);
        beneficialOwner.setDateOfBirth(dateOfBirh);
        beneficialOwner.setSsn("721074426");
        beneficialOwner.setEmail(email);
        beneficialOwner.setPhone(p);
        beneficialOwner.setOccupation(Occupation.ARCHITECT_OR_ENGINEER);
        beneficialOwner.setAnnualIncome(AnnualIncome.BETWEEN100K_AND250K);
        beneficialOwner.setSourceOfIncome(SourceOfIncome.BUSINESS_OWNERSHIP_INTERESTS);
        beneficialOwners.add(beneficialOwner);
        attr.setBeneficialOwners(beneficialOwners);

        createBusinessApplication.setAttributes(attr);

        CreateApplicationRequest ca = new CreateApplicationRequest();
        ca.data(new CreateApplicationRequestData(createBusinessApplication));

        UnitCreateApplicationResponse res = unitApi.createApplication(ca)

you can find more examples in the unit/e2e_tests/java directory

#### Typescript

    import {
    Address,
    AnnualIncome,
    BusinessAnnualRevenue,
    BusinessNumberOfEmployees,
    BusinessVertical,
    CashFlow,
    CreateApplicationRequest,
    EntityType,
    FullName,
    Occupation,
    OfficerTitleEnum,
    Phone,
    SourceOfIncome,
    UnitApi,
    } from "./api";
    import { Configuration } from "./configuration";

    const config: Configuration = new Configuration({
    accessToken: "access_token"
    });
    const unitApi: UnitApi = new UnitApi(config);

    const address: Address = {
    street: "20 Ingram St",
    city: "Forest Hills",
    postalCode: "11375",
    country: "US",
    state: "NY",
    };
    const phone: Phone = {
    number: "5555555555",
    countryCode: "1",
    };
    const fullName: FullName = {
    first: "Peter",
    last: "Parker",
    };
    const email = "richard@piedpiper.com";
    const dateOfBirth = "1997-11-01";
    const today = new Date();

    const req: CreateApplicationRequest = {
    data: {
        type: "businessApplication",
        attributes: {
        name: "Peter Parker",
        address,
        phone,
        stateOfIncorporation: "DE",
        ein: "123456789",
        entityType: EntityType.Corporation,
        ip: "127.0.0.1",
        annualRevenue: BusinessAnnualRevenue.Between1mAnd5m,
        numberOfEmployees: BusinessNumberOfEmployees.Between100And500,
        cashFlow: CashFlow.Predictable,
        countriesOfOperation: ["US", "CA"],
        businessVertical: BusinessVertical.AgricultureForestryFishingOrHunting,
        yearOfIncorporation: (today.getFullYear() - 2).toString(),
        contact: {
            email,
            fullName,
            phone,
        },
        officer: {
            annualIncome: AnnualIncome.Between100kAnd250k,
            fullName,
            address,
            email,
            phone,
            dateOfBirth,
            title: OfficerTitleEnum.Ceo,
            occupation: Occupation.ArchitectOrEngineer,
            sourceOfIncome: SourceOfIncome.BusinessOwnershipInterests,
            ssn: "123456789",
        },
        beneficialOwners: [
            {
            address,
            fullName,
            dateOfBirth,
            ssn: "721074426",
            email,
            phone,
            occupation: Occupation.ArchitectOrEngineer,
            sourceOfIncome: SourceOfIncome.BusinessOwnershipInterests,
            },
        ],
        },
    },
    };

    let res = await unitApi.createApplication(req);

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

### Java responses

We are using `Java native` as a library template during generation of the java sdk and there are issues with types in responses containing raw text or binary files.
In order to fix that we have a custom script in place. If you would generate a library yourself you should call the `fix-java-file-get-requests` script from package.json providing the `--path=./dist/java-sdk/src/main/java/unit/java/sdk/api/UnitApi.java` argument to fix the errors after the generation.

### CLI version

Use the follow command to check the openapi-generator-cli version:

`openapi-generator-cli version`

We recommend using v7.9.0, you can change the version with the command:

`openapi-generator-cli version-manager set 7.9.0`

## Legacy

All of the info listed below is related to the legacy functionality and will be deprecated or updated soon

### unit-python

We recommend using our script which can be found in this repository:

```commandline
python "./generate.py"
```

Or generate by yourself with swagger-codegen-cli:

```
java -jar swagger-codegen-cli-3.0.47.jar generate -i openapi.json -l python -o unit
```
