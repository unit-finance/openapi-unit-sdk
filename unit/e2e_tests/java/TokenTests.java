package unit.java.sdk;

import static unit.java.sdk.TestHelpers.GenerateUnitApiClient;
import static unit.java.sdk.CustomerTests.CreateIndividualCustomer;
// import static unit.java.sdk.AccountTests.CreateDepositAccount;

// import java.time.Instant;
// import java.time.OffsetDateTime;
// import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import unit.java.sdk.api.UnitApi;
import unit.java.sdk.model.ApiToken;
// import unit.java.sdk.model.CreateApiTokenRequest;
// import unit.java.sdk.model.CreateApiTokenRequestData;
// import unit.java.sdk.model.CreateApiTokenRequestDataAttributes;
import unit.java.sdk.model.CreateApiTokenRequestDataAttributesResourcesInner;
import unit.java.sdk.model.CreateCustomerTokenRequest;
import unit.java.sdk.model.CreateCustomerTokenRequestData;
import unit.java.sdk.model.CreateCustomerTokenRequestDataAttributes;
import unit.java.sdk.model.CreateCustomerTokenVerificationRequest;
import unit.java.sdk.model.CreateCustomerTokenVerificationRequestData;
import unit.java.sdk.model.CreateCustomerTokenVerificationRequestDataAttributes;
// import unit.java.sdk.model.Customer;
import unit.java.sdk.model.CustomerToken;
import unit.java.sdk.model.CustomerTokenVerification;
// import unit.java.sdk.model.DepositAccount;
import unit.java.sdk.model.IndividualCustomer;
// import unit.java.sdk.model.UnitApiTokenResponse;
import unit.java.sdk.model.UnitCustomerTokenResponse;
import unit.java.sdk.model.UnitCustomerTokenVerificationResponse;
import unit.java.sdk.model.CreateCustomerTokenVerificationRequestDataAttributes.ChannelEnum;

public class TokenTests {
    UnitApi unitApi = GenerateUnitApiClient();
    String testUserId = System.getenv("test_user_id");

    // Uncomment once createApiToken resource starts accepting different types of RFC3339
    // @Test
    // public void CreateOrgApiTokenApiTest() throws ApiException {
    //     CreateApiTokenRequest req = new CreateApiTokenRequest();
    //     CreateApiTokenRequestData data = new CreateApiTokenRequestData();
    //     CreateApiTokenRequestDataAttributes attributes = new CreateApiTokenRequestDataAttributes();

    //     Instant instant = Instant.now();
    //     attributes.setScope("customers applications");
    //     attributes.setDescription("Test token");
    //     attributes.setExpiration(instant.atOffset(ZoneOffset.UTC));
    //     List<CreateApiTokenRequestDataAttributesResourcesInner> resources = new ArrayList<CreateApiTokenRequestDataAttributesResourcesInner>();
    //     CreateApiTokenRequestDataAttributesResourcesInner resource = new CreateApiTokenRequestDataAttributesResourcesInner();
    //     DepositAccount account = CreateDepositAccount(unitApi, Customer.TypeEnum.INDIVIDUAL_CUSTOMER);

    //     resource.setType(CreateApiTokenRequestDataAttributesResourcesInner.TypeEnum.ACCOUNT);
    //     List<String> ids = new ArrayList<String>();
    //     ids.add(account.getId());

    //     resource.setIds(ids);
    //     resources.add(resource);
    //     // attributes.setResources(resources);

    //     data.setAttributes(attributes);
    //     data.setType(CreateApiTokenRequestData.TypeEnum.APITOKEN);
    //     req.setData(data);
    //     System.err.println(data.toString());

    //     UnitApiTokenResponse res = unitApi.createApiToken(testUserId, req);
    //     assert res.getData().getType().equals(ApiToken.TypeEnum.APITOKEN);
    // }

    @Test
    public void GetOrgTokensApiTest() throws ApiException {
        List<ApiToken> response = unitApi.getApiTokensList(testUserId).getData();

        for (ApiToken t: response) {
            assert t.getType().equals(ApiToken.TypeEnum.API_TOKEN);
        }
    }

    @Test
    public void CreateCustomerTokenApiTest() throws ApiException {
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
        assert res.getData().getType().equals(CustomerToken.TypeEnum.CUSTOMER_BEARER_TOKEN);
    }

    @Test
    public void CreateCustomerTokenVerificationApiTest() throws ApiException {
        IndividualCustomer customer = CreateIndividualCustomer(unitApi);
        CreateCustomerTokenVerificationRequest req = new CreateCustomerTokenVerificationRequest();
        CreateCustomerTokenVerificationRequestData data = new CreateCustomerTokenVerificationRequestData();
        CreateCustomerTokenVerificationRequestDataAttributes attributes = new CreateCustomerTokenVerificationRequestDataAttributes();
        attributes.setChannel(ChannelEnum.SMS);
        data.setAttributes(attributes);
        data.setType(CreateCustomerTokenVerificationRequestData.TypeEnum.CUSTOMER_TOKEN_VERIFICATION);
        req.setData(data);
        
        UnitCustomerTokenVerificationResponse res = unitApi.createCustomerTokenVerification(customer.getId(), req);
        assert res.getData().getType().equals(CustomerTokenVerification.TypeEnum.CUSTOMER_TOKEN_VERIFICATION);
    }
} 
