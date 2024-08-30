import unittest

from helpers.helpers import *
from dist.pythonsdk.openapi_client.models import *
from dist.pythonsdk.openapi_client.api.unit_api import UnitApi


class TestPaymentApi(unittest.TestCase):
    """PaymentApi unit test stubs"""

    def setUp(self):
        self.api_client = create_api_client()

    def tearDown(self):
        pass

    def create_individual_customer(self):
        app = UnitApi.create_application(self.api_client).create_application(create_individual_application_request()).data
        return app.relationships.customer.data.id

    def create_deposit_account(self):
        customer_id = self.create_individual_customer()

        attributes = CreateDepositAccountAttributes("checking", {"purpose": "sdk-test"})
        relationships = CreateDepositAccountRelationships(customer={"data": {"type": "customer",
                                                                    "id": customer_id}})
        req = CreateDepositAccount("depositAccount", attributes, relationships)

        response = UnitApi.create_account(self.api_client).create_account({"data": req})
        return response.data.id

    # def test_get_payments_list(self):
    #     response = GetListPaymentsApi(self.api_client).execute()
    #     for p in response.data:
    #         assert "Payment" in p.type

    # def test_get_a_payment(self):
    #     response = GetListPaymentsApi(self.api_client).execute()
    #     for p in response.data:
    #         assert "Payment" in p.type
    #         res = GetPaymentApi(self.api_client).execute(p.id).data
    #         assert p.id == res.id
    #         assert p.type == res.type
    #         assert p.attributes.status == res.attributes.status

    def test_create_book_payment(self):
        id1 = self.create_deposit_account()
        id2 = self.create_deposit_account()

        attributes = CreateBookPaymentAttributes(200, "Book Payment", tags={"purpose": "checking"})
        relationships = CreateBookPaymentRelationships(create_relationship("depositAccount", id1),
                                                       create_relationship("depositAccount", id2))
        req = CreateBookPayment("bookPayment", attributes, relationships)
        res = UnitApi.create_payment(self.api_client).execute({"data": req})

        assert res.data.type == "bookPayment"

    def test_create_inline_ach_payment(self):
        account_id = self.create_deposit_account()
        attributes = CreateAchPaymentAttributes(100, "Funding", direction="Credit",
                                                counterparty=create_counterparty_dto("812345673", "12345569",
                                                                                     "Checking", "Jane Doe"))
        relationships = CreateAchPaymentRelationships(create_relationship("depositAccount", account_id))
        req = CreateAchPayment("achPayment", attributes, relationships)

        res = UnitApi.create_payment(self.api_client).execute({"data": req})

        assert res.data.type == "achPayment"

    def test_create_wire_payment(self):
        account_id = self.create_deposit_account()
        address = Address("1600 Pennsylvania Avenue Northwest", None, "Washington", "CA", "20500", "US")

        attributes = CreateWirePaymentAttributes(200, "Credit", "Wire payment",
                                                 create_wire_counterparty_dto("April Oniel", "812345678",
                                                                              "1000000001", address))
        relationships = CreateAchPaymentRelationships(create_relationship("depositAccount", account_id))

        req = CreateWirePayment("wirePayment", attributes, relationships)

        res = UnitApi.create_payment(self.api_client).execute({"data": req})

        assert res.data.type == "wirePayment"

    def test_create_verified_ach_payment(self):
        account_id = self.create_deposit_account()

        attributes = CreateAchPaymentPlaidAttributes(100, "Funding", direction="Debit",
                                                     plaid_processor_token=
                                                     "processor-sandbox-561f2b29-d9b5-4ef7-90d6-45e1f0c09c0d")
        relationships = CreateAchPaymentRelationships(create_relationship("depositAccount", account_id))

        req = CreateAchPaymentPlaid("achPayment", attributes, relationships)
        res = UnitApi.create_payment(self.api_client).execute({"data": req})

        assert res.data.type == "achPayment"

    # def test_create_linked_ach_payment(self):
    #     account_id = self.create_deposit_account()
#     counterparty_id = create_counterparty().data.id
#     relationships_list = [create_relationship("depositAccount", account_id, "account"),
#                           create_relationship("counterparty", counterparty_id)]
#     relationships = create_relationships_dict(relationships_list)
#     request = CreateLinkedPaymentRequest(10000, "Funding", relationships)
#     response = client.payments.create(request)
#     assert response.data.type == "achPayment"
#
# def test_list_and_get_payments_filter_by_type():
#     payments_ids = []
#     params = ListPaymentParams(type=["AchPayment", "WirePayment"])
#     response = client.payments.list(params)
#
#     for t in response.data:
#         assert t.type == "achPayment" or t.type == "wirePayment"
#         payments_ids.append(t.id)
#
#     for id in payments_ids:
#         response = client.payments.get(id)
#         assert response.data.type == "achPayment" or response.data.type == "wirePayment"
#
#
# def test_list_and_get_payments_filter_by_status():
#     payments_ids = []
#     params = ListPaymentParams(status=["Pending", "Sent"])
#     response = client.payments.list(params)
#
#     for t in response.data:
#         assert t.attributes["status"] == "Pending" or t.attributes["status"] == "Sent"
#         payments_ids.append(t.id)
#
#     for id in payments_ids:
#         response = client.payments.get(id)
#         assert response.data.attributes["status"] == "Pending" or response.data.attributes["status"] == "Sent"
#
#
# def test_update_book_payment():
#     payment_id = create_book_payment().data.id
#     tags = {"purpose": "test"}
#     request = PatchBookPaymentRequest(payment_id, tags)
#     response = client.payments.update(request)
#     assert response.data.type == "bookPayment"
#