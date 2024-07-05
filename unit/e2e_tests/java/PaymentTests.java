package unit.java.sdk;

import org.junit.Test;

import static unit.java.sdk.AccountTests.CreateDepositAccount;
import static unit.java.sdk.TestHelpers.CreatePaymentCounterparty;
import static unit.java.sdk.TestHelpers.CreateWirePaymentCounterparty;
import static unit.java.sdk.TestHelpers.GenerateUnitApiClient;
import unit.java.sdk.api.UnitApi;
import unit.java.sdk.model.AccountRelationship;
import unit.java.sdk.model.AccountRelationshipData;
import unit.java.sdk.model.CounterpartyAccountRelationship;
import unit.java.sdk.model.CounterpartyAccountRelationshipData;
import unit.java.sdk.model.CreateAchPayment;
import unit.java.sdk.model.CreateAchPaymentAttributes;
import unit.java.sdk.model.CreateAchPaymentRelationships;
import unit.java.sdk.model.CreateBookPayment;
import unit.java.sdk.model.CreateBookPaymentAttributes;
import unit.java.sdk.model.CreateBookPaymentRelationships;
import unit.java.sdk.model.CreatePaymentRequest;
import unit.java.sdk.model.CreatePaymentRequestData;
import unit.java.sdk.model.CreateWirePayment;
import unit.java.sdk.model.CreateWirePaymentAttributes;
import unit.java.sdk.model.CreateWirePaymentRelationships;
import unit.java.sdk.model.DepositAccount;
import unit.java.sdk.model.Payment;
import unit.java.sdk.model.UnitPaymentResponse;
import unit.java.sdk.model.UnitPaymentResponseWithIncluded;
import unit.java.sdk.model.UnitPaymentsListResponse;

public class PaymentTests {
    UnitApi unitApi = GenerateUnitApiClient();

    @Test
    public void GetPaymentsListApiTest() throws ApiException {
        UnitPaymentsListResponse response = unitApi.getPaymentsList(null, null, null, null);
        assert !response.getData().isEmpty();
    }

    @Test
    public void GetPaymentsApiTest() throws ApiException {
        UnitPaymentsListResponse response = unitApi.getPaymentsList(null, null, null, null);
        assert !response.getData().isEmpty();

        for (Payment p: response.getData()) {
            UnitPaymentResponseWithIncluded paymentResponse = unitApi.getPayment(p.getId(), null);
            assert paymentResponse.getData().getType().contains("Payment");
        }
    }

    @Test
    public void CreateBookPaymentTest() throws ApiException {
        CreateBookPayment createBookPayment = new CreateBookPayment();
        CreateBookPaymentAttributes attributes = new CreateBookPaymentAttributes();
        attributes.setAmount(1000);
        attributes.setDescription("Funding");
        createBookPayment.setAttributes(attributes);

        DepositAccount account1 = (DepositAccount) CreateDepositAccount(unitApi);
        DepositAccount account2 = (DepositAccount) CreateDepositAccount(unitApi);

        CreateBookPaymentRelationships relationships = new CreateBookPaymentRelationships();

        AccountRelationship accountRelationship = new AccountRelationship();
        AccountRelationshipData accountRelationshipData = new AccountRelationshipData();
        accountRelationshipData.setId(account1.getId());
        accountRelationshipData.setType(AccountRelationshipData.TypeEnum.DEPOSITACCOUNT);
        accountRelationship.setData(accountRelationshipData);

        CounterpartyAccountRelationship counterpartyAccountRelationship = new CounterpartyAccountRelationship();
        CounterpartyAccountRelationshipData counterpartyAccountRelationshipData = new CounterpartyAccountRelationshipData();
        counterpartyAccountRelationshipData.setId(account2.getId());
        counterpartyAccountRelationshipData.setType(CounterpartyAccountRelationshipData.TypeEnum.DEPOSITACCOUNT);
        counterpartyAccountRelationship.setData(counterpartyAccountRelationshipData);

        relationships.setAccount(accountRelationship);
        relationships.setCounterpartyAccount(counterpartyAccountRelationship);
        createBookPayment.setRelationships(relationships);

    
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.data(new CreatePaymentRequestData(createBookPayment));
        UnitPaymentResponse response = unitApi.createPayment(request);
        assert response.getData().getType().equals("bookPayment");
    }
    @Test
    public void CreateAchPaymentTest() throws ApiException {
        CreateAchPayment createAchPayment = new CreateAchPayment();
        CreateAchPaymentAttributes attributes = new CreateAchPaymentAttributes();
        attributes.setAmount(1000);
        attributes.setCounterparty(CreatePaymentCounterparty());
        attributes.setDirection(CreateAchPaymentAttributes.DirectionEnum.CREDIT);
        attributes.setDescription("Funding");
        createAchPayment.setAttributes(attributes);

        DepositAccount account = (DepositAccount) CreateDepositAccount(unitApi);

        CreateAchPaymentRelationships relationships = new CreateAchPaymentRelationships();
        AccountRelationship accountRelationship = new AccountRelationship();
        AccountRelationshipData accountRelationshipData = new AccountRelationshipData();
        accountRelationshipData.setId(account.getId());
        accountRelationshipData.setType(AccountRelationshipData.TypeEnum.ACCOUNT);
        accountRelationship.setData(accountRelationshipData);
        relationships.setAccount(accountRelationship);
        createAchPayment.setRelationships(relationships);

        CreatePaymentRequest request = new CreatePaymentRequest();
        request.data(new CreatePaymentRequestData(createAchPayment));
        UnitPaymentResponse response = unitApi.createPayment(request);
        assert response.getData().getType().equals("achPayment");
    }

    @Test
    public void CreateWirePaymentTest() throws ApiException {
        CreateWirePayment createWirePayment = new CreateWirePayment();
        CreateWirePaymentAttributes attributes = new CreateWirePaymentAttributes();
        createWirePayment.setType("wirePayment");
        attributes.setAmount(100);
        attributes.setDirection(CreateWirePaymentAttributes.DirectionEnum.CREDIT);
        attributes.setCounterparty(CreateWirePaymentCounterparty());
        attributes.setDescription("Wire payment");
        createWirePayment.setAttributes(attributes);

        DepositAccount account = CreateDepositAccount(unitApi);

        CreateWirePaymentRelationships relationships = new CreateWirePaymentRelationships();
        AccountRelationship accountRelationship = new AccountRelationship();
        AccountRelationshipData accountRelationshipData = new AccountRelationshipData();
        accountRelationshipData.setId(account.getId());
        accountRelationshipData.setType(AccountRelationshipData.TypeEnum.ACCOUNT);
        accountRelationship.setData(accountRelationshipData);
        relationships.setAccount(accountRelationship);
        createWirePayment.setRelationships(relationships);

        CreatePaymentRequest request = new CreatePaymentRequest();
        request.data(new CreatePaymentRequestData(createWirePayment));
        UnitPaymentResponse response = unitApi.createPayment(request);
        assert response.getData().getType().equals("wirePayment");
    }
}
