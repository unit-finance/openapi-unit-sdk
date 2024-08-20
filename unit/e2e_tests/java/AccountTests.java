package unit.java.sdk;

import java.util.HashMap;

import org.junit.Test;

import static unit.java.sdk.CustomerTests.CreateIndividualCustomer;
import static unit.java.sdk.TestHelpers.GenerateUnitApiClient;
import unit.java.sdk.api.UnitApi;
import unit.java.sdk.model.Account;
import unit.java.sdk.model.CreateAccountRequest;
import unit.java.sdk.model.CreateAccountRequestData;
import unit.java.sdk.model.CreateDepositAccount;
import unit.java.sdk.model.CreateDepositAccountAttributes;
import unit.java.sdk.model.CreateDepositAccountRelationships;
import unit.java.sdk.model.CustomerRelationship;
import unit.java.sdk.model.CustomerRelationshipData;
import unit.java.sdk.model.DepositAccount;
import unit.java.sdk.model.IndividualCustomer;
import unit.java.sdk.model.UnitAccountResponse;
import unit.java.sdk.model.UnitAccountResponseWithIncluded;
import unit.java.sdk.model.UnitAccountsListResponse;
import unit.java.sdk.model.UpdateAccountRequest;
import unit.java.sdk.model.UpdateAccountRequestData;
import unit.java.sdk.model.UpdateDepositAccount;
import unit.java.sdk.model.UpdateDepositAccountAttributes;

public class AccountTests {
    UnitApi unitApi = GenerateUnitApiClient();

    @Test
    public void GetAccountListApiTest() throws ApiException {
        UnitAccountsListResponse response = unitApi.getAccountsList(null, null, null);
        assert !response.getData().isEmpty();
    }

    @Test
    public void GetAccountApiTest() throws ApiException {
        UnitAccountsListResponse response = unitApi.getAccountsList(null, null, null);
        assert !response.getData().isEmpty();
        response.getData().forEach(x -> {
            try {
                UnitAccountResponseWithIncluded account = unitApi.getAccount(x.getId(), null);
                assert account.getData().getId().equals(x.getId());
                assert account.getData().getType().toString().toLowerCase()
                        .equals(account.getData().getClass().getSimpleName().toLowerCase());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    @Test
    public void UpdateAccountApiTest() throws ApiException {
        UnitAccountsListResponse response = unitApi.getAccountsList(null, null, null);
        assert !response.getData().isEmpty();
        response.getData().forEach(x -> {
            try {
                UnitAccountResponseWithIncluded account = unitApi.getAccount(x.getId(), null);
                assert account.getData().getId().equals(x.getId());
                assert account.getData().getType().toString().toLowerCase()
                        .equals(account.getData().getClass().getSimpleName().toLowerCase());

                UpdateDepositAccountAttributes attributes = new UpdateDepositAccountAttributes();
                HashMap<String, String> tags = new HashMap<>();
                tags.put("test", "open-api-sdk");
                attributes.tags(tags);

                UpdateDepositAccount updateDepositAccount = new UpdateDepositAccount();
                updateDepositAccount.attributes(attributes);
                UpdateAccountRequest pa = new UpdateAccountRequest().data(new UpdateAccountRequestData(updateDepositAccount));

                UnitAccountResponse res = unitApi.updateAccount(x.getId(), pa);
                res.getData().getId().equals(x.getId());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static DepositAccount CreateDepositAccount(UnitApi unitApi) throws ApiException {
        IndividualCustomer customer = CreateIndividualCustomer(unitApi);

        CreateDepositAccount cda = new CreateDepositAccount();
        CreateDepositAccountAttributes attributes = new CreateDepositAccountAttributes();
        attributes.setDepositProduct("checking");

        CreateDepositAccountRelationships relationships = new CreateDepositAccountRelationships();
        CustomerRelationshipData customerRelationshipData = new CustomerRelationshipData();
        customerRelationshipData.setId(customer.getId());
        customerRelationshipData.setType(CustomerRelationshipData.TypeEnum.CUSTOMER);
        CustomerRelationship customerLinkageRelationship = new CustomerRelationship();
        customerLinkageRelationship.setData(customerRelationshipData);

        relationships.setCustomer(customerLinkageRelationship);

        cda.setAttributes(attributes);
        cda.setRelationships(relationships);

        CreateAccountRequest ca = new CreateAccountRequest();
        ca.setData(new CreateAccountRequestData(cda));

        return (DepositAccount) unitApi.createAccount(ca).getData();
    }

    @Test
    public void CreateDepositAccountTest() throws ApiException {
        assert CreateDepositAccount(unitApi).getType().equals(Account.TypeEnum.DEPOSITACCOUNT);
    }
}
