package org.openapitools.client;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.CreateCustomerTokenApi;
import org.openapitools.client.api.GetListOrgApiTokensApi;
import org.openapitools.client.api.UnitApi;
import org.openapitools.client.model.*;

import java.util.List;

public class TokenTests {
    UnitApi unitApi = null;

    @BeforeAll
    void init() {
        String access_token = System.getenv("access_token");
        ApiClient cl = new ApiClient();
        cl.setBearerToken(access_token);
        unitApi = new UnitApi(cl);
    }

    @Test
    public void GetOrgTokensTest() throws ApiException {
        GetListOrgApiTokensApi listApi = new GetListOrgApiTokensApi();

        // TODO: Is it alright that userId is hardcoded in the test?
        List<ApiToken> response = unitApi.getApiTokens("252").getData();

        for (ApiToken t: response) {
            assert t.getType().equals("apiToken");
        }
    }

    @Test
    public void CreateCustomerToken() throws ApiException {
        
        CreateCustomerToken request = new CreateCustomerToken();
        CreateCustomerToken cct = new CreateCustomerToken();
        CreateCustomerTokenAttributes attributes = new CreateCustomerTokenAttributes();
        attributes.setScope("customers accounts");
        cct.setAttributes(attributes);
        request.setData(cct);

        UnitCustomerTokenResponse res = unitApi.createCustomerToken("1527981", request);
        assert res.getData().getType().equals("customerBearerToken");
    }
}
