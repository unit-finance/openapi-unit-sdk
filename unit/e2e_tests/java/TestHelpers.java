package unit.java.sdk;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
import unit.java.sdk.model.CreateBusinessApplication;
import unit.java.sdk.model.CreateBusinessApplicationAttributes;
import unit.java.sdk.model.CreateOfficer;
import unit.java.sdk.model.EntityType;
import unit.java.sdk.model.FullName;
import unit.java.sdk.model.Occupation;
import unit.java.sdk.model.PaymentCounterparty;
import unit.java.sdk.model.Phone;
import unit.java.sdk.model.SourceOfIncome;
import unit.java.sdk.model.WirePaymentCounterparty;

public class TestHelpers {
    private static UnitApi unitApi;
    static UnitApi GenerateUnitApiClient() {
        if(unitApi == null){
            String access_token = System.getenv("access_token");
        ApiClient cl = new ApiClient();
        cl.setRequestInterceptor(r -> r.header("Authorization", "Bearer " + access_token));
        unitApi = new UnitApi(cl);
        }

        return unitApi;
    }

    public static CreateApplicationRequest GenerateCreateApplicationRequest() {
        unit.java.sdk.model.CreateIndividualApplication createIndividualApplication = new unit.java.sdk.model.CreateIndividualApplication();
        unit.java.sdk.model.CreateIndividualApplicationAttributes attr = new unit.java.sdk.model.CreateIndividualApplicationAttributes();

        unit.java.sdk.model.FullName fn = new unit.java.sdk.model.FullName();
        fn.setFirst("Peter");
        fn.setLast("Parker");
        attr.setFullName(fn);

        unit.java.sdk.model.Address address = new unit.java.sdk.model.Address();
        address.setStreet("20 Ingram St");
        address.setCity("Forest Hills");
        address.setPostalCode("11375");
        address.setCountry("US");
        address.setState("NY");
        attr.setAddress(address);

        attr.setSsn("721074426");
        attr.setDateOfBirth(LocalDate.parse("2001-08-10"));
        attr.setEmail("peter@oscorp.com");
        unit.java.sdk.model.Phone p = new unit.java.sdk.model.Phone();
        p.setNumber("5555555555");
        p.setCountryCode("1");
        attr.setPhone(p);
        attr.setIdempotencyKey("3a1a33be-4e12-4603-9ed0-820922389fb8");
        attr.setOccupation(unit.java.sdk.model.Occupation.ARCHITECTORENGINEER);

        createIndividualApplication.setAttributes(attr);

        CreateApplicationRequest ca = new CreateApplicationRequest();
        ca.data(new CreateApplicationRequestData(createIndividualApplication));

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
        attr.setAnnualRevenue(BusinessAnnualRevenue.BETWEEN250KAND500K);
        attr.setNumberOfEmployees(BusinessNumberOfEmployees.BETWEEN100AND500);
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
        officer.setAnnualIncome(AnnualIncome.BETWEEN50KAND100K);
        officer.setFullName(fn);
        officer.setAddress(address);
        officer.setEmail(email);
        officer.setPhone(p);
        LocalDate dateOfBirh = LocalDate.of(1997, 11, 1);
        officer.setDateOfBirth(dateOfBirh);
        officer.setTitle(CreateOfficer.TitleEnum.CEO);
        officer.setOccupation(Occupation.ARCHITECTORENGINEER);
        officer.setSourceOfIncome(SourceOfIncome.BUSINESSOWNERSHIPINTERESTS);
        officer.setSsn("721074426");

        attr.setOfficer(officer);
        attr.setBusinessVertical(BusinessVertical.ARTSENTERTAINMENTANDRECREATION);

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
}
