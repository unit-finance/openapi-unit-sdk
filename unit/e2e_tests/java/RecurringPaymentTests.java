package unit.java.sdk;

import org.junit.Test;

import unit.java.sdk.api.UnitApi;
import unit.java.sdk.model.UnitRecurringPaymentListResponse;
import unit.java.sdk.model.UnitRecurringPaymentResponse;


public class RecurringPaymentTests {
    UnitApi unitApi = TestHelpers.GenerateUnitApiClient();

    @Test
    public void GetRecurringPaymentListApiTest() throws ApiException {
        UnitRecurringPaymentListResponse response = unitApi.getRecurringPaymentsList(null, null, null);
        assert !response.getData().isEmpty();
    }

    @Test
    public void GetRecurringPaymentApiTest() throws ApiException {
        UnitRecurringPaymentListResponse response = unitApi.getRecurringPaymentsList(null, null, null);
        assert !response.getData().isEmpty();

        response.getData().forEach(x -> {
            try {
                UnitRecurringPaymentResponse payment = unitApi.getRecurringPayment(x.getId());
                assert payment.getData().getId().equals(x.getId());
                assert payment.getData().getType().toString().toLowerCase()
                        .equals(payment.getData().getClass().getSimpleName().toLowerCase());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
