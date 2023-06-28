import unittest

from e2e_tests.helpers.helpers import create_api_client
from swagger_client import GetListWebhooksApi


class TestWebhookApi(unittest.TestCase):
    """WebhookApi unit test stubs"""

    def setUp(self):
        self.api_client = create_api_client()

    def tearDown(self):
        pass

    def test_get_list_webhooks(self):
        response = GetListWebhooksApi(self.api_client).execute()
        for w in response.data:
            assert w.type == "webhook"
