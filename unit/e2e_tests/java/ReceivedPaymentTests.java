package unit.java.sdk;

import org.junit.Test;

import static unit.java.sdk.AccountTests.CreateDepositAccount;
import static unit.java.sdk.TestHelpers.GenerateUnitApiClient;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import unit.java.sdk.api.UnitApi;
import unit.java.sdk.model.AccountRelationship;
import unit.java.sdk.model.AccountRelationshipData;
import unit.java.sdk.model.CreateACHReceivedPaymentTransactionRelationships;
import unit.java.sdk.model.CreateACHReceivedPaymentTransactionRequest;
import unit.java.sdk.model.CreateACHReceivedPaymentTransactionRequestData;
import unit.java.sdk.model.CreateACHReceivedPaymentTransactionRequestDataAttributes;
import unit.java.sdk.model.CreateIncomingAchPaymentRequest;
import unit.java.sdk.model.CreateIncomingAchPaymentRequestData;
import unit.java.sdk.model.CreateIncomingAchPaymentRequestDataAttributes;
import unit.java.sdk.model.CreateIncomingAchPaymentTransactionRelationships;
import unit.java.sdk.model.DepositAccount;
import unit.java.sdk.model.ListPageParameters;
import unit.java.sdk.model.ReceivedPayment;
import unit.java.sdk.model.UnitReceivedPaymentListResponse;
import unit.java.sdk.model.UnitReceivedPaymentResponse;
import unit.java.sdk.model.UnitReceivedPaymentResponseWithIncluded;

public class ReceivedPaymentTests {
    UnitApi unitApi = GenerateUnitApiClient();

    ReceivedPayment CreateReceivedTransaction(Long amount) throws ApiException {
        DepositAccount account = CreateDepositAccount(unitApi, null);
        CreateACHReceivedPaymentTransactionRequest req = new CreateACHReceivedPaymentTransactionRequest();
        CreateACHReceivedPaymentTransactionRequestData data = new CreateACHReceivedPaymentTransactionRequestData();
        CreateACHReceivedPaymentTransactionRequestDataAttributes attributes = new CreateACHReceivedPaymentTransactionRequestDataAttributes();
        attributes.setAmount(amount);
        attributes.setDescription("paycheck simulation Sandbox");
        attributes.setCompanyName("UBER LTD");
        attributes.setCompletionDate(LocalDate.now());
        Date dt = new Date();
        Calendar c = Calendar.getInstance(); 
        c.setTime(dt); 
        c.add(Calendar.DATE, 1);
        attributes.setCompletionDate(c.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        attributes.setSecCode("PPD");

        CreateACHReceivedPaymentTransactionRelationships relationships = new CreateACHReceivedPaymentTransactionRelationships();
        AccountRelationship accountRelationship = new AccountRelationship();
        AccountRelationshipData accountRelationshipData = new AccountRelationshipData();
        accountRelationshipData.setId(account.getId());
        accountRelationshipData.setType(AccountRelationshipData.TypeEnum.DEPOSIT_ACCOUNT);
        accountRelationship.setData(accountRelationshipData);
        relationships.setAccount(accountRelationship);
        data.setRelationships(relationships);

        data.setAttributes(attributes);

        req.setData(data);

        UnitReceivedPaymentResponse res = unitApi.createACHReceivedPaymentTransactionSimulation(req);
        assert res.getData().getType().equals(ReceivedPayment.TypeEnum.ACH_RECEIVED_PAYMENT);
        return res.getData();
    }
    
    @Test
    public void GetReceivedPaymentListApiTest() throws ApiException {
        ListPageParameters params = new ListPageParameters();
        params.setLimit(20);
        UnitReceivedPaymentListResponse response = unitApi.getReceivedPaymentsList(params, null, null, null);
        assert !response.getData().isEmpty();
    }

    @Test
    public void GetReceivedPaymentListWithIncludedApiTest() throws ApiException {
        ListPageParameters params = new ListPageParameters();
        params.setLimit(20);
        UnitReceivedPaymentListResponse response = unitApi.getReceivedPaymentsList(params, null, "customer", null);
        assert !response.getData().isEmpty();
    }

    @Test
    public void GetReceivedPaymentApiTest() throws ApiException {
        ListPageParameters params = new ListPageParameters();
        params.setLimit(20);
        UnitReceivedPaymentListResponse response = unitApi.getReceivedPaymentsList(null, null, null, null);
        assert !response.getData().isEmpty();

        response.getData().forEach(x -> {
            try {
                UnitReceivedPaymentResponseWithIncluded payment = unitApi.getReceivedPayment(x.getId(), null);
                assert payment.getData().getId().equals(x.getId());
                assert payment.getData().getAttributes().getAmount().equals(x.getAttributes().getAmount());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void AdvanceReceivedPaymentApiTest() throws ApiException {
        ReceivedPayment payment = CreateReceivedTransaction(1000L);
        UnitReceivedPaymentResponse res = unitApi.advanceReceivedPayment(payment.getId());
        assert res.getData().getType().equals(ReceivedPayment.TypeEnum.ACH_RECEIVED_PAYMENT);
    }

    @Test
    public void ReprocessReceivedPaymentApiTest() throws ApiException {
        DepositAccount account = CreateDepositAccount(unitApi, null);
        CreateIncomingAchPaymentRequest req = new CreateIncomingAchPaymentRequest();
        CreateIncomingAchPaymentRequestData data = new CreateIncomingAchPaymentRequestData();
        CreateIncomingAchPaymentRequestDataAttributes attributes = new CreateIncomingAchPaymentRequestDataAttributes();
        attributes.setAmount(40000000L);
        attributes.setCompanyName("Easy");
        attributes.setSettlementDate(LocalDate.now());
        attributes.setDirection(CreateIncomingAchPaymentRequestDataAttributes.DirectionEnum.DEBIT);
        attributes.setSecCode("WEB");
        attributes.setReceivingEntityName("Easy Company");

        CreateIncomingAchPaymentTransactionRelationships relationships = new CreateIncomingAchPaymentTransactionRelationships();
        AccountRelationship accountRelationship = new AccountRelationship();
        AccountRelationshipData accountRelationshipData = new AccountRelationshipData();
        accountRelationshipData.setId(account.getId());
        accountRelationshipData.setType(AccountRelationshipData.TypeEnum.DEPOSIT_ACCOUNT);
        accountRelationship.setData(accountRelationshipData);
        relationships.setAccount(accountRelationship);
        data.setRelationships(relationships);

        data.setAttributes(attributes);

        req.setData(data);

        UnitReceivedPaymentResponse res = unitApi.createIncomingAchPaymentSimulation(req);
        ReceivedPayment payment = res.getData();
        assert payment.getType().equals(ReceivedPayment.TypeEnum.ACH_RECEIVED_PAYMENT);
        
        UnitReceivedPaymentResponse reprocessRes = unitApi.reprocessReceivedPayment(payment.getId());
        assert reprocessRes.getData().getType().equals(ReceivedPayment.TypeEnum.ACH_RECEIVED_PAYMENT);
    }
}
