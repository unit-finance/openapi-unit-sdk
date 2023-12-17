package org.openapitools.client;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.CreateCustomerTokenApi;
import org.openapitools.client.api.GetListOrgApiTokensApi;
import org.openapitools.client.model.*;

import java.util.List;

public class TokenTests {
    @BeforeAll
    static void init() {
        String access_token = System.getenv("access_token");
        ApiClient cl = new ApiClient();
        cl.setBearerToken(access_token);
        Configuration.setDefaultApiClient(cl);
    }

    @Test
    public void GetOrgTokensTest() throws ApiException {
        GetListOrgApiTokensApi listApi = new GetListOrgApiTokensApi();
        List<ApiToken> response = listApi.execute("252").getData();

        for (ApiToken t: response) {
            assert t.getType().equals("apiToken");
        }
    }

    @Test
    public void CreateCustomerToken() throws ApiException {
        CreateCustomerTokenApi createApi = new CreateCustomerTokenApi();
        ExecuteRequest16 request = new ExecuteRequest16();

        CreateCustomerToken cct = new CreateCustomerToken();
        CreateCustomerTokenAttributes attributes = new CreateCustomerTokenAttributes();
        attributes.setScope("customers accounts");
        cct.setAttributes(attributes);
        request.setData(cct);

        UnitCustomerTokenResponse res = createApi.execute("1527981", request);
        assert res.getData().getType().equals("customerBearerToken");
    }
}
