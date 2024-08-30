import unittest

from helpers.helpers import *
from dist.pythonsdk.openapi_client.api.unit_api import UnitApi
from dist.pythonsdk.openapi_client.models import *

class TestTransactionApi(unittest.TestCase):
    """TransactionApi unit test stubs"""

    def setUp(self):
        self.api_client = create_api_client()

    def tearDown(self):
        pass

    def test_get_list_transactions(self):
        response = UnitApi.get_transactions_list(self.api_client).execute()
        for t in response.data:
            assert "Transaction" in t.type
            # res = GetPaymentApi(self.api_client).execute(p.id).data
            # assert p.id == res.id
            # assert p.type == res.type
            # assert p.attributes.status == res.attributes.status
