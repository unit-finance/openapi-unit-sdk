package unit.java.sdk;

import java.time.LocalDate;

import unit.java.sdk.api.UnitApi;
import unit.java.sdk.model.Address;
import unit.java.sdk.model.CreateApplicationRequest;
import unit.java.sdk.model.CreateApplicationRequestData;
import unit.java.sdk.model.PaymentCounterparty;
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
