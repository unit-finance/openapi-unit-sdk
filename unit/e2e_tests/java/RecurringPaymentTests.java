package unit.java.sdk;

import static unit.java.sdk.AccountTests.CreateDepositAccount;
import static unit.java.sdk.CounterpartyTests.CreateCounterparty;
import static unit.java.sdk.CustomerTests.CreateIndividualCustomer;
import static unit.java.sdk.CustomerTests.CreateBusinessCustomer;

import org.junit.Test;

import unit.java.sdk.api.UnitApi;
import unit.java.sdk.model.AccountRelationship;
import unit.java.sdk.model.AccountRelationshipData;
import unit.java.sdk.model.Counterparty;
import unit.java.sdk.model.CounterpartyAccountRelationship;
import unit.java.sdk.model.CounterpartyAccountRelationshipData;
import unit.java.sdk.model.CounterpartyRelationship;
import unit.java.sdk.model.CounterpartyRelationshipData;
import unit.java.sdk.model.CreatePaymentRelationshipsWithCounterparty;
import unit.java.sdk.model.CreatePaymentRelationshipsWithCounterpartyAccount;
import unit.java.sdk.model.CreateRecurringCreditAchPayment;
import unit.java.sdk.model.CreateRecurringCreditAchPaymentAttributes;
import unit.java.sdk.model.CreateRecurringCreditBookPayment;
import unit.java.sdk.model.CreateRecurringCreditBookPaymentAttributes;
import unit.java.sdk.model.CreateRecurringDebitAchPayment;
import unit.java.sdk.model.CreateRecurringDebitAchPaymentAttributes;
import unit.java.sdk.model.CreateRecurringPaymentRequest;
import unit.java.sdk.model.CreateRecurringPaymentRequestData;
import unit.java.sdk.model.Customer;
import unit.java.sdk.model.DepositAccount;
import unit.java.sdk.model.MonthlySchedule;
import unit.java.sdk.model.RecurringCreditAchPayment;
import unit.java.sdk.model.RecurringPayment;
import unit.java.sdk.model.Schedule;
import unit.java.sdk.model.UnitRecurringPaymentListResponse;
import unit.java.sdk.model.UnitRecurringPaymentResponse;


public class RecurringPaymentTests {
    UnitApi unitApi = TestHelpers.GenerateUnitApiClient();

    @Test
    public void GetRecurringPaymentListApiTest() throws ApiException {
        UnitRecurringPaymentListResponse response = unitApi.getRecurringPaymentsList(null, null, null);
        assert !response.getData().isEmpty();
    }

    @Test
    public void GetRecurringPaymentApiTest() throws ApiException {
        UnitRecurringPaymentListResponse response = unitApi.getRecurringPaymentsList(null, null, null);
        assert !response.getData().isEmpty();

        response.getData().forEach(x -> {
            try {
                UnitRecurringPaymentResponse payment = unitApi.getRecurringPayment(x.getId());
                assert payment.getData().getId().equals(x.getId());
                assert payment.getData().getType().toString().toLowerCase()
                        .equals(payment.getData().getClass().getSimpleName().toLowerCase());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }

    RecurringCreditAchPayment CreateRecurringCreditAchPayment() throws ApiException {
        DepositAccount account = CreateDepositAccount(unitApi, CreateIndividualCustomer(unitApi));
        Counterparty counterparty = CreateCounterparty(unitApi, CreateBusinessCustomer(unitApi));

        CreateRecurringPaymentRequest req = new CreateRecurringPaymentRequest();
        
        CreateRecurringCreditAchPayment createRecurringPayment = new CreateRecurringCreditAchPayment();
        CreateRecurringCreditAchPaymentAttributes attributes = new CreateRecurringCreditAchPaymentAttributes();

        attributes.amount(10);
        attributes.setDescription("Test");
        Schedule schedule = new Schedule();
        MonthlySchedule monthlySchedule = new MonthlySchedule();
        monthlySchedule.setDayOfMonth(16);
        monthlySchedule.setTotalNumberOfPayments(5);
        monthlySchedule.setInterval(MonthlySchedule.IntervalEnum.MONTHLY);
        schedule.setActualInstance(monthlySchedule);
        attributes.setSchedule(schedule);

        CreatePaymentRelationshipsWithCounterparty relationships = new CreatePaymentRelationshipsWithCounterparty();
        AccountRelationship accountRelationship = new AccountRelationship();
        AccountRelationshipData accountRelationshipData = new AccountRelationshipData();
        accountRelationshipData.setId(account.getId());
        accountRelationshipData.setType(AccountRelationshipData.TypeEnum.ACCOUNT);
        accountRelationship.setData(accountRelationshipData);

        CounterpartyRelationship counterpartyRelationship = new CounterpartyRelationship();
        CounterpartyRelationshipData counterpartyAccountRelationshipData = new CounterpartyRelationshipData();
        counterpartyAccountRelationshipData.setId(counterparty.getId());
        counterpartyAccountRelationshipData.setType(CounterpartyRelationshipData.TypeEnum.COUNTERPARTY);
        counterpartyRelationship.setData(counterpartyAccountRelationshipData);

        relationships.setAccount(accountRelationship);
        relationships.setCounterparty(counterpartyRelationship);

        createRecurringPayment.setAttributes(attributes);
        createRecurringPayment.setRelationships(relationships);
        createRecurringPayment.setType(CreateRecurringCreditAchPayment.TypeEnum.RECURRING_CREDIT_ACH_PAYMENT);
        CreateRecurringPaymentRequestData data = new CreateRecurringPaymentRequestData(createRecurringPayment);
        req.setData(data);

        UnitRecurringPaymentResponse res = unitApi.createRecurringPayment(req);
        RecurringCreditAchPayment recurringCreditAchPayment = (RecurringCreditAchPayment) res.getData();
        assert recurringCreditAchPayment.getType().equals(RecurringPayment.TypeEnum.RECURRING_CREDIT_ACH_PAYMENT);
        return recurringCreditAchPayment;
    }

    @Test
    public void CreateRecurringCreditAchPaymentApiTest() throws ApiException {
        CreateRecurringCreditAchPayment();
    }

    @Test
    public void CreateRecurringDebitAchPaymentApiTest() throws ApiException {
        DepositAccount account = CreateDepositAccount(unitApi, CreateIndividualCustomer(unitApi));
        Counterparty counterparty = CreateCounterparty(unitApi, CreateBusinessCustomer(unitApi));

        CreateRecurringPaymentRequest req = new CreateRecurringPaymentRequest();
        
        CreateRecurringDebitAchPayment createRecurringPayment = new CreateRecurringDebitAchPayment();
        CreateRecurringDebitAchPaymentAttributes attributes = new CreateRecurringDebitAchPaymentAttributes();

        attributes.amount(10);
        attributes.setDescription("Test");
        Schedule schedule = new Schedule();
        MonthlySchedule monthlySchedule = new MonthlySchedule();
        monthlySchedule.setDayOfMonth(16);
        monthlySchedule.setTotalNumberOfPayments(5);
        monthlySchedule.setInterval(MonthlySchedule.IntervalEnum.MONTHLY);
        schedule.setActualInstance(monthlySchedule);
        attributes.setSchedule(schedule);

        CreatePaymentRelationshipsWithCounterparty relationships = new CreatePaymentRelationshipsWithCounterparty();
        AccountRelationship accountRelationship = new AccountRelationship();
        AccountRelationshipData accountRelationshipData = new AccountRelationshipData();
        accountRelationshipData.setId(account.getId());
        accountRelationshipData.setType(AccountRelationshipData.TypeEnum.ACCOUNT);
        accountRelationship.setData(accountRelationshipData);

        CounterpartyRelationship counterpartyRelationship = new CounterpartyRelationship();
        CounterpartyRelationshipData counterpartyAccountRelationshipData = new CounterpartyRelationshipData();
        counterpartyAccountRelationshipData.setId(counterparty.getId());
        counterpartyAccountRelationshipData.setType(CounterpartyRelationshipData.TypeEnum.COUNTERPARTY);
        counterpartyRelationship.setData(counterpartyAccountRelationshipData);

        relationships.setAccount(accountRelationship);
        relationships.setCounterparty(counterpartyRelationship);

        createRecurringPayment.setAttributes(attributes);
        createRecurringPayment.setRelationships(relationships);
        createRecurringPayment.setType(CreateRecurringDebitAchPayment.TypeEnum.RECURRING_DEBIT_ACH_PAYMENT);
        CreateRecurringPaymentRequestData data = new CreateRecurringPaymentRequestData(createRecurringPayment);
        req.setData(data);

        UnitRecurringPaymentResponse res = unitApi.createRecurringPayment(req);
        assert res.getData().getType().equals(RecurringPayment.TypeEnum.RECURRING_DEBIT_ACH_PAYMENT);
    }

    @Test
    public void CreateRecurringCreditBookPaymentApiTest() throws ApiException {
        DepositAccount account = CreateDepositAccount(unitApi, CreateIndividualCustomer(unitApi));
        Customer counterpartyCustomer = CreateIndividualCustomer(unitApi);
        CreateCounterparty(unitApi, counterpartyCustomer);
        DepositAccount counterpartyAccount = CreateDepositAccount(unitApi, counterpartyCustomer);

        CreateRecurringPaymentRequest req = new CreateRecurringPaymentRequest();
        
        CreateRecurringCreditBookPayment createRecurringPayment = new CreateRecurringCreditBookPayment();
        CreateRecurringCreditBookPaymentAttributes attributes = new CreateRecurringCreditBookPaymentAttributes();

        attributes.amount(10);
        attributes.setDescription("Test");
        Schedule schedule = new Schedule();
        MonthlySchedule monthlySchedule = new MonthlySchedule();
        monthlySchedule.setDayOfMonth(16);
        monthlySchedule.setTotalNumberOfPayments(5);
        monthlySchedule.setInterval(MonthlySchedule.IntervalEnum.MONTHLY);
        schedule.setActualInstance(monthlySchedule);
        attributes.setSchedule(schedule);

        CreatePaymentRelationshipsWithCounterpartyAccount relationships = new CreatePaymentRelationshipsWithCounterpartyAccount();
        AccountRelationship accountRelationship = new AccountRelationship();
        AccountRelationshipData accountRelationshipData = new AccountRelationshipData();
        accountRelationshipData.setId(account.getId());
        accountRelationshipData.setType(AccountRelationshipData.TypeEnum.ACCOUNT);
        accountRelationship.setData(accountRelationshipData);

        CounterpartyAccountRelationship counterpartyRelationship = new CounterpartyAccountRelationship();
        CounterpartyAccountRelationshipData counterpartyAccountRelationshipData = new CounterpartyAccountRelationshipData();
        counterpartyAccountRelationshipData.setId(counterpartyAccount.getId());
        counterpartyAccountRelationshipData.setType(CounterpartyAccountRelationshipData.TypeEnum.ACCOUNT);
        counterpartyRelationship.setData(counterpartyAccountRelationshipData);

        relationships.setAccount(accountRelationship);
        relationships.setCounterpartyAccount(counterpartyRelationship);

        createRecurringPayment.setAttributes(attributes);
        createRecurringPayment.setRelationships(relationships);
        createRecurringPayment.setType(CreateRecurringCreditBookPayment.TypeEnum.RECURRING_CREDIT_BOOK_PAYMENT);
        CreateRecurringPaymentRequestData data = new CreateRecurringPaymentRequestData(createRecurringPayment);
        req.setData(data);

        UnitRecurringPaymentResponse res = unitApi.createRecurringPayment(req);
        assert res.getData().getType().equals(RecurringPayment.TypeEnum.RECURRING_CREDIT_BOOK_PAYMENT);
    }

    @Test 
    public void DisableAndEnableRecurringPaymentApiTest() throws ApiException {
        RecurringCreditAchPayment recurringCreditAchPayment = CreateRecurringCreditAchPayment();
        UnitRecurringPaymentResponse disableRes = unitApi.disableRecurringPayment(recurringCreditAchPayment.getId(), new Object());
        assert disableRes.getData().getType().equals(RecurringPayment.TypeEnum.RECURRING_CREDIT_ACH_PAYMENT);

        UnitRecurringPaymentResponse enableRes = unitApi.enableRecurringPayment(recurringCreditAchPayment.getId(), new Object());
        assert enableRes.getData().getType().equals(RecurringPayment.TypeEnum.RECURRING_CREDIT_ACH_PAYMENT);
    }
}
