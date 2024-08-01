package unit.java.sdk;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import unit.java.sdk.api.UnitApi;
import unit.java.sdk.model.ApiToken;
import unit.java.sdk.model.CreateApiTokenRequestDataAttributesResourcesInner;
import unit.java.sdk.model.CreateCustomerTokenRequest;
import unit.java.sdk.model.CreateCustomerTokenRequestData;
import unit.java.sdk.model.CreateCustomerTokenRequestDataAttributes;
import unit.java.sdk.model.CustomerToken;
import unit.java.sdk.model.UnitCustomerTokenResponse;

public class TokenTests {
    UnitApi unitApi = TestHelpers.GenerateUnitApiClient();

    @Test
    public void GetOrgTokensTest() throws ApiException {
        // TODO: Is it alright that userId is hardcoded in the test?
        List<ApiToken> response = unitApi.getApiTokensList("252").getData();

        for (ApiToken t: response) {
            assert t.getType().equals(ApiToken.TypeEnum.APITOKEN);
        }
    }

    @Test
    public void CreateCustomerToken() throws ApiException {
        CreateCustomerTokenRequest request = new CreateCustomerTokenRequest();
        CreateCustomerTokenRequestData cct = new CreateCustomerTokenRequestData();
        CreateCustomerTokenRequestDataAttributes attributes = new CreateCustomerTokenRequestDataAttributes();
        attributes.setScope("customers accounts");

        CreateApiTokenRequestDataAttributesResourcesInner resource = new CreateApiTokenRequestDataAttributesResourcesInner();
        resource.setType(CreateApiTokenRequestDataAttributesResourcesInner.TypeEnum.ACCOUNT);
        List<String> Ids = new ArrayList<String>();
        Ids.add("1527981");
        resource.setIds(Ids);
        
        List<CreateApiTokenRequestDataAttributesResourcesInner> resources = new ArrayList<CreateApiTokenRequestDataAttributesResourcesInner>();
        resources.add(resource);
        attributes.setResources(resources);
        
        cct.setAttributes(attributes);
        request.setData(cct);

        UnitCustomerTokenResponse res = unitApi.createCustomerToken("1527981", request);
        assert res.getData().getType().equals(CustomerToken.TypeEnum.CUSTOMERBEARERTOKEN);
    }
}
