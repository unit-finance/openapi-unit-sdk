package unit.java.sdk;

import static unit.java.sdk.TestHelpers.GenerateUnitApiClient;

import java.util.HashMap;
import java.util.Map;

import static unit.java.sdk.AccountTests.CreateDepositAccount;
import static unit.java.sdk.CustomerTests.CreateIndividualCustomer;

import org.junit.Test;

import unit.java.sdk.api.UnitApi;
import unit.java.sdk.model.AccountRelationship;
import unit.java.sdk.model.AccountRelationshipData;
import unit.java.sdk.model.CheckDeposit;
import unit.java.sdk.model.CreateCheckDepositRelationships;
import unit.java.sdk.model.CreateCheckDepositRequest;
import unit.java.sdk.model.CreateCheckDepositRequestData;
import unit.java.sdk.model.CreateCheckDepositRequestDataAttributes;
import unit.java.sdk.model.DepositAccount;
import unit.java.sdk.model.ListPageParameters;
import unit.java.sdk.model.UnitCheckDepositResponse;
import unit.java.sdk.model.UnitListCheckDepositResponse;
import unit.java.sdk.model.UpdateCheckDeposit;
import unit.java.sdk.model.UpdateCheckDepositAttributes;
import unit.java.sdk.model.UpdateCheckDepositRequest;

public class CheckDepositTests {
    UnitApi unitApi = GenerateUnitApiClient();

    CheckDeposit CreateCheckDeposit() throws ApiException {
        CreateCheckDepositRequest req = new CreateCheckDepositRequest();
        CreateCheckDepositRequestData data = new CreateCheckDepositRequestData();
        CreateCheckDepositRequestDataAttributes attributes = new CreateCheckDepositRequestDataAttributes();
        attributes.setAmount(2000);
        attributes.setDescription("Test");

        DepositAccount account = CreateDepositAccount(unitApi, CreateIndividualCustomer(unitApi));
        CreateCheckDepositRelationships relationships = new CreateCheckDepositRelationships();
        AccountRelationship accountRelationship = new AccountRelationship();
        AccountRelationshipData accountRelationshipData = new AccountRelationshipData();
        accountRelationshipData.setType(AccountRelationshipData.TypeEnum.DEPOSIT_ACCOUNT);
        accountRelationshipData.setId(account.getId());
        accountRelationship.setData(accountRelationshipData);
        relationships.setAccount(accountRelationship);

        data.setAttributes(attributes);
        data.setRelationships(relationships);
        req.setData(data);

        UnitCheckDepositResponse res = unitApi.createCheckDeposit(req);
        CheckDeposit checkDeposit = res.getData();
        assert checkDeposit.getType().equals(CheckDeposit.TypeEnum.CHECK_DEPOSIT);
        return checkDeposit;
    }

    @Test
    public void CreateCheckDepositApiTest() throws ApiException {
        CreateCheckDeposit();
    }

    @Test
    public void UpdateCheckDepositApiTest() throws ApiException {
        CheckDeposit checkDeposit = CreateCheckDeposit();
        UpdateCheckDepositRequest req = new UpdateCheckDepositRequest();
        UpdateCheckDeposit data = new UpdateCheckDeposit();
        UpdateCheckDepositAttributes attributes = new UpdateCheckDepositAttributes();
        Map<String, String> tags = new HashMap<String, String>();
        tags.put("test", "value");
        attributes.setTags(tags);

        data.setAttributes(attributes);
        data.setType(UpdateCheckDeposit.TypeEnum.CHECK_DEPOSIT);
        req.setData(data);

        UnitCheckDepositResponse res = unitApi.updateCheckDeposit(checkDeposit.getId(), req);
        assert res.getData().getType().equals(CheckDeposit.TypeEnum.CHECK_DEPOSIT);
    }

    @Test
    public void GetCheckDepositsApiTest() throws ApiException {
        ListPageParameters params = new ListPageParameters();
        UnitListCheckDepositResponse res = unitApi.getCheckDepositsList(params, null, null, null);
        res.getData().forEach(checkDeposit -> {
            try {
                UnitCheckDepositResponse checkDepositResponse = unitApi.getCheckDeposit(checkDeposit.getId());
                assert checkDepositResponse.getData().getType().equals(CheckDeposit.TypeEnum.CHECK_DEPOSIT);
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }


}
