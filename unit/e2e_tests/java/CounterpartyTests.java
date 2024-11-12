package unit.java.sdk;

import static unit.java.sdk.CustomerTests.CreateIndividualCustomer;
import static unit.java.sdk.TestHelpers.GenerateUnitApiClient;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import unit.java.sdk.api.UnitApi;
import unit.java.sdk.model.Counterparty;
import unit.java.sdk.model.CounterpartyBalance;
import unit.java.sdk.model.CreateAchCounterparty;
import unit.java.sdk.model.CreateAchCounterpartyAttributes;
import unit.java.sdk.model.CreateAchCounterpartyAttributes.AccountTypeEnum;
import unit.java.sdk.model.CreatePlaidCounterpartyAttributes.PermissionsEnum;
import unit.java.sdk.model.CreateCounterpartyRelationships;
import unit.java.sdk.model.CreateCounterpartyRequest;
import unit.java.sdk.model.CreateCounterpartyRequestData;
import unit.java.sdk.model.CreatePlaidCounterparty;
import unit.java.sdk.model.CreatePlaidCounterpartyAttributes;
import unit.java.sdk.model.Customer;
import unit.java.sdk.model.CustomerRelationship;
import unit.java.sdk.model.CustomerRelationshipData;
import unit.java.sdk.model.IndividualCustomer;
import unit.java.sdk.model.ListPageParameters;
import unit.java.sdk.model.UnitCounterpartiesListResponse;
import unit.java.sdk.model.UnitCounterpartyBalanceResponse;
import unit.java.sdk.model.UnitCounterpartyResponse;
import unit.java.sdk.model.UpdateCounterparty;
import unit.java.sdk.model.UpdateCounterpartyAttributes;
import unit.java.sdk.model.UpdateCounterpartyRequest;
import unit.java.sdk.model.UpdateCounterpartyRequestData;

public class CounterpartyTests {
    UnitApi unitApi = GenerateUnitApiClient();
    String plaidToken = System.getenv("test_plaid_counterparty_token");
    
    public static Counterparty CreateCounterparty(UnitApi unitApi, Customer customer) throws ApiException {
        if(customer == null) customer = CreateIndividualCustomer(unitApi);
        CreateCounterpartyRequest req = new CreateCounterpartyRequest();
        CreateAchCounterparty createCounterparty = new CreateAchCounterparty();
        CreateAchCounterpartyAttributes attributes = new CreateAchCounterpartyAttributes();
        CreateCounterpartyRelationships relationships = new CreateCounterpartyRelationships();
        CustomerRelationship customerRelationship = new CustomerRelationship();
        CustomerRelationshipData customerRelationshipData = new CustomerRelationshipData();
        
        customerRelationshipData.setId(customer.getId());
        customerRelationshipData.setType(CustomerRelationshipData.TypeEnum.CUSTOMER);
        customerRelationship.setData(customerRelationshipData);
        relationships.setCustomer(customerRelationship);

        attributes.setName("John Doe");
        attributes.setRoutingNumber("011000133");
        attributes.setAccountNumber("123");
        attributes.setAccountType(AccountTypeEnum.CHECKING);
        attributes.setType(CreateAchCounterpartyAttributes.TypeEnum.PERSON);

        createCounterparty.setType(CreateAchCounterparty.TypeEnum.ACH_COUNTERPARTY);
        createCounterparty.setAttributes(attributes);
        createCounterparty.setRelationships(relationships);
       CreateCounterpartyRequestData data = new CreateCounterpartyRequestData(createCounterparty);
       req.setData(data);

       UnitCounterpartyResponse res = unitApi.createCounterparty(req);
       Counterparty counterparty = res.getData();
       assert counterparty.getType().equals(Counterparty.TypeEnum.ACH_COUNTERPARTY);
       return counterparty;
    }

    @Test
    public void CreateCounterpartyApiTest() throws ApiException {
        CreateCounterparty(unitApi, null);
    }

    Counterparty CreateCounterpartyWithPlaidToken() throws ApiException {
        IndividualCustomer customer = CreateIndividualCustomer(unitApi); 
        CreateCounterpartyRequest req = new CreateCounterpartyRequest();
        CreatePlaidCounterparty createCounterparty = new CreatePlaidCounterparty();
        CreatePlaidCounterpartyAttributes attributes = new CreatePlaidCounterpartyAttributes();
        CreateCounterpartyRelationships relationships = new CreateCounterpartyRelationships();
        CustomerRelationship customerRelationship = new CustomerRelationship();
        CustomerRelationshipData customerRelationshipData = new CustomerRelationshipData();
        
        customerRelationshipData.setId(customer.getId());
        customerRelationshipData.setType(CustomerRelationshipData.TypeEnum.CUSTOMER);
        customerRelationship.setData(customerRelationshipData);
        relationships.setCustomer(customerRelationship);

        attributes.setName("John Doe");
        attributes.setType(CreatePlaidCounterpartyAttributes.TypeEnum.PERSON);
        attributes.setPlaidProcessorToken(plaidToken);
        attributes.setPermissions(PermissionsEnum.DEBIT_ONLY);

        createCounterparty.setType(CreatePlaidCounterparty.TypeEnum.ACH_COUNTERPARTY);
        createCounterparty.setAttributes(attributes);
        createCounterparty.setRelationships(relationships);
       CreateCounterpartyRequestData data = new CreateCounterpartyRequestData(createCounterparty);
       req.setData(data);

       UnitCounterpartyResponse res = unitApi.createCounterparty(req);
       Counterparty counterparty = res.getData();
       assert counterparty.getType().equals(Counterparty.TypeEnum.ACH_COUNTERPARTY);
       return counterparty;
    }

    @Test
    public void CreateCounterpartyWithPlaidTokenApiTest() throws ApiException {
        CreateCounterpartyWithPlaidToken();
    }

    @Test
    public void DeleteCounterpartyApiTest() throws ApiException {
        Counterparty counterparty = CreateCounterparty(unitApi, null);
        unitApi.deleteCounterparty(counterparty.getId());
    }

    @Test
    public void GetCounterpartiesApiTest() throws ApiException {
        ListPageParameters params = new ListPageParameters();
        params.setLimit(20);
        UnitCounterpartiesListResponse res = unitApi.getCounterpartiesList(params, null, null);
        res.getData().forEach(counterparty -> {
            try {
                UnitCounterpartyResponse getRes = unitApi.getCounterparty(counterparty.getId());
                assert getRes.getData().getType().equals(Counterparty.TypeEnum.ACH_COUNTERPARTY);
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void GetCounterpartyBalanceApiTest() throws ApiException {
        Counterparty counterparty = CreateCounterpartyWithPlaidToken();
        UnitCounterpartyBalanceResponse res = unitApi.getCounterpartyBalance(counterparty.getId());
        assert res.getData().getType().equals(CounterpartyBalance.TypeEnum.COUNTERPARTY_BALANCE);
    }

    @Test
    public void UpdateCounterpartyApiTest() throws ApiException {
        Counterparty counterparty = CreateCounterparty(unitApi, null);
        UpdateCounterpartyRequest req = new UpdateCounterpartyRequest();
        UpdateCounterparty updateCounterparty = new UpdateCounterparty();
        UpdateCounterpartyAttributes attributes = new UpdateCounterpartyAttributes();
        Map<String, String> tags = new HashMap<String, String>();
        String key = "TestKey";
        String value = "TestValue";
        tags.put(key, value);
        attributes.setTags(tags);
        updateCounterparty.setType(UpdateCounterparty.TypeEnum.COUNTERPARTY);
        updateCounterparty.setAttributes(attributes);

        UpdateCounterpartyRequestData data = new UpdateCounterpartyRequestData(updateCounterparty);

        req.setData(data);
        UnitCounterpartyResponse res = unitApi.updateCounterparty(counterparty.getId(), req);
        Counterparty resCounterparty = res.getData();
        assert resCounterparty.getType().equals(Counterparty.TypeEnum.ACH_COUNTERPARTY);
        assert resCounterparty.getAttributes().getTags().get(key).equals(value);
    }
}
