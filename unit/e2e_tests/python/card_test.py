import os
import unittest

from helpers.helpers import create_api_client, create_individual_application_request, \
    create_business_application_request, create_address
from dist.pythonsdk.openapi_client.api.unit_api import UnitApi
from dist.pythonsdk.openapi_client.models import Address, CardLevelLimits, \
    CreateCardRelationships, Relationship, RelationshipData, CreateDepositAccountAttributes, \
    CreateDepositAccountRelationships, CreateDepositAccount, CreateIndividualDebitCardRequestAttributes, \
CreateIndividualDebitCardRequest, CreateBusinessDebitCardRequestAttributes, \
CreateBusinessDebitCardRequest, FullName, Phone, CreateBusinessVirtualDebitCardRequestAttributes,\
CreateBusinessVirtualDebitCardRequest, CreateIndividualVirtualDebitCardRequestAttributes, CreateIndividualVirtualDebitCardRequest, \
    UpdateIndividualDebitCardAttributes, UpdateIndividualDebitCard

card_types = ["individualDebitCard", "businessDebitCard", "individualVirtualDebitCard", "businessVirtualDebitCard",
              "businessCreditCard", "businessVirtualCreditCard"]

headers = {
            "content-type": "application/vnd.api+json",
            "authorization": f"Bearer {os.environ.get('TOKEN')}",
            "user-agent": "unit-pythonsdk"
        }

address = Address("5230 Newell Rd", city="Palo Alto", state="CA", postal_code="94303")
limits = CardLevelLimits(50000, 50000, 50000, 70000)


class TestCardApi(unittest.TestCase):
    """CardApi unit test stubs"""

    def setUp(self):
        self.api_client = create_api_client()

    def tearDown(self):
        pass

    def create_card_relationships(self, account_id: str, _type="depositAccount"):
        return CreateCardRelationships(Relationship(RelationshipData(account_id, _type)))


    def card_list(self):
        return UnitApi.get_cards_list(self.api_client).execute().data

    def test_card_list(self):
        res = self.card_list()
        for card in res:
            assert card.type in card_types

    def test_card_list_and_get(self):
        res = UnitApi.get_cards_list(self.api_client).execute()

        assert res.included is None

        for card in res.data:
            assert card.type in card_types
            response_card = UnitApi.get_card(self.api_client).execute(card.id).data
            assert card.id == response_card.id
            assert card.type == response_card.type

    def test_card_list_with_filters(self):
        res = UnitApi.get_cards_list(self.api_client).execute(filter_status=["Active"], include="customer")

        assert res.included is not None

        for card in res.data:
            assert card.type in card_types
            assert card.attributes.status == "Active"
            response_card = UnitApi.get_card(self.api_client).execute(card.id).data
            assert card.id == response_card.id
            assert card.type == response_card.type
            assert card.attributes.status == response_card.attributes.status
            assert card.attributes.created_at == response_card.attributes.created_at

    def create_individual_customer(self):
        app = UnitApi.create_application(self.api_client).execute(create_individual_application_request()).data
        return app.relationships.customer.data.id

    def create_business_customer(self):
        app = UnitApi.create_application(self.api_client).execute(create_business_application_request()).data
        return app.relationships.customer.data.id

    def create_deposit_account(self):
        customer_id = self.create_individual_customer()

        attributes = CreateDepositAccountAttributes("checking", {"purpose": "sdk-test"})
        relationships = CreateDepositAccountRelationships(customer={"data": {"type": "customer",
                                                                    "id": customer_id}})
        req = CreateDepositAccount(attributes=attributes, relationships=relationships)

        response = UnitApi.create_account(self.api_client).execute({"data": req})
        return response.data

    def create_deposit_account_for_business(self):
        customer_id = self.create_business_customer()

        attributes = CreateDepositAccountAttributes("checking", {"purpose": "sdk-test-business-account"})
        relationships = CreateDepositAccountRelationships(customer={"data": {"type": "customer",
                                                                             "id": customer_id}})
        req = CreateDepositAccount(attributes=attributes, relationships=relationships)

        response = UnitApi.create_account(self.api_client).execute({"data": req})
        return response.data

    def create_individual_debit_card(self):
        account_id = self.create_deposit_account().id
        attributes = CreateIndividualDebitCardRequestAttributes(address, limits=limits)
        req = CreateIndividualDebitCardRequest(attributes=attributes, relationships=self.create_card_relationships(account_id))

        card = UnitApi.create_card(self.api_client).execute({"data": req}).data
        return card

    def test_create_individual_debit_card(self):
        card = self.create_individual_debit_card()
        assert card.type == "individualDebitCard"

    def create_business_debit_card(self):
        account_id = self.create_deposit_account_for_business().id

        attributes = CreateBusinessDebitCardRequestAttributes(address, address, FullName(first="Richard", last="Hendricks"),
                                                       Phone(country_code="1", number="5555555555"), "richard@piedpiper.com", "2001-08-10",
                                                       limits=limits)
        req = CreateBusinessDebitCardRequest(attributes=attributes, relationships=self.create_card_relationships(account_id))

        card = UnitApi.create_card(self.api_client).execute({"data": req}).data
        return card

    def test_create_business_debit_card(self):
        card = self.create_business_debit_card()
        assert card.type == "businessDebitCard"

    def create_business_virtual_debit_card(self):
        account_id = self.create_deposit_account_for_business().id

        attributes = CreateBusinessVirtualDebitCardRequestAttributes(address, FullName("Richard", "Hendricks"),
                                                              Phone("1", "5555555555"), "richard@piedpiper.com",
                                                              "2001-08-10", limits=limits)
        req = CreateBusinessVirtualDebitCardRequest(attributes=attributes, relationships=self.create_card_relationships(account_id))

        card = UnitApi.create_card(self.api_client).execute({"data": req}).data
        return card

    def test_create_business_virtual_debit_card(self):
        card = self.create_business_virtual_debit_card()
        assert card.type == "businessVirtualDebitCard"

    def create_individual_virtual_debit_card(self):
        account_id = self.create_deposit_account().id

        attributes = CreateIndividualVirtualDebitCardRequestAttributes(limits=limits)
        req = CreateIndividualVirtualDebitCardRequest(attributes=attributes,
                                               relationships=self.create_card_relationships(account_id))

        card = UnitApi.create_card(self.api_client).execute({"data": req}).data
        return card

    def test_create_individual_virtual_debit_card(self):
        card = self.create_individual_virtual_debit_card()
        assert card.type == "individualVirtualDebitCard"

    def test_freeze_and_unfreeze_card(self):
        card = self.create_individual_debit_card()
        assert card.type == "individualDebitCard"
        response = UnitApi.freeze_card(self.api_client).execute(card.id).data
        assert response.attributes.status == "Frozen"
        response = UnitApi.unfreeze_card(self.api_client).execute(card.id).data
        assert response.attributes.status != "Frozen"

    def test_close_card(self):
        card = self.create_individual_debit_card()
        response = UnitApi.close_card(self.api_client).execute(card.id).data
        assert response.attributes.status == "ClosedByCustomer"

    # def test_replace_card(self):
    #     card = self.create_individual_debit_card()
    #     _address = Address("1616 Pennsylvania Avenue Northwest", "Washington", "CA", "21500", "US")
    #     response = client.cards.replace(card_id, _address)
    #     assert response.data.type == "individualDebitCard"
    #

    def test_get_debit_card(self):
        card = self.create_individual_debit_card()
        response = UnitApi.get_card(self.api_client).execute(card.id)
        assert response.data.type in card_types

    def test_update_individual_card(self):
        card = self.create_individual_debit_card()
        _address = create_address("1818 Pennsylvania Avenue Northwest", "Washington", "CA", "21500", "US")
        _attributes = UpdateIndividualDebitCardAttributes(shipping_address=_address, tags={"test": "updated"})
        request = UpdateIndividualDebitCard(type="individualDebitCard",attributes=_attributes)
        response = UnitApi.update_card(self.api_client).execute(card_id=card.id,body={"data": request})
        assert response.data.type == "individualDebitCard"

    def test_get_pin_status(self):
        response = self.card_list()

        for card in response:
            card_status = card.attributes.status
            if card_status == "active":
                card_id = card.id
                if card_id:
                    pin_status = UnitApi.get_card_pin_status(self.api_client).execute(card_id=card_id)
                    assert pin_status.type == "pinStatus"


    if __name__ == '__main__':
        unittest.main()

    # def test_report_stolen_card(self):
    #     card = self.create_individual_debit_card()
    #     response = ReportCardAsStolenApi(self.api_client).execute(card.id)
    #     assert response.data.type in card_types
    #     assert response.data.attributes.status == "Stolen"
    # def test_report_lost_card(self):
    #     card = self.create_individual_debit_card()
    #     response = ReportCardAsLostApi(self.api_client).execute(card.id)
    #     assert response.data.type in card_types
    #     assert response.data.attributes.status == "Lost"
# def test_update_business_card():
#     card_id = create_business_debit_card().id
#     _address = Address("1818 Pennsylvania Avenue Northwest", "Washington", "CA", "21500", "US")
#     request = PatchBusinessDebitCard(card_id, address=_address, tags={"test": "updated"})
#     response = client.cards.update(request)
#     assert response.data.type == "businessDebitCard"
#     assert response.data.attributes.get("tags").get("test") == "updated"
#

# def test_card_limits():
#     card_id = find_card_id({"type": "individualDebitCard", "status": "Active"})
#     response = client.cards.limits(card_id)
#     assert response.data.type == "limits"
#
# def create_business_credit_card():
#     account_id = create_credit_account_for_business().data.id
#
#     request = CreateBusinessCreditCard(full_name, "2001-08-10", address, phone,
#                                        "richard@piedpiper.com", shipping_address=address,
#                                        idempotency_key=generate_uuid(),
#                                        relationships=create_relationship("creditAccount", account_id, "account"))
#
#     return client.cards.create(request)
#
#
# def test_create_business_credit_card():
#     response = create_business_credit_card()
#     assert response.data.type == "businessCreditCard"
#
#
# def test_create_business_virtual_credit_card():
#     account_id = create_credit_account_for_business().data.id
#
#     request = CreateBusinessVirtualCreditCard(full_name, "2001-08-10", address, phone, "richard@piedpiper.com",
#         relationships=create_relationship("creditAccount", account_id, "account")
#     )
#
#     response = client.cards.create(request)
#     assert response.data.type == "businessVirtualCreditCard"
#
#
# def test_update_business_credit_card():
#     card_id = create_business_credit_card().data.id
#     _address = Address("1818 Pennsylvania Avenue Northwest", "Washington", "CA", "21500", "US")
#     request = PatchBusinessCreditCard(card_id, address=_address, tags={"test": "updated"})
#     response = client.cards.update(request)
#     assert response.data.type == "businessCreditCard"
#     assert response.data.attributes.get("tags").get("test") == "updated"
#
