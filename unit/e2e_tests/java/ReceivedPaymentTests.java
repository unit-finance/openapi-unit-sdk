package org.openapitools.client;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.GetReceivedPaymentApi;
import org.openapitools.client.api.GetReceivedPaymentsListApi;
import org.openapitools.client.model.UnitReceivedPaymentListResponse;
import org.openapitools.client.model.UnitReceivedPaymentResponseWithIncluded;

public class ReceivedPaymentTests {
    @BeforeAll
    static void init() {
        String access_token = System.getenv("access_token");
        ApiClient cl = new ApiClient();
        cl.setBearerToken(access_token);
        Configuration.setDefaultApiClient(cl);
    }

    @Test
    public void GetReceivedPaymentListApiTest() throws ApiException {
        GetReceivedPaymentsListApi api = new GetReceivedPaymentsListApi();

        UnitReceivedPaymentListResponse response = api.execute(null);
        assert response.getData().size() != 0;
    }

    @Test
    public void GetReceivedPaymentListWithIncludedApiTest() throws ApiException {
        GetReceivedPaymentsListApi api = new GetReceivedPaymentsListApi();

        UnitReceivedPaymentListResponse response = api.execute(null);
        assert response.getData().size() != 0;
    }

    @Test
    public void GetReceivedPaymentApiTest() throws ApiException {
        GetReceivedPaymentsListApi api = new GetReceivedPaymentsListApi();

        UnitReceivedPaymentListResponse response = api.execute(null);
        assert response.getData().size() != 0;

        GetReceivedPaymentApi getApi = new GetReceivedPaymentApi();

        response.getData().forEach(x -> {
            try {
                UnitReceivedPaymentResponseWithIncluded payment = getApi.execute(x.getId(), null);
                assert payment.getData().getId().equals(x.getId());
                assert payment.getData().getAttributes().getAmount().equals(x.getAttributes().getAmount());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
