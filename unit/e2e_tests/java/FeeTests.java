package unit.java.sdk;

import static unit.java.sdk.TestHelpers.GenerateUnitApiClient;

import java.util.ArrayList;
import java.util.List;

import static unit.java.sdk.AccountTests.CreateCreditAccount;

import org.junit.Test;


import unit.java.sdk.api.UnitApi;
import unit.java.sdk.model.AccountRelationship;
import unit.java.sdk.model.AccountRelationshipData;
import unit.java.sdk.model.CreateFeeRelationships;
import unit.java.sdk.model.CreateFeeRequest;
import unit.java.sdk.model.CreateFeeRequestData;
import unit.java.sdk.model.CreateFeeRequestDataAttributes;
import unit.java.sdk.model.CreditAccount;
import unit.java.sdk.model.Fee;
import unit.java.sdk.model.FeeReversal;
import unit.java.sdk.model.GetTransactionsListFilterParameter;
import unit.java.sdk.model.ReverseFeeRequest;
import unit.java.sdk.model.ReverseFeeRequestData;
import unit.java.sdk.model.ReverseFeeRequestDataAttributes;
import unit.java.sdk.model.ReverseFeeRequestRelationships;
import unit.java.sdk.model.Transaction;
import unit.java.sdk.model.TransactionRelationship;
import unit.java.sdk.model.TransactionRelationshipData;
import unit.java.sdk.model.UnitFeeResponse;
import unit.java.sdk.model.UnitFeeReversalResponse;
import unit.java.sdk.model.UnitTransactionsListResponse;

public class FeeTests {
    UnitApi unitApi = GenerateUnitApiClient();


    Fee CreateFee(CreditAccount account) throws ApiException {
        CreateFeeRequest req = new CreateFeeRequest();
        CreateFeeRequestData data = new CreateFeeRequestData();
        CreateFeeRequestDataAttributes attributes = new CreateFeeRequestDataAttributes();
    
        attributes.setAmount(100);
        attributes.setDescription("Test");
        
        CreateFeeRelationships relationships = new CreateFeeRelationships();
        AccountRelationship relationship = new AccountRelationship();
        AccountRelationshipData relationshipData = new AccountRelationshipData();
        relationshipData.setId(account.getId());
        relationshipData.setType(AccountRelationshipData.TypeEnum.DEPOSIT_ACCOUNT);
        relationship.setData(relationshipData);
        relationships.setAccount(relationship);

        data.setAttributes(attributes);
        data.setRelationships(relationships);
        data.setType(CreateFeeRequestData.TypeEnum.FEE);
        req.setData(data);

        UnitFeeResponse res = unitApi.createFee(req);
        assert res.getData().getType().equals(Fee.TypeEnum.FEE);
        return res.getData();
    }

    @Test
    public void CreateFeeApiTest() throws ApiException {
        CreditAccount account = CreateCreditAccount(unitApi);
        CreateFee(account);
    }

    @Test
    public void ReverseFeeApiTest() throws ApiException {
        CreditAccount account = CreateCreditAccount(unitApi);
        CreateFee(account);
        
        GetTransactionsListFilterParameter filter = new GetTransactionsListFilterParameter();
        filter.setAccountId(account.getId());
        filter.setFromAmount(100);
        List<GetTransactionsListFilterParameter.DirectionEnum> directions = new ArrayList<GetTransactionsListFilterParameter.DirectionEnum>();
        directions.add(GetTransactionsListFilterParameter.DirectionEnum.CREDIT);
        filter.setDirection(null);
        UnitTransactionsListResponse transactionRes = unitApi.getTransactionsList(null, filter, null, null);
        assert !transactionRes.getData().isEmpty();

        Transaction transaction = transactionRes.getData().get(0);
        
        ReverseFeeRequest req = new ReverseFeeRequest();
        ReverseFeeRequestData data = new ReverseFeeRequestData();
        ReverseFeeRequestDataAttributes attributes = new ReverseFeeRequestDataAttributes();

        attributes.setDescription("Test");
        
        ReverseFeeRequestRelationships relationships = new ReverseFeeRequestRelationships();
        AccountRelationship accountRelationship = new AccountRelationship();
        AccountRelationshipData accountRelationshipData = new AccountRelationshipData();
        accountRelationshipData.setId(account.getId());
        accountRelationshipData.setType(AccountRelationshipData.TypeEnum.CREDIT_ACCOUNT);
        accountRelationship.setData(accountRelationshipData);
        
        TransactionRelationship transactionRelationship = new TransactionRelationship();
        TransactionRelationshipData transactionRelationshipData = new TransactionRelationshipData();
        transactionRelationshipData.setId(transaction.getId());
        transactionRelationshipData.setType(TransactionRelationshipData.TypeEnum.TRANSACTION);
        transactionRelationship.setData(transactionRelationshipData);
        relationships.setAccount(accountRelationship);
        relationships.setTransaction(transactionRelationship);
        
        data.setRelationships(relationships);
        data.setAttributes(attributes);
        req.setData(data);

        UnitFeeReversalResponse res = unitApi.reverseFee(req);
        assert res.getData().getType().equals(FeeReversal.TypeEnum.FEE_REVERSAL);
    }
}
