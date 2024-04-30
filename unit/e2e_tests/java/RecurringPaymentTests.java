package org.openapitools.client;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.GetListRecurringPaymentsApi;
import org.openapitools.client.api.GetRecurringPaymentApi;
import org.openapitools.client.api.UnitApi;
import org.openapitools.client.model.UnitRecurringPaymentListResponse;
import org.openapitools.client.model.UnitRecurringPaymentResponse;


public class RecurringPaymentTests {
    UnitApi unitApi = null;

    @BeforeAll
    static void init() {
        String access_token = System.getenv("access_token");
        ApiClient cl = new ApiClient();
        cl.setBearerToken(access_token);
        Configuration.setDefaultApiClient(cl);
    }

    @Test
    public void GetRecurringPaymentListApiTest() throws ApiException {
        UnitRecurringPaymentListResponse response = unitApi.getRecurringPayments();
        assert response.getData().size() != 0;
    }

    @Test
    public void GetRecurringPaymentApiTest() throws ApiException {
        GetListRecurringPaymentsApi api = new GetListRecurringPaymentsApi();

        UnitRecurringPaymentListResponse response = unitApi.getRecurringPayments();
        assert response.getData().size() != 0;

        response.getData().forEach(x -> {
            try {
                UnitRecurringPaymentResponse payment = unitApi.getRecurringPaymentById(x.getId());
                assert payment.getData().getId().equals(x.getId());
                assert payment.getData().getType().toLowerCase()
                        .equals(payment.getData().getClass().getSimpleName().toLowerCase());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
