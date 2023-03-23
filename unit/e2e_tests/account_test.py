import os
import unittest

from e2e_tests.helpers.helpers import create_api_client, create_individual_application_request, \
    create_business_application_request
from swagger_client import CreateApplicationApi, CreateAnAccountApi, \
    CreateDepositAccountAttributes, CreateDepositAccountRelationships, CreateDepositAccount, GetListAccountsApi, \
    GetAccountApi, CloseAnAccountApi, ReopenAnAccountApi, FreezeAnAccountApi, UnfreezeAccountApi


class TestAccountApi(unittest.TestCase):
    """ApplicationApi unit test stubs"""

    def setUp(self):
        token = os.environ.get('TOKEN')
        self.api_client = create_api_client()

    def tearDown(self):
        pass

    def create_individual_customer(self):
        app = CreateApplicationApi(self.api_client).create_application(create_individual_application_request()).data
        return app.relationships.customer.data.id

    def create_business_customer(self):
        app = CreateApplicationApi(self.api_client).create_application(create_business_application_request()).data
        return app.relationships.customer.data.id

    def create_deposit_account(self):
        customer_id = self.create_individual_customer()

        attributes = CreateDepositAccountAttributes("checking", {"purpose": "sdk-test"})
        relationships = CreateDepositAccountRelationships(customer={"data": {"type": "customer",
                                                                    "id": customer_id}})
        req = CreateDepositAccount("depositAccount", attributes, relationships)

        response = CreateAnAccountApi(self.api_client).create_account({"data": req})
        return response.data

    def create_deposit_account_for_business(self):
        customer_id = self.create_business_customer()

        attributes = CreateDepositAccountAttributes("checking", {"purpose": "sdk-test-business-account"})
        relationships = CreateDepositAccountRelationships(customer={"data": {"type": "customer",
                                                                             "id": customer_id}})
        req = CreateDepositAccount("depositAccount", attributes, relationships)

        response = CreateAnAccountApi(self.api_client).create_account({"data": req})
        return response.data

    def test_create_deposit_account(self):
        account = self.create_deposit_account()

        assert account.type == "depositAccount"

    def test_create_business_deposit_account(self):
        account = self.create_deposit_account_for_business()

        assert account.type == "depositAccount"

    def test_list_accounts(self):
        res = GetListAccountsApi(self.api_client).find_list_accounts() #no unit response type
        for acc in res.data:
            assert acc.type == "depositAccount"

    def test_list_accounts(self):
        res = GetListAccountsApi(self.api_client).find_list_accounts() #no unit response type
        for acc in res.data:
            assert acc.type == "depositAccount"
            response_account = GetAccountApi(self.api_client).find_account_by_id(acc.id).data
            assert acc.type == response_account.type
            assert acc.id == response_account.id

    def close_account(self, close_reason="Fraud"):
        account_id = self.create_deposit_account().id

        account = CloseAnAccountApi(self.api_client).close_account_by_id({"data": {
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

        reopen_account = ReopenAnAccountApi(self.api_client).reopen_account_by_id(account.id).data
        assert reopen_account.id == account.id
        assert reopen_account.type == "depositAccount"
        assert reopen_account.attributes.status == "Open"

    def freeze_account(self):
        account_id = self.create_deposit_account().id

        freeze_reason_text = "This is a test - SDK"

        account = FreezeAnAccountApi(self.api_client).freeze_account_by_id({"data": {
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

        reopen_account = UnfreezeAccountApi(self.api_client).unfreeze_account_by_id(account.id).data
        assert reopen_account.id == account.id
        assert reopen_account.type == "depositAccount"
        assert reopen_account.attributes.status == "Open"


if __name__ == '__main__':
    unittest.main()

# def create_credit_account_for_business():
#     customer_id = create_business_customer()
#     request = CreateCreditAccountRequest("credit_terms_test", 20000, create_relationship("customer", customer_id),
#                                          {"purpose": "some_purpose"})
#     return client.accounts.create(request)
#
#
# def test_create_credit_account_for_business():
#     response = create_credit_account_for_business()
#     assert response.data.type == "creditAccount"
#
#
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
