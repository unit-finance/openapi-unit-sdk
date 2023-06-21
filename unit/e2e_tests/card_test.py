import os
import unittest
import requests

from e2e_tests.helpers.helpers import create_api_client, create_individual_application_request, \
    create_business_application_request
from swagger_client import GetListOfCardsApi, GetCardApi, CreateApplicationApi, CreateDepositAccountAttributes, \
    CreateDepositAccountRelationships, CreateDepositAccount, CreateAnAccountApi, CreateACardApi, FreezeACardApi, \
    UnfreezeACardApi, CloseACardApi, ReportCardAsStolenApi, ReportCardAsLostApi, CreateIndividualDebitCard, \
    CreateIndividualDebitCardAttributes, Address, CardLevelLimits, CreateCardRelationships, Relationship, \
    RelationshipData

card_types = ["individualDebitCard", "businessDebitCard", "individualVirtualDebitCard", "businessVirtualDebitCard",
              "businessCreditCard", "businessVirtualCreditCard"]

headers = {
            "content-type": "application/vnd.api+json",
            "authorization": f"Bearer {os.environ.get('TOKEN')}",
            "user-agent": "unit-python-sdk"
        }


class TestCardApi(unittest.TestCase):
    """CardApi unit test stubs"""

    def setUp(self):
        self.api_client = create_api_client()

    def tearDown(self):
        pass

    def test_card_list(self):
        res = GetListOfCardsApi(self.api_client).execute().data
        for card in res:
            assert card.type in card_types
            if card.attributes.status == "Inactive":
                requests.post(f"https://api.s.unit.sh/sandbox/cards/{card.id}/activate/", headers=headers)

    def test_card_list_and_get(self):
        res = GetListOfCardsApi(self.api_client).execute().data
        for card in res:
            assert card.type in card_types
            response_card = GetCardApi(self.api_client).execute(card.id).data
            assert card.id == response_card.id
            assert card.type == response_card.type

    def create_individual_customer(self):
        app = CreateApplicationApi(self.api_client).execute(create_individual_application_request()).data
        return app.relationships.customer.data.id

    def create_business_customer(self):
        app = CreateApplicationApi(self.api_client).execute(create_business_application_request()).data
        return app.relationships.customer.data.id

    def create_deposit_account(self):
        customer_id = self.create_individual_customer()

        attributes = CreateDepositAccountAttributes("checking", {"purpose": "sdk-test"})
        relationships = CreateDepositAccountRelationships(customer={"data": {"type": "customer",
                                                                    "id": customer_id}})
        req = CreateDepositAccount("depositAccount", attributes, relationships)

        response = CreateAnAccountApi(self.api_client).execute({"data": req})
        return response.data

    def create_deposit_account_for_business(self):
        customer_id = self.create_business_customer()

        attributes = CreateDepositAccountAttributes("checking", {"purpose": "sdk-test-business-account"})
        relationships = CreateDepositAccountRelationships(customer={"data": {"type": "customer",
                                                                             "id": customer_id}})
        req = CreateDepositAccount("depositAccount", attributes, relationships)

        response = CreateAnAccountApi(self.api_client).execute({"data": req})
        return response.data

    def create_individual_debit_card(self):
        account_id = self.create_deposit_account().id
        attributes = CreateIndividualDebitCardAttributes(Address("5230 Newell Rd", None, "Palo Alto", "CA", "94303"),
                                                         limits=CardLevelLimits(50000, 50000, 50000, 70000))
        req = CreateIndividualDebitCard("individualDebitCard", attributes,
                                        CreateCardRelationships(Relationship(RelationshipData(account_id,
                                                                                              "depositAccount"))))

        card = CreateACardApi(self.api_client).execute({"data": req}).data

        res = requests.post(f"https://api.s.unit.sh/sandbox/cards/{card.id}/activate/", headers=headers)

        if res.status_code != 200:
            print("Failed to activate card")

        return card

    def test_create_individual_debit_card(self):
        card = self.create_individual_debit_card()
        assert card.type == "individualDebitCard"

    def create_business_debit_card(self):
        account_id = self.create_deposit_account_for_business().id

        req = {
            "type": "businessDebitCard",
            "attributes": {
                "shippingAddress": {
                    "street": "5230 Newell Rd",
                    "street2": None,
                    "city": "Palo Alto",
                    "state": "CA",
                    "postalCode": "94303",
                    "country": "US"
                },
                "fullName": {
                    "first": "Richard",
                    "last": "Hendricks"
                },
                "address": {
                    "street": "5230 Newell Rd",
                    "street2": None,
                    "city": "Palo Alto",
                    "state": "CA",
                    "postalCode": "94303",
                    "country": "US"
                },
                "dateOfBirth": "2001-08-10",
                "email": "richard@piedpiper.com",
                "phone": {
                    "countryCode": "1",
                    "number": "5555555555"
                },
                "limits": {
                    "dailyWithdrawal": 50000,
                    "dailyPurchase": 50000,
                    "monthlyWithdrawal": 500000,
                    "monthlyPurchase": 700000
                }
            },
            "relationships": {
                "account": {
                    "data": {
                        "type": "depositAccount",
                        "id": account_id
                    }
                }
            }
        }

        card = CreateACardApi(self.api_client).execute({"data": req}).data

        res = requests.post(f"https://api.s.unit.sh/sandbox/cards/{card.id}/activate/", headers=headers)

        if res.status_code != 200:
            print("Failed to activate card")

        return card

    def test_create_business_debit_card(self):
        card = self.create_business_debit_card()
        assert card.type == "businessDebitCard"

    def create_business_virtual_debit_card(self):
        account_id = self.create_deposit_account_for_business().id

        req = {
            "type": "businessVirtualDebitCard",
            "attributes": {
              "fullName": {
                "first": "Richard",
                "last": "Hendricks"
              },
              "address": {
                "street": "5230 Newell Rd",
                "street2": None,
                "city": "Palo Alto",
                "state": "CA",
                "postalCode": "94303",
                "country": "US"
              },
              "dateOfBirth": "2001-08-10",
              "email": "richard@piedpiper.com",
              "phone": {
                "countryCode": "1",
                "number": "5555555555"
              },
              "limits": {
                "dailyWithdrawal": 50000,
                "dailyPurchase": 50000,
                "monthlyWithdrawal": 500000,
                "monthlyPurchase": 700000
              }
            },
            "relationships": {
              "account": {
                "data": {
                  "type": "depositAccount",
                  "id": account_id
                }
              }
            }
          }

        card = CreateACardApi(self.api_client).execute({"data": req}).data

        res = requests.post(f"https://api.s.unit.sh/sandbox/cards/{card.id}/activate/", headers=headers)

        if res.status_code != 200:
            print("Failed to activate card")

        return card

    def test_create_business_virtual_debit_card(self):
        card = self.create_business_virtual_debit_card()
        assert card.type == "businessVirtualDebitCard"

    def create_individual_virtual_debit_card(self):
        account_id = self.create_deposit_account().id

        req = {
            "type": "individualVirtualDebitCard",
            "attributes": {
                "limits": {
                    "dailyWithdrawal": 50000,
                    "dailyPurchase": 50000,
                    "monthlyWithdrawal": 500000,
                    "monthlyPurchase": 700000
                }
            },
            "relationships": {
                "account": {
                    "data": {
                        "type": "depositAccount",
                        "id": account_id
                    }
                }
            }
        }
        card = CreateACardApi(self.api_client).execute({"data": req}).data

        res = requests.post(f"https://api.s.unit.sh/sandbox/cards/{card.id}/activate/", headers=headers)

        if res.status_code != 200:
            print("Failed to activate card")

        return card

    def test_create_individual_virtual_debit_card(self):
        card = self.create_individual_virtual_debit_card()
        assert card.type == "individualVirtualDebitCard"

    def test_freeze_and_unfreeze_card(self):
        card = self.create_individual_debit_card()
        assert card.type == "individualDebitCard"
        response = FreezeACardApi(self.api_client).freeze_card(card.id).data
        assert response.attributes.status == "Frozen"
        response = UnfreezeACardApi(self.api_client).unfreeze_card(card.id).data
        assert response.attributes.status != "Frozen"

    def test_close_card(self):
        card = self.create_individual_debit_card()
        response = CloseACardApi(self.api_client).close_card(card.id).data
        assert response.attributes.status == "ClosedByCustomer"

    # def test_replace_card(self):
    #     card = self.create_individual_debit_card()
    #     _address = Address("1616 Pennsylvania Avenue Northwest", "Washington", "CA", "21500", "US")
    #     response = client.cards.replace(card_id, _address)
    #     assert response.data.type == "individualDebitCard"
    #

    def test_report_stolen_card(self):
        card = self.create_individual_debit_card()
        response = ReportCardAsStolenApi(self.api_client).report_stolen_card(card.id)
        assert response.data.type in card_types
        assert response.data.attributes.status == "Stolen"

    def test_report_lost_card(self):
        card = self.create_individual_debit_card()
        response = ReportCardAsLostApi(self.api_client).report_lost_card(card.id)
        assert response.data.type in card_types
        assert response.data.attributes.status == "Lost"

    if __name__ == '__main__':
        unittest.main()

#
# def test_get_debit_card_include_customer():
#     card_id = create_individual_debit_card().data.id
#     response = client.cards.get(card_id, "customer")
#     assert response.data.type in card_types and response.included is not None
#
# def test_update_individual_card():
#     card_id = find_card_id({"type": "individualDebitCard", "status": "Active"})
#     _address = Address("1818 Pennsylvania Avenue Northwest", "Washington", "CA", "21500", "US")
#     request = PatchIndividualDebitCard(card_id, _address, tags={"test": "updated"})
#     response = client.cards.update(request)
#     assert response.data.type == "individualDebitCard"
#     assert response.data.attributes.get("tags").get("test") == "updated"
#
# def test_update_business_card():
#     card_id = create_business_debit_card().id
#     _address = Address("1818 Pennsylvania Avenue Northwest", "Washington", "CA", "21500", "US")
#     request = PatchBusinessDebitCard(card_id, address=_address, tags={"test": "updated"})
#     response = client.cards.update(request)
#     assert response.data.type == "businessDebitCard"
#     assert response.data.attributes.get("tags").get("test") == "updated"
#
# def test_get_pin_status():
#     response = client.cards.list()
#     for card in response.data:
#         if card.attributes["status"] != "Inactive":
#             pin_status = client.cards.get_pin_status(card.id).data
#             assert pin_status.type == "pinStatus"
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
