package org.openapitools.client;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.CreateAPaymentApi;
import org.openapitools.client.api.GetListPaymentsApi;
import org.openapitools.client.api.GetPaymentApi;
import org.openapitools.client.model.*;
import static org.openapitools.client.AccountTests.CreateDepositAccount;
import static org.openapitools.client.TestHelpers.CreateCounterparty;

public class PaymentTests {
    @BeforeAll
    static void init() {
        String access_token = System.getenv("access_token");
        ApiClient cl = new ApiClient();
        cl.setBearerToken(access_token);
        Configuration.setDefaultApiClient(cl);
    }

    @Test
    public void GetPaymentsListApiTest() throws ApiException {
        GetListPaymentsApi api = new GetListPaymentsApi();

        UnitPaymentsListResponse response = api.execute(null, null, null, null);
        assert response.getData().size() > 0;
    }

    @Test
    public void GetPaymentsApiTest() throws ApiException {
        GetListPaymentsApi api = new GetListPaymentsApi();

        UnitPaymentsListResponse response = api.execute(null, null, null, null);
        assert response.getData().size() > 0;

        GetPaymentApi getPaymentApi = new GetPaymentApi();

        for (Payment p: response.getData()) {
            UnitPaymentResponseWithIncluded paymentResponse = getPaymentApi.execute(p.getId(), null);
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

        DepositAccount account1 = (DepositAccount) CreateDepositAccount();
        DepositAccount account2 = (DepositAccount) CreateDepositAccount();

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

        CreateAPaymentApi createApi = new CreateAPaymentApi();
        ExecuteRequest6 request = new ExecuteRequest6();
        request.setData(new CreatePayment(createBookPayment));
        UnitPaymentResponse response = createApi.execute(request);
        assert response.getData().getType().equals("bookPayment");
    }

    @Test
    public void CreateAchPaymentTest() throws ApiException {
        CreateAchPayment createAchPayment = new CreateAchPayment();
        CreateAchPaymentAttributes attributes = new CreateAchPaymentAttributes();
        attributes.setAmount(1000);
        attributes.setCounterparty(CreateCounterparty());
        attributes.setDirection(CreateAchPaymentAttributes.DirectionEnum.CREDIT);
        attributes.setDescription("Funding");
        createAchPayment.setAttributes(attributes);

        DepositAccount account = (DepositAccount) CreateDepositAccount();

        CreateAchPaymentRelationships relationships = new CreateAchPaymentRelationships();
        AccountRelationship accountRelationship = new AccountRelationship();
        AccountRelationshipData accountRelationshipData = new AccountRelationshipData();
        accountRelationshipData.setId(account.getId());
        accountRelationshipData.setType(AccountRelationshipData.TypeEnum.ACCOUNT);
        accountRelationship.setData(accountRelationshipData);
        relationships.setAccount(accountRelationship);
        createAchPayment.setRelationships(relationships);

        CreateAPaymentApi createApi = new CreateAPaymentApi();
        ExecuteRequest6 request = new ExecuteRequest6();
        request.setData(new CreatePayment(createAchPayment));
        UnitPaymentResponse response = createApi.execute(request);
        assert response.getData().getType().equals("achPayment");
    }
}
