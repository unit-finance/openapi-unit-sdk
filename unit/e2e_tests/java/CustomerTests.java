package org.openapitools.client;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.CreateApplicationApi;
import org.openapitools.client.api.GetCustomerApi;
import org.openapitools.client.api.GetListCustomersApi;
import org.openapitools.client.api.UnitApi;
import org.openapitools.client.model.IndividualApplication;
import org.openapitools.client.model.IndividualCustomer;
import org.openapitools.client.model.UnitCreateApplicationResponse;
import org.openapitools.client.model.UnitCustomersListResponse;

import static org.openapitools.client.TestHelpers.CreateApplicationRequest;

public class CustomerTests {
    UnitApi unitApi = null;

    @BeforeAll
    void init() {
        String access_token = System.getenv("access_token");
        ApiClient cl = new ApiClient();
        cl.setBearerToken(access_token);
        unitApi = new UnitApi(cl);
    }

    @Test
    public void GetCustomersListApiTest() throws ApiException {
        UnitCustomersListResponse response = unitApi.getCustomers(null, null, null);
        assert response.getData().size() > 0;
    }

    public IndividualCustomer CreateIndividualCustomer() throws ApiException {
        UnitCreateApplicationResponse res = unitApi.createApplication(CreateApplicationRequest());
        assert res.getData().getType().equals("individualApplication");

        IndividualApplication app = (IndividualApplication) res.getData();
        String customerId = app.getRelationships().getCustomer().getData().getId();

        return (IndividualCustomer) unitApi.getCustomerById(customerId).getData();
    }

    @Test
    public void CreateIndividualCustomerTest() throws ApiException {
        IndividualCustomer customer = CreateIndividualCustomer();
        assert customer.getType().equals("individualCustomer");
    }
}
