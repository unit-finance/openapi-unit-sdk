package unit.java.sdk;

import static unit.java.sdk.CustomerTests.CreateBusinessCustomer;
import static unit.java.sdk.CustomerTests.CreateIndividualCustomer;

import unit.java.sdk.api.UnitApi;
import unit.java.sdk.model.Counterparty;
import unit.java.sdk.model.CreateAchCounterparty;
import unit.java.sdk.model.CreateAchCounterpartyAttributes;
import unit.java.sdk.model.CreateAchCounterpartyAttributes.AccountTypeEnum;
import unit.java.sdk.model.CreateCounterpartyRelationships;
import unit.java.sdk.model.CreateCounterpartyRequest;
import unit.java.sdk.model.CreateCounterpartyRequestData;
import unit.java.sdk.model.Customer;
import unit.java.sdk.model.CustomerRelationship;
import unit.java.sdk.model.CustomerRelationshipData;
import unit.java.sdk.model.UnitCounterpartyResponse;

public class CounterpartyTests {
    public static Counterparty CreateCounterparty(UnitApi unitApi, Customer customer) throws ApiException {
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
}
