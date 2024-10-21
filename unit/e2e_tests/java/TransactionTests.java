package unit.java.sdk;

import static unit.java.sdk.TestHelpers.GenerateUnitApiClient;

import org.junit.Test;

import unit.java.sdk.api.UnitApi;
import unit.java.sdk.model.ListPageParameters;
import unit.java.sdk.model.UnitTransactionsListResponse;

public class TransactionTests {
    UnitApi unitApi = GenerateUnitApiClient();

    @Test
    public void GetTransactionsApiTest() throws ApiException {
        ListPageParameters params = new ListPageParameters();
        params.setLimit(20);
        UnitTransactionsListResponse res = unitApi.getTransactionsList(null, null, null, null);
        res.getData().forEach(transaction -> {
            assert transaction != null;
        });
    }
}
