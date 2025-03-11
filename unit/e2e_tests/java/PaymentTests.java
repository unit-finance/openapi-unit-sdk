package unit.java.sdk;

import org.junit.Test;

import static unit.java.sdk.AccountTests.CreateDepositAccount;
import static unit.java.sdk.TestHelpers.CreatePaymentCounterparty;
import static unit.java.sdk.TestHelpers.CreateWirePaymentCounterparty;
import static unit.java.sdk.TestHelpers.GenerateUnitApiClient;

import java.util.List;

import static unit.java.sdk.CustomerTests.CreateIndividualCustomer;
import static unit.java.sdk.CounterpartyTests.CreateCounterparty;
import static unit.java.sdk.CardTests.CreateAndActivateIndividualDebitCard;
import unit.java.sdk.api.UnitApi;
import unit.java.sdk.model.AccountRelationship;
import unit.java.sdk.model.AccountRelationshipData;
import unit.java.sdk.model.AchPayment;
import unit.java.sdk.model.BookPayment;
import unit.java.sdk.model.CashDepositBarcode;
import unit.java.sdk.model.Counterparty;
import unit.java.sdk.model.CounterpartyAccountRelationship;
import unit.java.sdk.model.CounterpartyAccountRelationshipData;
import unit.java.sdk.model.CounterpartyRelationship;
import unit.java.sdk.model.CounterpartyRelationshipData;
import unit.java.sdk.model.CreateAchPayment;
import unit.java.sdk.model.CreateAchPaymentAttributes;
import unit.java.sdk.model.CreateAchPaymentCounterparty;
import unit.java.sdk.model.CreateAchPaymentCounterpartyAttributes;
import unit.java.sdk.model.CreateAchPaymentCounterpartyRelationships;
import unit.java.sdk.model.CreateAchPaymentPlaid;
import unit.java.sdk.model.CreateAchPaymentPlaidAttributes;
import unit.java.sdk.model.CreateAchPaymentPlaidRelationships;
import unit.java.sdk.model.CreateAchPaymentRelationships;
import unit.java.sdk.model.CreateAtmDepositSimulationRelationships;
import unit.java.sdk.model.CreateAtmDepositSimulationRequest;
import unit.java.sdk.model.CreateAtmDepositSimulationRequestData;
import unit.java.sdk.model.CreateAtmDepositSimulationRequestDataAttributes;
import unit.java.sdk.model.CreateBookPayment;
import unit.java.sdk.model.CreateBookPaymentAttributes;
import unit.java.sdk.model.CreateBookPaymentRelationships;
import unit.java.sdk.model.CreatePaymentRequest;
import unit.java.sdk.model.CreatePaymentRequestData;
import unit.java.sdk.model.CreateWirePayment;
import unit.java.sdk.model.CreateWirePaymentAttributes;
import unit.java.sdk.model.CreateWirePaymentRelationships;
import unit.java.sdk.model.CustomerRelationship;
import unit.java.sdk.model.CustomerRelationshipData;
import unit.java.sdk.model.DepositAccount;
import unit.java.sdk.model.GenerateBarcodeRequest;
import unit.java.sdk.model.GenerateBarcodeRequestData;
import unit.java.sdk.model.GenerateBarcodeRequestDataAttributes;
import unit.java.sdk.model.GenerateBarcodeRequestRelationships;
import unit.java.sdk.model.GetCashDepositStoreLocationsListFilterParameter;
import unit.java.sdk.model.IndividualCustomer;
import unit.java.sdk.model.IndividualDebitCard;
import unit.java.sdk.model.ListPageParameters;
import unit.java.sdk.model.Payment;
import unit.java.sdk.model.StoreLocation;
import unit.java.sdk.model.UnitCashDepositBarcodeResponse;
import unit.java.sdk.model.UnitPaymentResponse;
import unit.java.sdk.model.UnitPaymentResponseWithIncluded;
import unit.java.sdk.model.UnitPaymentsListResponse;
import unit.java.sdk.model.UnitStoreLocationsListResponse;
import unit.java.sdk.model.UpdateAchPayment;
import unit.java.sdk.model.UpdateAchPaymentAttributes;
import unit.java.sdk.model.UpdateBookPayment;
import unit.java.sdk.model.UpdateBookPaymentAttributes;
import unit.java.sdk.model.UpdatePaymentRequest;
import unit.java.sdk.model.UpdatePaymentRequestData;
import unit.java.sdk.model.GetCashDepositStoreLocationsListFilterParameter.ServiceTypeEnum;

public class PaymentTests {
    UnitApi unitApi = GenerateUnitApiClient();
    String plaidToken = System.getenv("test_plaid_counterparty_token");

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
            assert paymentResponse.getData().getType().toString().contains("Payment");
        }
    }

    BookPayment CreateBookPayment() throws ApiException {
        CreateBookPayment createBookPayment = new CreateBookPayment();
        CreateBookPaymentAttributes attributes = new CreateBookPaymentAttributes();
        attributes.setAmount(1000L);
        attributes.setDescription("Funding");
        createBookPayment.setAttributes(attributes);

        DepositAccount account1 = (DepositAccount) CreateDepositAccount(unitApi, CreateIndividualCustomer(unitApi));
        DepositAccount account2 = (DepositAccount) CreateDepositAccount(unitApi, CreateIndividualCustomer(unitApi));

        CreateBookPaymentRelationships relationships = new CreateBookPaymentRelationships();

        AccountRelationship accountRelationship = new AccountRelationship();
        AccountRelationshipData accountRelationshipData = new AccountRelationshipData();
        accountRelationshipData.setId(account1.getId());
        accountRelationshipData.setType(AccountRelationshipData.TypeEnum.DEPOSIT_ACCOUNT);
        accountRelationship.setData(accountRelationshipData);

        CounterpartyAccountRelationship counterpartyAccountRelationship = new CounterpartyAccountRelationship();
        CounterpartyAccountRelationshipData counterpartyAccountRelationshipData = new CounterpartyAccountRelationshipData();
        counterpartyAccountRelationshipData.setId(account2.getId());
        counterpartyAccountRelationshipData.setType(CounterpartyAccountRelationshipData.TypeEnum.DEPOSIT_ACCOUNT);
        counterpartyAccountRelationship.setData(counterpartyAccountRelationshipData);

        relationships.setAccount(accountRelationship);
        relationships.setCounterpartyAccount(counterpartyAccountRelationship);
        createBookPayment.setRelationships(relationships);

    
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.data(new CreatePaymentRequestData(createBookPayment));
        UnitPaymentResponse response = unitApi.createPayment(request);
        assert response.getData().getType().equals(BookPayment.TypeEnum.BOOK_PAYMENT);
        return (BookPayment) response.getData();
    }

    @Test
    public void CreateBookPaymentApiTest() throws ApiException {
        CreateBookPayment();
    }

    @Test
    public void UpdateBookPaymentApiTest() throws ApiException {
        BookPayment payment = CreateBookPayment();
        UpdatePaymentRequest req = new UpdatePaymentRequest();
        UpdateBookPayment updateBookPayment = new UpdateBookPayment();
        UpdateBookPaymentAttributes attributes = new UpdateBookPaymentAttributes();
        updateBookPayment.setAttributes(attributes);
        updateBookPayment.setType(UpdateBookPayment.TypeEnum.BOOK_PAYMENT);
        UpdatePaymentRequestData data = new UpdatePaymentRequestData(updateBookPayment);
        req.setData(data);

        String key = "by";
        String value = "Test Value";
        attributes.putTagsItem(key, value);
        UnitPaymentResponse res = unitApi.updatePayment(payment.getId(), req);
        BookPayment paymentRes = (BookPayment) res.getData();
        assert paymentRes.getType().equals(Payment.TypeEnum.BOOK_PAYMENT);
        assert paymentRes.getAttributes().getTags().containsKey(key);
        assert paymentRes.getAttributes().getTags().containsValue(value);
    }

    AchPayment CreateAchPayment() throws ApiException {
        CreateAchPayment createAchPayment = new CreateAchPayment();
        CreateAchPaymentAttributes attributes = new CreateAchPaymentAttributes();
        attributes.setAmount(1000L);
        attributes.setCounterparty(CreatePaymentCounterparty());
        attributes.setDirection(CreateAchPaymentAttributes.DirectionEnum.CREDIT);
        attributes.setDescription("Funding");
        createAchPayment.setAttributes(attributes);

        DepositAccount account = (DepositAccount) CreateDepositAccount(unitApi, CreateIndividualCustomer(unitApi));
        IndividualDebitCard card = CreateAndActivateIndividualDebitCard(unitApi, account);
        CreateAtmDepositSimulationRequest atmDepositSimulationRequest = new CreateAtmDepositSimulationRequest();
        CreateAtmDepositSimulationRequestData atmDepositSimulationRequestData = new CreateAtmDepositSimulationRequestData();
        CreateAtmDepositSimulationRequestDataAttributes atmDepositSimulationRequestDataAttributes = new CreateAtmDepositSimulationRequestDataAttributes();
        atmDepositSimulationRequestDataAttributes.setAmount(2000L);
        atmDepositSimulationRequestDataAttributes.setAtmLocation("Cupertino, CA, US");
        atmDepositSimulationRequestDataAttributes.setAtmName("HOME FED SAV BK");
        atmDepositSimulationRequestDataAttributes.setLast4Digits(card.getAttributes().getLast4Digits());

        CreateAtmDepositSimulationRelationships atmDepositRelationships = new CreateAtmDepositSimulationRelationships();
        AccountRelationship atmDepositAccountRelationship = new AccountRelationship();
        AccountRelationshipData atmDepositAccountRelationshipData = new AccountRelationshipData();
        atmDepositAccountRelationshipData.setId(account.getId());
        atmDepositAccountRelationshipData.setType(AccountRelationshipData.TypeEnum.DEPOSIT_ACCOUNT);
        atmDepositAccountRelationship.setData(atmDepositAccountRelationshipData);
        atmDepositRelationships.setAccount(atmDepositAccountRelationship);

        atmDepositSimulationRequestData.setAttributes(atmDepositSimulationRequestDataAttributes);
        atmDepositSimulationRequestData.setRelationships(atmDepositRelationships);

        atmDepositSimulationRequest.setData(atmDepositSimulationRequestData);
        unitApi.createAtmDepositSimulation(atmDepositSimulationRequest);

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
        assert response.getData().getType().equals(Payment.TypeEnum.ACH_PAYMENT);
        return (AchPayment) response.getData();
    }    
    
    @Test
    public void CreateAchPaymentApiTest() throws ApiException {
        CreateAchPayment();
    }

    @Test
    public void CreateAchPaymentCounterpartyApiTest() throws ApiException {
        CreateAchPaymentCounterparty createAchPayment = new CreateAchPaymentCounterparty();
        CreateAchPaymentCounterpartyAttributes attributes = new CreateAchPaymentCounterpartyAttributes();
        attributes.setAmount(1000L);
        attributes.setDirection(CreateAchPaymentCounterpartyAttributes.DirectionEnum.CREDIT);
        attributes.setDescription("Funding");
        createAchPayment.setAttributes(attributes);

        DepositAccount account = (DepositAccount) CreateDepositAccount(unitApi, CreateIndividualCustomer(unitApi));

        CreateAchPaymentCounterpartyRelationships relationships = new CreateAchPaymentCounterpartyRelationships();
        AccountRelationship accountRelationship = new AccountRelationship();
        AccountRelationshipData accountRelationshipData = new AccountRelationshipData();
        accountRelationshipData.setId(account.getId());
        accountRelationshipData.setType(AccountRelationshipData.TypeEnum.ACCOUNT);
        accountRelationship.setData(accountRelationshipData);
        relationships.setAccount(accountRelationship);
        
        Counterparty counterparty = CreateCounterparty(unitApi, null);
        CounterpartyRelationship counterpartyRelationship = new CounterpartyRelationship();
        CounterpartyRelationshipData counterpartyAccountRelationshipData = new CounterpartyRelationshipData();
        counterpartyAccountRelationshipData.setId(counterparty.getId());
        counterpartyAccountRelationshipData.setType(CounterpartyRelationshipData.TypeEnum.COUNTERPARTY);
        counterpartyRelationship.setData(counterpartyAccountRelationshipData);
        relationships.setCounterparty(counterpartyRelationship);
        
        
        createAchPayment.setRelationships(relationships);
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.data(new CreatePaymentRequestData(createAchPayment));
        UnitPaymentResponse response = unitApi.createPayment(request);
        assert response.getData().getType().equals(Payment.TypeEnum.ACH_PAYMENT);
    }

    AchPayment CreateAchPaymentPlaid() throws ApiException {
        CreateAchPaymentPlaid createAchPayment = new CreateAchPaymentPlaid();
        CreateAchPaymentPlaidAttributes attributes = new CreateAchPaymentPlaidAttributes();
        attributes.setAmount(1000L);
        attributes.setDirection(CreateAchPaymentPlaidAttributes.DirectionEnum.DEBIT);
        attributes.setPlaidProcessorToken(plaidToken);
        attributes.setDescription("Funding");
        createAchPayment.setAttributes(attributes);

        DepositAccount account = CreateDepositAccount(unitApi, CreateIndividualCustomer(unitApi));

        CreateAchPaymentPlaidRelationships relationships = new CreateAchPaymentPlaidRelationships();
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
        assert response.getData().getType().equals(Payment.TypeEnum.ACH_PAYMENT);
        return (AchPayment) response.getData();
    }

    @Test
    public void CreateAchPaymentPlaidApiTest() throws ApiException {
        CreateAchPaymentPlaid();
    }

    @Test
    public void UpdateAchPaymentApiTest() throws ApiException {
        AchPayment payment = CreateAchPayment();
        UpdatePaymentRequest req = new UpdatePaymentRequest();
        UpdateAchPayment updateBookPayment = new UpdateAchPayment();
        UpdateAchPaymentAttributes attributes = new UpdateAchPaymentAttributes();
        updateBookPayment.setAttributes(attributes);
        updateBookPayment.setType(UpdateAchPayment.TypeEnum.ACH_PAYMENT);
        UpdatePaymentRequestData data = new UpdatePaymentRequestData(updateBookPayment);
        req.setData(data);

        String key = "by";
        String value = "Test Value";
        attributes.putTagsItem(key, value);
        UnitPaymentResponse res = unitApi.updatePayment(payment.getId(), req);
        AchPayment paymentRes = (AchPayment) res.getData();
        assert paymentRes.getType().equals(Payment.TypeEnum.ACH_PAYMENT);
        assert paymentRes.getAttributes().getTags().containsKey(key);
        assert paymentRes.getAttributes().getTags().containsValue(value);
    }

    @Test
    public void CancelAchPaymentTest() throws ApiException {
        AchPayment payment = CreateAchPayment();

        UnitPaymentResponse res = unitApi.cancelPayment(payment.getId(), new Object());
        assert res.getData().getType().equals(Payment.TypeEnum.ACH_PAYMENT);
    }

    @Test
    public void CreateWirePaymentApiTest() throws ApiException {
        CreateWirePayment createWirePayment = new CreateWirePayment();
        CreateWirePaymentAttributes attributes = new CreateWirePaymentAttributes();
        createWirePayment.setType(CreateWirePayment.TypeEnum.WIRE_PAYMENT);
        attributes.setAmount(100L);
        attributes.setDirection(CreateWirePaymentAttributes.DirectionEnum.CREDIT);
        attributes.setCounterparty(CreateWirePaymentCounterparty());
        attributes.setDescription("Wire payment");
        createWirePayment.setAttributes(attributes);

        DepositAccount account = CreateDepositAccount(unitApi, CreateIndividualCustomer(unitApi));

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
        assert response.getData().getType().equals(Payment.TypeEnum.WIRE_PAYMENT);
    }
    
    public List<StoreLocation> GetCashDepositStoresList() throws ApiException {
        ListPageParameters params = new ListPageParameters();
        params.setLimit(20);
        GetCashDepositStoreLocationsListFilterParameter filter = new GetCashDepositStoreLocationsListFilterParameter();
        filter.setPostalCode("10001");
        filter.setServiceType(ServiceTypeEnum.BARCODE);
        UnitStoreLocationsListResponse res = unitApi.getCashDepositStoreLocationsList(params, filter);
        assert !res.getData().isEmpty();
        return res.getData();
    }

    @Test
    public void GetCashDepositStoresListApiTest() throws ApiException {
        GetCashDepositStoresList();
    }

    public CashDepositBarcode GenerateBarcode() throws ApiException {
        StoreLocation store = GetCashDepositStoresList().get(0);

        IndividualCustomer customer = CreateIndividualCustomer(unitApi);
        DepositAccount account = CreateDepositAccount(unitApi, customer);

        GenerateBarcodeRequest req = new GenerateBarcodeRequest();
        GenerateBarcodeRequestData data = new GenerateBarcodeRequestData();
        GenerateBarcodeRequestDataAttributes attributes = new GenerateBarcodeRequestDataAttributes();
        GenerateBarcodeRequestRelationships relationships = new GenerateBarcodeRequestRelationships();
        AccountRelationship accountRelationship = new AccountRelationship();
        AccountRelationshipData accountRelationshipData = new AccountRelationshipData();
        accountRelationshipData.setId(account.getId());
        accountRelationshipData.setType(AccountRelationshipData.TypeEnum.ACCOUNT);
        accountRelationship.setData(accountRelationshipData);
        relationships.setAccount(accountRelationship);
        CustomerRelationship customerRelationship = new CustomerRelationship();
        CustomerRelationshipData customerRelationshipData = new CustomerRelationshipData();
        customerRelationshipData.setId(customer.getId());
        customerRelationshipData.setType(CustomerRelationshipData.TypeEnum.CUSTOMER);
        customerRelationship.setData(customerRelationshipData);
        relationships.setCustomer(customerRelationship);

        attributes.setStoreId(store.getAttributes().getStoreId());

        data.setAttributes(attributes);
        data.setRelationships(relationships);
        req.setData(data);

        UnitCashDepositBarcodeResponse res = unitApi.generateBarcode(req);
        assert res.getData().getType().equals(CashDepositBarcode.TypeEnum.CASH_DEPOSIT_BARCODE);
        return res.getData();
    }

    @Test
    public void GenerateBarcodeApiTest() throws ApiException {
        GenerateBarcode();
    }

    @Test
    public void GetBarcodeImageApiTest() throws ApiException {
        CashDepositBarcode barcode = GenerateBarcode();
        unitApi.getBarcodeImage(barcode.getAttributes().getBarcodeNumber());
    }
}
