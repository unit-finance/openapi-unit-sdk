package unit.java.sdk;

import static unit.java.sdk.TestHelpers.GenerateUnitApiClient;

import java.time.LocalDate;

import static unit.java.sdk.AccountTests.CreateDepositAccount;
import static unit.java.sdk.AccountTests.CreateCreditAccount;
import static unit.java.sdk.CustomerTests.CreateIndividualCustomer;
import static unit.java.sdk.CustomerTests.CreateBusinessCustomer;

import org.junit.Test;
import unit.java.sdk.api.UnitApi;
import unit.java.sdk.model.AccountRelationship;
import unit.java.sdk.model.AccountRelationshipData;
import unit.java.sdk.model.Address;
import unit.java.sdk.model.Card;
import unit.java.sdk.model.CardLevelLimits;
import unit.java.sdk.model.CreateBusinessCardAttributes;
import unit.java.sdk.model.CreateBusinessCreditCardRequest;
import unit.java.sdk.model.CreateBusinessDebitCardRequest;
import unit.java.sdk.model.CreateBusinessVirtualCreditCardRequest;
import unit.java.sdk.model.CreateBusinessVirtualDebitCardRequest;
import unit.java.sdk.model.CreateBusinessVirtualDebitCardRequestAttributes;
import unit.java.sdk.model.CreateCardRelationships;
import unit.java.sdk.model.CreateCardRequest;
import unit.java.sdk.model.CreateCardRequestData;
import unit.java.sdk.model.CreateIndividualDebitCardRequest;
import unit.java.sdk.model.CreateIndividualDebitCardRequestAttributes;
import unit.java.sdk.model.CreateIndividualVirtualDebitCardRequest;
import unit.java.sdk.model.CreateIndividualVirtualDebitCardRequestAttributes;
import unit.java.sdk.model.CreditAccount;
import unit.java.sdk.model.DepositAccount;
import unit.java.sdk.model.FullName;
import unit.java.sdk.model.IndividualDebitCard;
import unit.java.sdk.model.ListPageParameters;
import unit.java.sdk.model.Phone;
import unit.java.sdk.model.ReplaceCardRequest;
import unit.java.sdk.model.ReplaceCardRequestData;
import unit.java.sdk.model.ReplaceCardRequestDataAttributes;
import unit.java.sdk.model.UnitCardLimitsResponse;
import unit.java.sdk.model.UnitCardLimitsResponseData;
import unit.java.sdk.model.UnitCardResponse;
import unit.java.sdk.model.UnitCardResponseWithIncluded;
import unit.java.sdk.model.UnitCardsListReponse;
import unit.java.sdk.model.UnitPinStatusResponse;
import unit.java.sdk.model.UnitPinStatusResponseData;

public class CardTests {
    UnitApi unitApi = GenerateUnitApiClient();

    static IndividualDebitCard CreateIndividualDebitCard(UnitApi unitApi, DepositAccount account) throws ApiException {
        if(account == null) account = CreateDepositAccount(unitApi, CreateIndividualCustomer(unitApi));
        CreateCardRequest req = new CreateCardRequest();
        CreateIndividualDebitCardRequest specificData = new CreateIndividualDebitCardRequest();
        
        CreateCardRelationships relationships = new CreateCardRelationships();
        AccountRelationship accountRelationship = new AccountRelationship();
        AccountRelationshipData relationshipData = new AccountRelationshipData();
        relationshipData.setType(AccountRelationshipData.TypeEnum.DEPOSIT_ACCOUNT);
        relationshipData.setId(account.getId());
        relationships.setAccount(accountRelationship);
        accountRelationship.setData(relationshipData);
        relationships.setAccount(accountRelationship);

        CreateIndividualDebitCardRequestAttributes attributes = new CreateIndividualDebitCardRequestAttributes();
        Address shippingAddress = new Address();
        shippingAddress.setStreet("5230 Newell Rd");
        shippingAddress.setState("CA");
        shippingAddress.setCountry("US");
        shippingAddress.setPostalCode("94303");
        shippingAddress.setCity("Palo Alto");
        attributes.setShippingAddress(shippingAddress);
        
        CardLevelLimits limits = new CardLevelLimits();
        limits.setDailyPurchase(1000L);
        limits.setDailyWithdrawal(500L);
        limits.setMonthlyPurchase(20000L);
        limits.setMonthlyWithdrawal(10000L);
        attributes.setLimits(limits);

        specificData.setType(CreateIndividualDebitCardRequest.TypeEnum.INDIVIDUAL_DEBIT_CARD);
        specificData.setAttributes(attributes);
        specificData.setRelationships(relationships);

        
        CreateCardRequestData reqData = new CreateCardRequestData(specificData);
        req.setData(reqData);
        UnitCardResponse res = unitApi.createCard(req);
        assert res.getData().getType().equals(Card.TypeEnum.INDIVIDUAL_DEBIT_CARD);

        return (IndividualDebitCard) res.getData();
    }

    static IndividualDebitCard CreateAndActivateIndividualDebitCard(UnitApi unitApi, DepositAccount account) throws ApiException {
        IndividualDebitCard card = CreateIndividualDebitCard(unitApi, account);
        unitApi.activateCardSimulation(card.getId());
        return card;
    }

    @Test
    public void CreateIndividualDebitCardApiTest() throws ApiException {
        CreateIndividualDebitCard(unitApi, null);
    }

    @Test
    public void CreateBusinessDebitCardApiTest() throws ApiException {
        DepositAccount account = CreateDepositAccount(unitApi, CreateBusinessCustomer(unitApi));
        CreateCardRequest req = new CreateCardRequest();
        CreateBusinessDebitCardRequest specificData = new CreateBusinessDebitCardRequest();
        
        CreateCardRelationships relationships = new CreateCardRelationships();
        AccountRelationship accountRelationship = new AccountRelationship();
        AccountRelationshipData relationshipData = new AccountRelationshipData();
        relationshipData.setType(AccountRelationshipData.TypeEnum.DEPOSIT_ACCOUNT);
        relationshipData.setId(account.getId());
        relationships.setAccount(accountRelationship);
        accountRelationship.setData(relationshipData);
        relationships.setAccount(accountRelationship);

        CreateBusinessCardAttributes attributes = new CreateBusinessCardAttributes();
        Address shippingAddress = new Address();
        shippingAddress.setStreet("5230 Newell Rd");
        shippingAddress.setState("CA");
        shippingAddress.setCountry("US");
        shippingAddress.setPostalCode("94303");
        shippingAddress.setCity("Palo Alto");
        attributes.setShippingAddress(shippingAddress);
        
        CardLevelLimits limits = new CardLevelLimits();
        limits.setDailyPurchase(1000L);
        limits.setDailyWithdrawal(500L);
        limits.setMonthlyPurchase(20000L);
        limits.setMonthlyWithdrawal(10000L);
        attributes.setLimits(limits);

        attributes.setAddress(shippingAddress);
        attributes.email("test@mail.com");

        FullName fullName = new FullName();
        fullName.setFirst("John");
        fullName.setLast("Doe");
        attributes.setFullName(fullName);
        Phone phone = new Phone();
        phone.setCountryCode("1");
        phone.setNumber("5555555555");
        attributes.setPhone(phone);
        attributes.setSsn("123456789");

        LocalDate dateOfBirth = LocalDate.of(2008, 8, 10);
        attributes.setDateOfBirth(dateOfBirth);

        specificData.setType(CreateBusinessDebitCardRequest.TypeEnum.BUSINESS_DEBIT_CARD);
        specificData.setAttributes(attributes);
        specificData.setRelationships(relationships);

        CreateCardRequestData reqData = new CreateCardRequestData(specificData);
        req.setData(reqData);
        UnitCardResponse res = unitApi.createCard(req);
        assert res.getData().getType().equals(Card.TypeEnum.BUSINESS_DEBIT_CARD);
    }

    @Test 
    public void CreateBusinessCreditCardApiTest() throws ApiException {
        CreditAccount account = CreateCreditAccount(unitApi);
        CreateCardRequest req = new CreateCardRequest();
        CreateBusinessCreditCardRequest specificData = new CreateBusinessCreditCardRequest();
        
        CreateCardRelationships relationships = new CreateCardRelationships();
        AccountRelationship accountRelationship = new AccountRelationship();
        AccountRelationshipData relationshipData = new AccountRelationshipData();
        relationshipData.setType(AccountRelationshipData.TypeEnum.DEPOSIT_ACCOUNT);
        relationshipData.setId(account.getId());
        relationships.setAccount(accountRelationship);
        accountRelationship.setData(relationshipData);
        relationships.setAccount(accountRelationship);

        CreateBusinessCardAttributes attributes = new CreateBusinessCardAttributes();
        Address shippingAddress = new Address();
        shippingAddress.setStreet("5230 Newell Rd");
        shippingAddress.setState("CA");
        shippingAddress.setCountry("US");
        shippingAddress.setPostalCode("94303");
        shippingAddress.setCity("Palo Alto");
        attributes.setShippingAddress(shippingAddress);
        
        CardLevelLimits limits = new CardLevelLimits();
        limits.setDailyPurchase(0L);
        limits.setDailyWithdrawal(0L);
        limits.setMonthlyPurchase(0L);
        limits.setMonthlyWithdrawal(0L);
        attributes.setLimits(limits);

        attributes.setAddress(shippingAddress);
        attributes.email("test@mail.com");

        FullName fullName = new FullName();
        fullName.setFirst("John");
        fullName.setLast("Doe");
        attributes.setFullName(fullName);
        Phone phone = new Phone();
        phone.setCountryCode("1");
        phone.setNumber("5555555555");
        attributes.setPhone(phone);
        attributes.setSsn("123456789");

        LocalDate dateOfBirth = LocalDate.of(2008, 8, 10);
        attributes.setDateOfBirth(dateOfBirth);

        specificData.setType(CreateBusinessCreditCardRequest.TypeEnum.BUSINESS_CREDIT_CARD);
        specificData.setAttributes(attributes);
        specificData.setRelationships(relationships);

        CreateCardRequestData reqData = new CreateCardRequestData(specificData);
        req.setData(reqData);
        UnitCardResponse res = unitApi.createCard(req);
        assert res.getData().getType().equals(Card.TypeEnum.BUSINESS_CREDIT_CARD);
    }

    @Test
    public void CreateIndividualVirtualDebitCardApiTest() throws ApiException {
        DepositAccount account = CreateDepositAccount(unitApi, CreateIndividualCustomer(unitApi));
        CreateCardRequest req = new CreateCardRequest();
        CreateIndividualVirtualDebitCardRequest specificData = new CreateIndividualVirtualDebitCardRequest();
        
        CreateCardRelationships relationships = new CreateCardRelationships();
        AccountRelationship accountRelationship = new AccountRelationship();
        AccountRelationshipData relationshipData = new AccountRelationshipData();
        relationshipData.setType(AccountRelationshipData.TypeEnum.DEPOSIT_ACCOUNT);
        relationshipData.setId(account.getId());
        relationships.setAccount(accountRelationship);
        accountRelationship.setData(relationshipData);
        relationships.setAccount(accountRelationship);

        CreateIndividualVirtualDebitCardRequestAttributes attributes = new CreateIndividualVirtualDebitCardRequestAttributes();
        
        CardLevelLimits limits = new CardLevelLimits();
        limits.setDailyPurchase(1000L);
        limits.setDailyWithdrawal(500L);
        limits.setMonthlyPurchase(20000L);
        limits.setMonthlyWithdrawal(10000L);
        attributes.setLimits(limits);

        specificData.setType(CreateIndividualVirtualDebitCardRequest.TypeEnum.INDIVIDUAL_VIRTUAL_DEBIT_CARD);
        specificData.setAttributes(attributes);
        specificData.setRelationships(relationships);

        
        CreateCardRequestData reqData = new CreateCardRequestData(specificData);
        req.setData(reqData);
        UnitCardResponse res = unitApi.createCard(req);
        assert res.getData().getType().equals(Card.TypeEnum.INDIVIDUAL_VIRTUAL_DEBIT_CARD);
    }

    @Test
    public void CreateBusinessVirtualDebitCard() throws ApiException {
        DepositAccount account = CreateDepositAccount(unitApi, CreateBusinessCustomer(unitApi));
        CreateCardRequest req = new CreateCardRequest();
        CreateBusinessVirtualDebitCardRequest specificData = new CreateBusinessVirtualDebitCardRequest();
        
        CreateCardRelationships relationships = new CreateCardRelationships();
        AccountRelationship accountRelationship = new AccountRelationship();
        AccountRelationshipData relationshipData = new AccountRelationshipData();
        relationshipData.setType(AccountRelationshipData.TypeEnum.DEPOSIT_ACCOUNT);
        relationshipData.setId(account.getId());
        relationships.setAccount(accountRelationship);
        accountRelationship.setData(relationshipData);
        relationships.setAccount(accountRelationship);

        CreateBusinessVirtualDebitCardRequestAttributes attributes = new CreateBusinessVirtualDebitCardRequestAttributes();
        
        CardLevelLimits limits = new CardLevelLimits();
        limits.setDailyPurchase(1000L);
        limits.setDailyWithdrawal(500L);
        limits.setMonthlyPurchase(20000L);
        limits.setMonthlyWithdrawal(10000L);
        attributes.setLimits(limits);

        attributes.email("test@mail.com");

        Address address = new Address();
        address.setStreet("5230 Newell Rd");
        address.setState("CA");
        address.setCountry("US");
        address.setPostalCode("94303");
        address.setCity("Palo Alto");
        attributes.setAddress(address);

        FullName fullName = new FullName();
        fullName.setFirst("John");
        fullName.setLast("Doe");
        attributes.setFullName(fullName);
        Phone phone = new Phone();
        phone.setCountryCode("1");
        phone.setNumber("5555555555");
        attributes.setPhone(phone);
        attributes.setSsn("123456789");

        LocalDate dateOfBirth = LocalDate.of(2008, 8, 10);
        attributes.setDateOfBirth(dateOfBirth);

        specificData.setType(CreateBusinessVirtualDebitCardRequest.TypeEnum.BUSINESS_VIRTUAL_DEBIT_CARD);
        specificData.setAttributes(attributes);
        specificData.setRelationships(relationships);

        CreateCardRequestData reqData = new CreateCardRequestData(specificData);
        req.setData(reqData);
        UnitCardResponse res = unitApi.createCard(req);
        assert res.getData().getType().equals(Card.TypeEnum.BUSINESS_VIRTUAL_DEBIT_CARD);
    }

    @Test public void CreateBusinessVirtualCreditCard() throws ApiException { 
        CreditAccount account = CreateCreditAccount(unitApi);
        CreateCardRequest req = new CreateCardRequest();
        CreateBusinessVirtualCreditCardRequest specificData = new CreateBusinessVirtualCreditCardRequest();
        
        CreateCardRelationships relationships = new CreateCardRelationships();
        AccountRelationship accountRelationship = new AccountRelationship();
        AccountRelationshipData relationshipData = new AccountRelationshipData();
        relationshipData.setType(AccountRelationshipData.TypeEnum.DEPOSIT_ACCOUNT);
        relationshipData.setId(account.getId());
        relationships.setAccount(accountRelationship);
        accountRelationship.setData(relationshipData);
        relationships.setAccount(accountRelationship);

        CreateBusinessVirtualDebitCardRequestAttributes attributes = new CreateBusinessVirtualDebitCardRequestAttributes();
        
        CardLevelLimits limits = new CardLevelLimits();
        limits.setDailyPurchase(0L);
        limits.setDailyWithdrawal(0L);
        limits.setMonthlyPurchase(0L);
        limits.setMonthlyWithdrawal(0L);
        attributes.setLimits(limits);

        attributes.email("test@mail.com");

        Address address = new Address();
        address.setStreet("5230 Newell Rd");
        address.setState("CA");
        address.setCountry("US");
        address.setPostalCode("94303");
        address.setCity("Palo Alto");

        attributes.setAddress(address);

        FullName fullName = new FullName();
        fullName.setFirst("John");
        fullName.setLast("Doe");
        attributes.setFullName(fullName);
        Phone phone = new Phone();
        phone.setCountryCode("1");
        phone.setNumber("5555555555");
        attributes.setPhone(phone);
        attributes.setSsn("123456789");

        LocalDate dateOfBirth = LocalDate.of(2008, 8, 10);
        attributes.setDateOfBirth(dateOfBirth);

        specificData.setType(CreateBusinessVirtualCreditCardRequest.TypeEnum.BUSINESS_VIRTUAL_CREDIT_CARD);
        specificData.setAttributes(attributes);
        specificData.setRelationships(relationships);

        CreateCardRequestData reqData = new CreateCardRequestData(specificData);
        req.setData(reqData);
        UnitCardResponse res = unitApi.createCard(req);
        assert res.getData().getType().equals(Card.TypeEnum.BUSINESS_VIRTUAL_CREDIT_CARD);
    }

    @Test
    public void GetPinStatusApiTest() throws ApiException {
        IndividualDebitCard card = CreateIndividualDebitCard(unitApi, null);
        unitApi.activateCardSimulation(card.getId());
        UnitPinStatusResponse res = unitApi.getCardPinStatus(card.getId());
        assert res.getData().getType().equals(UnitPinStatusResponseData.TypeEnum.PIN_STATUS);
    }

    @Test
    public void CloseCardApiTest() throws ApiException { 
        IndividualDebitCard card = CreateIndividualDebitCard(unitApi, null);
        UnitCardResponse res = unitApi.closeCard(card.getId());
        assert res.getData().getType().equals(Card.TypeEnum.INDIVIDUAL_DEBIT_CARD);
    }

    @Test
    public void FreezeCardApiTest() throws ApiException { 
        IndividualDebitCard card = CreateIndividualDebitCard(unitApi, null);
        UnitCardResponse res = unitApi.freezeCard(card.getId());
        assert res.getData().getType().equals(Card.TypeEnum.INDIVIDUAL_DEBIT_CARD);
    }

    @Test
    public void UnfreezeCardApiTest() throws ApiException { 
        IndividualDebitCard card = CreateIndividualDebitCard(unitApi, null);
        UnitCardResponse freezeRes = unitApi.freezeCard(card.getId());
        assert freezeRes.getData().getType().equals(Card.TypeEnum.INDIVIDUAL_DEBIT_CARD);
        UnitCardResponse unfreezeRes = unitApi.unfreezeCard(card.getId());
        assert unfreezeRes.getData().getType().equals(Card.TypeEnum.INDIVIDUAL_DEBIT_CARD);
    }

    @Test
    public void ReplaceCardApiTest() throws ApiException { 
        IndividualDebitCard card = CreateIndividualDebitCard(unitApi, null);
        ReplaceCardRequest req = new ReplaceCardRequest();
        ReplaceCardRequestData data = new ReplaceCardRequestData();
        ReplaceCardRequestDataAttributes attributes = new ReplaceCardRequestDataAttributes();
        Address shippingAddress = new Address();
        shippingAddress.setStreet("5230 Newell Rd");
        shippingAddress.setState("CA");
        shippingAddress.setCountry("US");
        shippingAddress.setPostalCode("94303");
        shippingAddress.setCity("Palo Alto");
        attributes.setShippingAddress(shippingAddress);
        data.setAttributes(attributes);
        data.setType(ReplaceCardRequestData.TypeEnum.REPLACE_CARD);
        req.setData(data);

        UnitCardResponse res = unitApi.replaceCard(card.getId(), req);
        assert res.getData().getType().equals(Card.TypeEnum.INDIVIDUAL_DEBIT_CARD);
    }

    @Test 
    public void GetCardsListApiTest() throws ApiException {
        ListPageParameters params = new ListPageParameters();
        params.setLimit(20);
        UnitCardsListReponse res = unitApi.getCardsList(params, null, null, null);
        assert !res.getData().isEmpty();
    }

    @Test 
    public void GetCardByIdApiTest() throws ApiException {
        IndividualDebitCard card = CreateIndividualDebitCard(unitApi, null);
        UnitCardResponseWithIncluded res = unitApi.getCard(card.getId(), null);
        assert res.getData().getType().equals(Card.TypeEnum.INDIVIDUAL_DEBIT_CARD);
    }

    @Test
    public void GetCardLimitsApiTest() throws ApiException {
        IndividualDebitCard card = CreateIndividualDebitCard(unitApi, null);
        UnitCardLimitsResponse res = unitApi.getCardLimits(card.getId());
        assert res.getData().getType().equals(UnitCardLimitsResponseData.TypeEnum.LIMITS);
    }

    @Test
    public void ActivateCardApiTest() throws ApiException {
        IndividualDebitCard card = CreateIndividualDebitCard(unitApi, null);
        UnitCardLimitsResponse res = unitApi.getCardLimits(card.getId());
        assert res.getData().getType().equals(UnitCardLimitsResponseData.TypeEnum.LIMITS);
    }
}