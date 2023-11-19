package org.openapitools.client;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.GetAccountApi;
import org.openapitools.client.api.GetListAccountsApi;
import org.openapitools.client.model.UnitAccountResponseWithIncluded;
import org.openapitools.client.model.UnitAccountsListResponse;

public class AccountTests {
    @BeforeAll
    static void init() {
        String access_token = System.getenv("access_token");
        ApiClient cl = new ApiClient();
        cl.setBearerToken(access_token);
        Configuration.setDefaultApiClient(cl);
    }

    @Test
    public void GetAccountListApiTest() throws ApiException {
        GetListAccountsApi api = new GetListAccountsApi();

        UnitAccountsListResponse response = api.execute(null, null, null);
        assert response.getData().size() > 0;
    }

    @Test
    public void GetAccountApiTest() throws ApiException {
        GetListAccountsApi api = new GetListAccountsApi();

        UnitAccountsListResponse response = api.execute(null, null, null);
        assert response.getData().size() > 0;

        GetAccountApi getApi = new GetAccountApi();

        response.getData().forEach(x -> {
            try {
                UnitAccountResponseWithIncluded account = getApi.execute(x.getId(), null);
                assert account.getData().getId().equals(x.getId());
                assert account.getData().getType().toLowerCase()
                        .equals(account.getData().getClass().getSimpleName().toLowerCase());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });


    }
}
