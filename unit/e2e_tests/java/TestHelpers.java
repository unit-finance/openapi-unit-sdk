package unit.java.sdk;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.cdimascio.dotenv.Dotenv;

import unit.java.sdk.api.UnitApi;
import unit.java.sdk.model.Address;
import unit.java.sdk.model.AnnualIncome;
import unit.java.sdk.model.BusinessAnnualRevenue;
import unit.java.sdk.model.BusinessNumberOfEmployees;
import unit.java.sdk.model.BusinessVertical;
import unit.java.sdk.model.CashFlow;
import unit.java.sdk.model.Contact;
import unit.java.sdk.model.CreateApplicationRequest;
import unit.java.sdk.model.CreateApplicationRequestData;
import unit.java.sdk.model.CreateBeneficialOwner;
import unit.java.sdk.model.CreateBusinessApplication;
import unit.java.sdk.model.CreateBusinessApplicationAttributes;
import unit.java.sdk.model.CreateIndividualApplication;
import unit.java.sdk.model.CreateIndividualApplicationAttributes;
import unit.java.sdk.model.CreateOfficer;
import unit.java.sdk.model.CreateSoleProprietorApplication;
import unit.java.sdk.model.CreateSoleProprietorApplicationAttributes;
import unit.java.sdk.model.EntityType;
import unit.java.sdk.model.ThreadApplicationEntityType;
import unit.java.sdk.model.FullName;
import unit.java.sdk.model.Occupation;
import unit.java.sdk.model.PaymentCounterparty;
import unit.java.sdk.model.Phone;
import unit.java.sdk.model.SoleProprietorshipAnnualRevenue;
import unit.java.sdk.model.SoleProprietorshipNumberOfEmployees;
import unit.java.sdk.model.SourceOfIncome;
import unit.java.sdk.model.WirePaymentCounterparty;
import unit.java.sdk.model.CreateIndividualThreadApplication;
import unit.java.sdk.model.CreateIndividualThreadApplicationAttributes;
import unit.java.sdk.model.CreateBusinessThreadApplication;
import unit.java.sdk.model.CreateBusinessThreadApplicationAttributes;
import unit.java.sdk.model.CreateSoleProprietorThreadApplication;
import unit.java.sdk.model.CreateSoleProprietorThreadApplicationAttributes;
import unit.java.sdk.model.ThreadIndividualAccountPurpose;
import unit.java.sdk.model.ThreadIndividualSourceOfFunds;
import unit.java.sdk.model.ThreadIndividualTransactionVolume;
import unit.java.sdk.model.ThreadIndividualProfession;
import unit.java.sdk.model.ThreadBusinessAccountPurpose;
import unit.java.sdk.model.ThreadBusinessSourceOfFunds;
import unit.java.sdk.model.ThreadBusinessTransactionVolume;
import unit.java.sdk.model.ThreadIndividualTransactionVolume;
import unit.java.sdk.model.ThreadSoleProprietorshipTransactionVolume;
import unit.java.sdk.model.ThreadBusinessIndustry;
import unit.java.sdk.model.ThreadBusinessUsNexus;

public class TestHelpers {
    private static UnitApi unitApi;
    private static UnitApi threadUnitApi;
    
    static UnitApi GenerateUnitApiClient() {
        if(unitApi == null){
            // Load from .env file first, fallback to environment variable
            Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();
            
            final String access_token;
            String token = dotenv.get("access_token");
            if (token == null) {
                token = System.getenv("access_token");
            }
            access_token = token;
            
        ApiClient cl = new ApiClient();
        ObjectMapper mapper = cl.getObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        cl.setObjectMapper(mapper);
        cl.setRequestInterceptor(r -> {
            r.header("Authorization", "Bearer " + access_token);
        });
        unitApi = new UnitApi(cl);
        }

        return unitApi;
    }
    
    static UnitApi GenerateThreadUnitApiClient() {
        if(threadUnitApi == null){
            // Load from .env file first, fallback to environment variable
            Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();
            
            final String thread_access_token;
            String token = dotenv.get("thread_access_token");
            if (token == null) {
                token = System.getenv("thread_access_token");
            }
            thread_access_token = token;
            
        ApiClient cl = new ApiClient();
        ObjectMapper mapper = cl.getObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        cl.setObjectMapper(mapper);
        cl.setRequestInterceptor(r -> {
            r.header("Authorization", "Bearer " + thread_access_token);
        });
        threadUnitApi = new UnitApi(cl);
        }

        return threadUnitApi;
    }

    public static CreateApplicationRequest GenerateCreateIndividualApplicationRequest(String ssn) {
        if (ssn == null) ssn = "721074426";
        CreateIndividualApplication createIndividualApplication = new CreateIndividualApplication();
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

        attr.setSsn(ssn);
        attr.setDateOfBirth(LocalDate.parse("2001-08-10"));
        attr.setEmail("peter@oscorp.com");
        Phone p = new Phone();
        p.setNumber("5555555555");
        p.setCountryCode("1");
        attr.setPhone(p);
        attr.setOccupation(Occupation.ARCHITECT_OR_ENGINEER);

        createIndividualApplication.setAttributes(attr);

        CreateApplicationRequest ca = new CreateApplicationRequest();
        ca.data(new CreateApplicationRequestData(createIndividualApplication));

        return ca;
    }

    public static CreateApplicationRequest GenerateCreateSoleProprietorApplicationRequest() {
        CreateSoleProprietorApplication createSoleProprietorApplication = new CreateSoleProprietorApplication();
        CreateSoleProprietorApplicationAttributes attr = new CreateSoleProprietorApplicationAttributes();

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
        attr.setEmail("peter@oscorp.com");
        Phone p = new Phone();
        p.setNumber("5555555555");
        p.setCountryCode("1");
        attr.setPhone(p);
        attr.setSoleProprietorship(true);
        attr.setBusinessVertical(BusinessVertical.BUSINESS_SUPPORT_OR_BUILDING_SERVICES);
        attr.setAnnualRevenue(SoleProprietorshipAnnualRevenue.BETWEEN100K_AND200K);
        attr.setNumberOfEmployees(SoleProprietorshipNumberOfEmployees.BETWEEN5_AND10);

        createSoleProprietorApplication.setAttributes(attr);

        CreateApplicationRequest ca = new CreateApplicationRequest();
        ca.data(new CreateApplicationRequestData(createSoleProprietorApplication));

        return ca;
    }

    public static CreateApplicationRequest GenerateCreateBusinessApplicationRequest() {
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
        beneficialOwner.setPercentage(100);
        beneficialOwners.add(beneficialOwner);
        attr.setBeneficialOwners(beneficialOwners);

        createBusinessApplication.setAttributes(attr);

        CreateApplicationRequest ca = new CreateApplicationRequest();
        ca.data(new CreateApplicationRequestData(createBusinessApplication));

        return ca;
    }

    public static PaymentCounterparty CreatePaymentCounterparty() {
        PaymentCounterparty counterparty = new PaymentCounterparty();
        counterparty.setName("Jane Doe");
        counterparty.setRoutingNumber("812345673");
        counterparty.setAccountNumber("12345569");
        counterparty.setAccountType(PaymentCounterparty.AccountTypeEnum.CHECKING);

        return counterparty;
    }

    public static Address CreateAddress() {
        Address address = new Address();
        address.setStreet("20 Ingram St");
        address.setCity("Forest Hills");
        address.setState("CA");
        address.setPostalCode("11375");
        address.setCountry("US");

        return address;
    }

    public static WirePaymentCounterparty CreateWirePaymentCounterparty() {
        WirePaymentCounterparty counterparty = new WirePaymentCounterparty();
        counterparty.setName("April Oniel");
        counterparty.setAccountNumber("1000000001");
        counterparty.setRoutingNumber("812345678");
        counterparty.setAddress(CreateAddress());

        return counterparty;
    }

    public static CreateApplicationRequest GenerateCreateIndividualThreadApplicationRequest(String ssn) {
        if (ssn == null) ssn = "721074426";
        CreateIndividualThreadApplication createIndividualThreadApplication = new CreateIndividualThreadApplication();
        CreateIndividualThreadApplicationAttributes attr = new CreateIndividualThreadApplicationAttributes();

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

        attr.setSsn(ssn);
        attr.setDateOfBirth(LocalDate.parse("2001-08-10"));
        attr.setEmail("peter@oscorp.com");
        Phone p = new Phone();
        p.setNumber("5555555555");
        p.setCountryCode("1");
        attr.setPhone(p);

        // Thread application specific fields
        attr.setAccountPurpose(ThreadIndividualAccountPurpose.PAYROLL_OR_DIRECT_DEPOSIT);
        attr.setAccountPurposeDetail("Receiving monthly salary payments");
        attr.setSourceOfFunds(ThreadIndividualSourceOfFunds.SALARY_OR_WAGES);
        attr.setTransactionVolume(ThreadIndividualTransactionVolume.BETWEEN1_K_AND5_K);
        attr.setProfession(ThreadIndividualProfession.ENGINEER);

        createIndividualThreadApplication.setAttributes(attr);

        CreateApplicationRequest ca = new CreateApplicationRequest();
        ca.data(new CreateApplicationRequestData(createIndividualThreadApplication));

        return ca;
    }

    public static CreateApplicationRequest GenerateCreateBusinessThreadApplicationRequest() {
        CreateBusinessThreadApplication app = new CreateBusinessThreadApplication();
        CreateBusinessThreadApplicationAttributes attr = new CreateBusinessThreadApplicationAttributes();

        // Basic business information
        attr.setName("Tech Startup LLC");
        attr.setEin("123456789");
        attr.setEntityType(ThreadApplicationEntityType.LLC);
        attr.setStateOfIncorporation("DE");
        attr.setYearOfIncorporation("2020");

        // Address
        Address address = new Address();
        address.setStreet("123 Main St");
        address.setCity("San Francisco");
        address.setState("CA");
        address.setPostalCode("94105");
        address.setCountry("US");
        attr.setAddress(address);

        // Phone
        Phone phone = new Phone();
        phone.setCountryCode("1");
        phone.setNumber("4155551234");
        attr.setPhone(phone);

        // Contact person
        Contact contact = new Contact();
        FullName contactName = new FullName();
        contactName.setFirst("John");
        contactName.setLast("Doe");
        contact.setFullName(contactName);
        contact.setEmail("john@techstartup.com");
        contact.setPhone(phone);
        attr.setContact(contact);

        // Officer
        CreateOfficer officer = new CreateOfficer();
        officer.setFullName(contactName);
        officer.setEmail("john@techstartup.com");
        officer.setPhone(phone);
        officer.setAddress(address);
        officer.setDateOfBirth(LocalDate.of(1985, 6, 15));
        officer.setSsn("123456789");
        officer.setTitle(CreateOfficer.TitleEnum.CEO);
        officer.setOccupation(Occupation.ARCHITECT_OR_ENGINEER);
        officer.setAnnualIncome(AnnualIncome.BETWEEN100K_AND250K);
        officer.setSourceOfIncome(SourceOfIncome.BUSINESS_OWNERSHIP_INTERESTS);
        attr.setOfficer(officer);

        // Beneficial owners
        List<CreateBeneficialOwner> beneficialOwners = new ArrayList<>();
        CreateBeneficialOwner owner = new CreateBeneficialOwner();
        owner.setFullName(contactName);
        owner.setEmail("john@techstartup.com");
        owner.setPhone(phone);
        owner.setAddress(address);
        owner.setDateOfBirth(LocalDate.of(1985, 6, 15));
        owner.setSsn("123456789");
        owner.setOccupation(Occupation.ARCHITECT_OR_ENGINEER);
        owner.setAnnualIncome(AnnualIncome.BETWEEN100K_AND250K);
        owner.setSourceOfIncome(SourceOfIncome.BUSINESS_OWNERSHIP_INTERESTS);
        owner.setPercentage(100);
        beneficialOwners.add(owner);
        attr.setBeneficialOwners(beneficialOwners);

        // Thread-specific required fields
        attr.setAccountPurpose(ThreadBusinessAccountPurpose.RETAIL_SALES_IN_PERSON);
        attr.setAccountPurposeDetail("Processing customer payments for retail operations");
        attr.setSourceOfFunds(ThreadBusinessSourceOfFunds.SALES_OF_SERVICES);
        attr.setTransactionVolume(ThreadBusinessTransactionVolume.BETWEEN10_K_AND50_K);
        attr.setBusinessIndustry(ThreadBusinessIndustry.CONSTRUCTION);
        attr.setBusinessDescription("Technology consulting and software development services");
        attr.setIsRegulated(false);

        // US Nexus (required)
        List<ThreadBusinessUsNexus> usNexus = new ArrayList<>();
        usNexus.add(ThreadBusinessUsNexus.PHYSICAL_OFFICE_OR_FACILITY);
        attr.setUsNexus(usNexus);

        // Countries of operation (required)
        List<String> countries = new ArrayList<>();
        countries.add("US");
        attr.setCountriesOfOperation(countries);

        app.setAttributes(attr);

        CreateApplicationRequest request = new CreateApplicationRequest();
        request.data(new CreateApplicationRequestData(app));

        return request;
    }

    public static CreateApplicationRequest GenerateCreateSoleProprietorThreadApplicationRequest() {
        CreateSoleProprietorThreadApplication app = new CreateSoleProprietorThreadApplication();
        CreateSoleProprietorThreadApplicationAttributes attr = new CreateSoleProprietorThreadApplicationAttributes();

        // Personal information
        FullName fullName = new FullName();
        fullName.setFirst("Jane");
        fullName.setLast("Smith");
        attr.setFullName(fullName);

        attr.setSsn("721074426");
        attr.setDateOfBirth(LocalDate.of(1985, 3, 20));
        attr.setEmail("jane.smith@business.com");

        // Address
        Address address = new Address();
        address.setStreet("456 Market St");
        address.setCity("Los Angeles");
        address.setState("CA");
        address.setPostalCode("90012");
        address.setCountry("US");
        attr.setAddress(address);

        // Phone
        Phone phone = new Phone();
        phone.setCountryCode("1");
        phone.setNumber("3105551234");
        attr.setPhone(phone);

        // Sole proprietorship flag
        attr.setSoleProprietorship(true);

        // Optional business details
        attr.setDba("Smith Consulting");
        attr.setEin("123456789");

        // Thread-specific required fields
        attr.setAccountPurpose(ThreadBusinessAccountPurpose.RETAIL_SALES_IN_PERSON);
        attr.setAccountPurposeDetail("Managing consulting fees and client payments");
        attr.setSourceOfFunds(ThreadBusinessSourceOfFunds.SALES_OF_SERVICES);
        attr.setTransactionVolume(ThreadSoleProprietorshipTransactionVolume.BETWEEN5_K_AND20_K);
        attr.setBusinessIndustry(ThreadBusinessIndustry.CONSTRUCTION);
        attr.setBusinessDescription("Consulting and professional services");

        // US Nexus (required)
        List<ThreadBusinessUsNexus> usNexus = new ArrayList<>();
        usNexus.add(ThreadBusinessUsNexus.PHYSICAL_OFFICE_OR_FACILITY);
        attr.setUsNexus(usNexus);

        // Countries of operation (required)
        List<String> countries = new ArrayList<>();
        countries.add("US");
        attr.setCountriesOfOperation(countries);

        app.setAttributes(attr);

        CreateApplicationRequest request = new CreateApplicationRequest();
        request.data(new CreateApplicationRequestData(app));

        return request;
    }
}
