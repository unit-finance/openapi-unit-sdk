package org.openapitools.client;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.*;
import org.openapitools.client.model.*;

import static org.openapitools.client.CustomerTests.CreateIndividualCustomer;
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

    public static Account CreateDepositAccount() throws ApiException {
        IndividualCustomer customer = CreateIndividualCustomer();

        CreateDepositAccount cda = new CreateDepositAccount();
        CreateDepositAccountAttributes attributes = new CreateDepositAccountAttributes();
        attributes.setDepositProduct("checking");

        CreateDepositAccountRelationships relationships = new CreateDepositAccountRelationships();
        CustomerLinkageData customerRelationshipData = new CustomerLinkageData();
        customerRelationshipData.setId(customer.getId());
        customerRelationshipData.setType(CustomerLinkageData.TypeEnum.CUSTOMER);
        CustomerLinkage customerLinkageRelationship = new CustomerLinkage();
        customerLinkageRelationship.setData(customerRelationshipData);

        relationships.setCustomer(customerLinkageRelationship);

        cda.setAttributes(attributes);
        cda.setRelationships(relationships);

        CreateAccountApi createAccountApi = new CreateAccountApi();
        CreateAccount ca = new CreateAccount();
        ca.setData(new CreateAccountData(cda));
        return createAccountApi.execute(ca).getData();
    }

    @Test
    public void CreateDepositAccountTest() throws ApiException {
        assert CreateDepositAccount().getType().equals("depositAccount");
    }
}
