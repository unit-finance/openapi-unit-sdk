package unit.java.sdk;

import org.junit.Test;

import static unit.java.sdk.TestHelpers.GenerateCreateApplicationRequest;
import static unit.java.sdk.TestHelpers.GenerateUnitApiClient;
import unit.java.sdk.api.UnitApi;
import unit.java.sdk.model.IndividualApplication;
import unit.java.sdk.model.IndividualCustomer;
import unit.java.sdk.model.UnitCreateApplicationResponse;
import unit.java.sdk.model.UnitCustomersListResponse;

public class CustomerTests {
    UnitApi unitApi = GenerateUnitApiClient();

    public static IndividualCustomer CreateIndividualCustomer(UnitApi unitApi) throws ApiException {
        UnitCreateApplicationResponse res = unitApi.createApplication(GenerateCreateApplicationRequest());
        assert res.getData().getType().equals("individualApplication");

        IndividualApplication app = (IndividualApplication) res.getData();
        String customerId = app.getRelationships().getCustomer().getData().getId();

        return (IndividualCustomer) unitApi.getCustomer(customerId).getData();
    }

    @Test
    public void GetCustomersListApiTest() throws ApiException {
        UnitCustomersListResponse response = unitApi.getCustomersList(null, null, null);
        assert !response.getData().isEmpty();
    }

    

    @Test
    public void CreateIndividualCustomerTest() throws ApiException {
        IndividualCustomer customer = CreateIndividualCustomer(unitApi);
        assert customer.getType().equals("individualCustomer");
    }
}
