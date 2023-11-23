package org.openapitools.client;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.GetAccountApi;
import org.openapitools.client.api.GetListAccountsApi;
import org.openapitools.client.api.UpdateAccountApi;
import org.openapitools.client.model.*;

import java.util.HashMap;

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
    @Test
    public void UpdateAccountApiTest() throws ApiException {
        GetListAccountsApi api = new GetListAccountsApi();

        UnitAccountsListResponse response = api.execute(null, null, null);
        assert response.getData().size() > 0;

        GetAccountApi getApi = new GetAccountApi();

        UpdateAccountApi updateAccountApi = new UpdateAccountApi();

        response.getData().forEach(x -> {
            try {
                UnitAccountResponseWithIncluded account = getApi.execute(x.getId(), null);
                assert account.getData().getId().equals(x.getId());
                assert account.getData().getType().toLowerCase()
                        .equals(account.getData().getClass().getSimpleName().toLowerCase());

                UpdateDepositAccountAttributes attributes = new UpdateDepositAccountAttributes();
                HashMap<String, String> tags = new HashMap<>();
                tags.put("test", "open-api-sdk");
                attributes.tags(tags);

                UpdateDepositAccount updateDepositAccount = new UpdateDepositAccount();
                updateDepositAccount.attributes(attributes);
                PatchAccount pa = new PatchAccount().data(new PatchAccountData(updateDepositAccount));

                UnitAccountResponse res = updateAccountApi.execute(x.getId(), pa);
                res.getData().getId().equals(x.getId());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
