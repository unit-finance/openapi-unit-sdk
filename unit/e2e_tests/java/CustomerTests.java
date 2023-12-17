package org.openapitools.client;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.CreateApplicationApi;
import org.openapitools.client.api.GetCustomerApi;
import org.openapitools.client.api.GetListCustomersApi;
import org.openapitools.client.model.IndividualApplication;
import org.openapitools.client.model.IndividualCustomer;
import org.openapitools.client.model.UnitCreateApplicationResponse;
import org.openapitools.client.model.UnitCustomersListResponse;

import static org.openapitools.client.TestHelpers.CreateApplicationRequest;

public class CustomerTests {
    @BeforeAll
    static void init() {
        String access_token = System.getenv("access_token");
        ApiClient cl = new ApiClient();
        cl.setBearerToken(access_token);
        Configuration.setDefaultApiClient(cl);
    }

    @Test
    public void GetCustomersListApiTest() throws ApiException {
        GetListCustomersApi api = new GetListCustomersApi();

        UnitCustomersListResponse response = api.execute(null, null, null);
        assert response.getData().size() > 0;
    }

    public static IndividualCustomer CreateIndividualCustomer() throws ApiException {
        CreateApplicationApi apiClient = new CreateApplicationApi();
        UnitCreateApplicationResponse res = apiClient.execute(CreateApplicationRequest());
        assert res.getData().getType().equals("individualApplication");

        IndividualApplication app = (IndividualApplication) res.getData();
        String customerId = app.getRelationships().getCustomer().getData().getId();
        GetCustomerApi customerApi = new GetCustomerApi();

        return (IndividualCustomer) customerApi.execute(customerId).getData();
    }

    @Test
    public void CreateIndividualCustomerTest() throws ApiException {
        IndividualCustomer customer = CreateIndividualCustomer();
        assert customer.getType().equals("individualCustomer");
    }
}
