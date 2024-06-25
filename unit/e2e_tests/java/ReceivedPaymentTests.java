package unit.java.sdk;

import org.junit.Test;

import static unit.java.sdk.TestHelpers.GenerateUnitApiClient;
import unit.java.sdk.api.UnitApi;
import unit.java.sdk.model.UnitReceivedPaymentListResponse;
import unit.java.sdk.model.UnitReceivedPaymentResponseWithIncluded;

public class ReceivedPaymentTests {
    UnitApi unitApi = GenerateUnitApiClient();
    
    @Test
    public void GetReceivedPaymentListApiTest() throws ApiException {
        UnitReceivedPaymentListResponse response = unitApi.getReceivedPaymentsList(null);
        assert !response.getData().isEmpty();
    }

    @Test
    public void GetReceivedPaymentListWithIncludedApiTest() throws ApiException {
        UnitReceivedPaymentListResponse response = unitApi.getReceivedPaymentsList("customer");
        assert !response.getData().isEmpty();
    }

    @Test
    public void GetReceivedPaymentApiTest() throws ApiException {
        UnitReceivedPaymentListResponse response = unitApi.getReceivedPaymentsList(null);
        assert !response.getData().isEmpty();

        response.getData().forEach(x -> {
            try {
                UnitReceivedPaymentResponseWithIncluded payment = unitApi.getReceivedPayment(x.getId(), null);
                assert payment.getData().getId().equals(x.getId());
                assert payment.getData().getAttributes().getAmount().equals(x.getAttributes().getAmount());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
