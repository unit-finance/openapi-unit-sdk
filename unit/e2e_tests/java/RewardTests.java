package unit.java.sdk;

import static unit.java.sdk.TestHelpers.GenerateUnitApiClient;
import static unit.java.sdk.AccountTests.CreateDepositAccount;

import org.junit.Test;

import unit.java.sdk.api.UnitApi;
import unit.java.sdk.model.CreateRewardRelationships;
import unit.java.sdk.model.CreateRewardRequest;
import unit.java.sdk.model.CreateRewardRequestData;
import unit.java.sdk.model.CreateRewardRequestDataAttributes;
import unit.java.sdk.model.Customer;
import unit.java.sdk.model.DepositAccount;
import unit.java.sdk.model.ListPageParameters;
import unit.java.sdk.model.ReceivingAccountRelationship;
import unit.java.sdk.model.ReceivingAccountRelationshipData;
import unit.java.sdk.model.Reward;
import unit.java.sdk.model.UnitRewardResponse;
import unit.java.sdk.model.UnitRewardsListResponse;

public class RewardTests {
    UnitApi unitApi = GenerateUnitApiClient();

    @Test
    public void CreateRewardApiTest() throws ApiException {
        CreateRewardRequest req = new CreateRewardRequest();
        CreateRewardRequestData data = new CreateRewardRequestData();
        CreateRewardRequestDataAttributes attributes = new CreateRewardRequestDataAttributes();
        CreateRewardRelationships relationships = new CreateRewardRelationships();

        attributes.setAmount(10);
        attributes.setDescription("Test reward");

        ReceivingAccountRelationship relationship = new ReceivingAccountRelationship();
        ReceivingAccountRelationshipData relationshipData = new ReceivingAccountRelationshipData();
        DepositAccount account = CreateDepositAccount(unitApi, Customer.TypeEnum.INDIVIDUAL_CUSTOMER);

        relationshipData.setId(account.getId());
        relationshipData.setType(ReceivingAccountRelationshipData.TypeEnum.DEPOSIT_ACCOUNT);
        relationship.setData(relationshipData);
        relationships.setReceivingAccount(relationship);

        data.setAttributes(attributes);
        data.setRelationships(relationships);

        req.data(data);
        UnitRewardResponse res = unitApi.createReward(req);
        assert res.getData().getType().equals(Reward.TypeEnum.REWARD);
    }

    @Test
    public void GetRewardsApiTest() throws ApiException {
        ListPageParameters params = new ListPageParameters();
        params.setLimit(20);
        UnitRewardsListResponse res = unitApi.getRewardsList(params, null, null, null);
        res.getData().forEach(reward -> {
            assert reward.getType().equals(Reward.TypeEnum.REWARD);
            try {
                UnitRewardResponse getRewardRes = unitApi.getReward(reward.getId());
                assert getRewardRes.getData().getType().equals(Reward.TypeEnum.REWARD);
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
