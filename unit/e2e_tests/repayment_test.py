from __future__ import absolute_import

import unittest

from e2e_tests.helpers.helpers import create_api_client
from swagger_client import GetListRepaymentsApi, GetRepaymentApi, CreateBookRepaymentRelationships, Relationship, \
    RelationshipData
from swagger_client.api import CreateARepaymentApi
from swagger_client.models.create_book_repayment import CreateBookRepayment
from swagger_client.models.create_book_repayment_attributes import CreateBookRepaymentAttributes
from swagger_client.models.repayments_body import RepaymentsBody


class TestRepaymentsApi(unittest.TestCase):
    """RepaymentsAPI unit test stubs"""

    def setUp(self):
        self.api_client = create_api_client()

    def tearDown(self):
        pass

    def test_list_repayments(self):
        res = GetListRepaymentsApi(self.api_client).execute()
        for app in res.data:
            assert "Repayment" in app.type

    def test_list_and_get_repayments(self):
        res = GetListRepaymentsApi(self.api_client).execute()
        get_repayment_api = GetRepaymentApi(self.api_client)

        for app in res.data:
            assert "Repayment" in app.type
            get_response = get_repayment_api.execute(app.id).data
            assert get_response.type == app.type
            assert get_response.id == app.id

    def test_create_book_repayment(self):
        attr = CreateBookRepaymentAttributes(0, "test - create book repayment", "override",
                                             "3a1a33be-4e12-4603-9ed0-820922389fb8")
        relationships = CreateBookRepaymentRelationships(Relationship(RelationshipData("27573", "depositAccount")),
                                                         Relationship(RelationshipData("1538445", "creditAccount")),
                                                         Relationship(RelationshipData("447232", "account")))
        data = CreateBookRepayment(attributes=attr, relationships=relationships)
        body = RepaymentsBody(data)
        res = CreateARepaymentApi(self.api_client).execute(body)

        assert res.data.type == "bookRepayment"

    if __name__ == '__main__':
        unittest.main()



