import os
import unittest

from helpers.helpers import create_api_client, create_individual_application_request, \
    create_business_application_request
from dist.pythonsdk.openapi_client.api.unit_api import UnitApi
from dist.pythonsdk.openapi_client.models import CreateDepositAccountAttributes, CreateDepositAccountRelationships, CreateCreditAccountAttributes, \
    CreateCreditAccount, CreateDepositAccount


class TestAccountApi(unittest.TestCase):
    """ApplicationApi unit test stubs"""

    def setUp(self):
        token = os.environ.get('TOKEN')
        self.api_client = create_api_client()

    def tearDown(self):
        pass

    def create_individual_customer(self):
        # Ensure that create_individual_application_request() returns the correct type
        application_request = create_individual_application_request()

        # Pass the application_request directly to create_application
        app = UnitApi.create_application(self.api_client).execute(application_request).data

        return app.relationships.customer.data.id

    def create_business_customer(self):
        app = UnitApi.create_application(self.api_client).execute(create_business_application_request()).data
        return app.relationships.customer.data.id

    def create_deposit_account(self):
        customer_id = self.create_individual_customer()

        attributes = CreateDepositAccountAttributes("checking", {"purpose": "sdk-test"})
        relationships = CreateDepositAccountRelationships(customer={"data": {"type": "customer",
                                                                    "id": customer_id}})
        req = CreateDepositAccount("depositAccount", attributes, relationships)

        response = UnitApi.create_account(self.api_client).execute({"data": req})
        return response.data

    def create_credit_account_for_business(self):
        customer_id = self.create_business_customer()
        attributes = CreateCreditAccountAttributes("credit_terms_test", 20000, {"purpose": "some_purpose"})
        relationships = CreateDepositAccountRelationships(customer={"data": {"type": "customer",
                                                                             "id": customer_id}})

        req = CreateCreditAccount("creditAccount", attributes, relationships)
        response = UnitApi.create_account(self.api_client).execute({"data": req})

        return response.data

    def test_create_credit_account_for_business(self):
        response = self.create_credit_account_for_business()
        assert response.type == "creditAccount"

    def create_deposit_account_for_business(self):
        customer_id = self.create_business_customer()

        attributes = CreateDepositAccountAttributes("checking", {"purpose": "sdk-test-business-account"})
        relationships = CreateDepositAccountRelationships(customer={"data": {"type": "customer",
                                                                             "id": customer_id}})
        req = CreateDepositAccount("depositAccount", attributes, relationships)

        response = UnitApi.create_account(self.api_client).execute({"data": req})
        return response.data

    def test_create_deposit_account(self):
        account = self.create_deposit_account()

        assert account.type == "depositAccount"

    def test_create_business_deposit_account(self):
        account = self.create_deposit_account_for_business()

        assert account.type == "depositAccount"

    def test_list_accounts(self):
        res = UnitApi.get_accounts_list(self.api_client).execute() #no unit response type
        for acc in res.data:
            assert acc.type == "depositAccount"

    def test_list_accounts(self):
        res = UnitApi.get_accounts_list(self.api_client).execute() #no unit response type
        for acc in res.data:
            assert acc.type == "depositAccount"
            response_account = UnitApi.get_account(self.api_client).execute(acc.id).data
            assert acc.type == response_account.type
            assert acc.id == response_account.id

    def close_account(self, close_reason="Fraud"):
        account_id = self.create_deposit_account().id

        account = UnitApi.close_account(self.api_client).execute({"data": {
            "type": "depositAccountClose",
            "attributes": {
                "reason": close_reason
            }
        }}, account_id).data

        assert account_id == account.id
        assert account.attributes.status == "Closed"
        assert account.attributes.close_reason == close_reason

        return account

    def test_close_account(self):
        account = self.close_account()
        assert account.type == "depositAccount"

    def test_reopen_account(self):
        account = self.close_account("ByCustomer")
        assert account.type == "depositAccount"

        reopen_account = UnitApi.reopen_account(self.api_client).execute(account.id).data
        assert reopen_account.id == account.id
        assert reopen_account.type == "depositAccount"
        assert reopen_account.attributes.status == "Open"

    def freeze_account(self):
        account_id = self.create_deposit_account().id

        freeze_reason_text = "This is a test - SDK"

        account = UnitApi.freeze_account(self.api_client).execute({"data": {
            "type": "accountFreeze",
            "attributes": {
                "reason": "Other",
                "reasonText": freeze_reason_text
            }
        }}, account_id).data

        assert account_id == account.id
        assert account.attributes.status == "Frozen"
        assert account.attributes.freeze_reason == freeze_reason_text

        return account

    def test_freeze_account(self):
        account = self.freeze_account()
        assert account.type == "depositAccount"

    def test_unfreeze_account(self):
        account = self.freeze_account()
        assert account.type == "depositAccount"

        reopen_account = UnitApi.unfreeze_account(self.api_client).execute(account.id).data
        assert reopen_account.id == account.id
        assert reopen_account.type == "depositAccount"
        assert reopen_account.attributes.status == "Open"


if __name__ == '__main__':
    unittest.main()

# def test_create_joint_deposit_account():
#     customer_id1 = create_individual_customer()
#     customer_id2 = create_individual_customer()
#     request = CreateDepositAccountRequest("checking",
#                                           {"customers": RelationshipArray([
#                                             Relationship("customer", customer_id1),
#                                             Relationship("customer", customer_id2)])},
#                                           {"purpose": "checking"})
#     response = client.accounts.create(request)
#     assert response.data.type == "depositAccount"
#
# def test_limits_account():
#     account_id = create_deposit_account().data.id
#     response = client.accounts.limits(account_id)
#     assert response.data.type == "limits"

# def test_update_account():
#     account_id = create_deposit_account().data.id
#     request = PatchDepositAccountRequest(account_id, tags={
#         "purpose": "tax",
#         "trackUserId": "userId_fe6885b5815463b26f65e71095832bdd916890f7"})
#     response = client.accounts.update(request)
#     assert response.data.type == "depositAccount"
#     assert response.data.attributes.get("tags").get("purpose") == "tax"
#
#
# def test_update_credit_account():
#     account_id = create_credit_account_for_business().data.id
#     _credit_limit = 4000
#     request = PatchCreditAccountRequest(account_id, tags={
#         "purpose": "tax",
#         "trackUserId": "userId_fe6885b5815463b26f65e71095832bdd916890f7"},
#                                         credit_limit=_credit_limit)
#     response = client.accounts.update(request)
#     assert response.data.type == "creditAccount"
#     assert response.data.attributes.get("creditLimit") == _credit_limit
#     assert response.data.attributes.get("tags").get("purpose") == "tax"
#
#
# def test_get_deposit_products():
#     response = create_deposit_account()
#     assert response.data.type == "depositAccount"
#     response = client.accounts.list()
#     assert len(response.data) > 0
#
#     for acc in response.data:
#         deposit_products = client.accounts.get_deposit_products(acc.id).data
#         for dp in deposit_products:
#             assert dp.type == "accountDepositProduct"
#
#
# def add_owners():
#     account_id = create_deposit_account().data.id
#     customer_ids = [create_individual_customer(), create_individual_customer()]
#     return client.accounts.add_owners(AccountOwnersRequest(account_id,
#                                                            RelationshipArray.from_ids_array("customer",
#                                                                                             customer_ids)))
#
# def test_add_owners():
#     response = add_owners()
#     assert response.data.type == "depositAccount"
#     assert response.data.relationships["customers"].data is not None
#     assert len(response.data.relationships["customers"].data) == 3
#
# def test_remove_owners():
#     response = add_owners()
#     assert response.data.type == "depositAccount"
#     account_id = response.data.id
#     last_owner_id = response.data.relationships["customers"].data.pop().id # An account should have at least one owner
#     response = client.accounts.remove_owners(AccountOwnersRequest(account_id, response.data.relationships["customers"]))
#     assert response.data.type == "depositAccount"
#     assert response.data.relationships.get("customer").id == last_owner_id
