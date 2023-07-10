import unittest

from e2e_tests.python.helpers.helpers import create_api_client
from swagger_client import GetListTransactionsApi


class TestTransactionApi(unittest.TestCase):
    """TransactionApi unit test stubs"""

    def setUp(self):
        self.api_client = create_api_client()

    def tearDown(self):
        pass

    def test_get_list_transactions(self):
        response = GetListTransactionsApi(self.api_client).execute()
        for t in response.data:
            assert "Transaction" in t.type
            # res = GetPaymentApi(self.api_client).execute(p.id).data
            # assert p.id == res.id
            # assert p.type == res.type
            # assert p.attributes.status == res.attributes.status
