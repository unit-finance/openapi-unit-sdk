package org.openapitools.client;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.*;
import org.openapitools.client.model.*;

import static org.openapitools.client.CustomerTests.CreateIndividualCustomer;
import java.util.HashMap;

public class AccountTests {
    UnitApi unitApi = null;
    
    @BeforeAll
    void init() {
        String access_token = System.getenv("access_token");
        ApiClient cl = new ApiClient();
        cl.setBearerToken(access_token);
        unitApi = new UnitApi(cl);
    }

    @Test
    public void GetAccountListApiTest() throws ApiException {
        UnitAccountsListResponse response = unitApi.getAccounts(null, null, null);
        assert response.getData().size() > 0;
    }

    @Test
    public void GetAccountApiTest() throws ApiException {
        UnitAccountsListResponse response = unitApi.getAccounts(null, null, null);
        assert response.getData().size() > 0;
        response.getData().forEach(x -> {
            try {
                UnitAccountResponseWithIncluded account = unitApi.getAccountById(x.getId(), null);
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
        UnitAccountsListResponse response = unitApi.getAccounts(null, null, null);
        assert response.getData().size() > 0;
        response.getData().forEach(x -> {
            try {
                UnitAccountResponseWithIncluded account = unitApi.getAccountById(x.getId(), null);
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

                UnitAccountResponse res = unitApi.updateAccount(x.getId(), pa);
                res.getData().getId().equals(x.getId());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Account CreateDepositAccount() throws ApiException {
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


        ca.setData(new CreateAccountData(cda));
        return unitApi.createAccount().getData();
    }

    @Test
    public void CreateDepositAccountTest() throws ApiException {
        assert CreateDepositAccount().getType().equals("depositAccount");
    }
}
