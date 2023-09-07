import unittest

from e2e_tests.python.helpers.helpers import create_api_client
from swagger_client import GetListWebhooksApi, CreateWebhookApi
from swagger_client.models.webhooks_data import WebhooksData
from swagger_client.models.webhooks_data_attributes import WebhooksDataAttributes


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

    def test_create_webhook(self):
        body = WebhooksData(attributes=WebhooksDataAttributes(
            "some label", "https://webhook.site/81ee6b53-fde4-4b7d-85a0-0b6249a4488d", "myToken", "Json", "AtLeastOnce",
            False, "OnlyAuthorizationRequest"))
        response = CreateWebhookApi(self.api_client).execute({"data": body})

        assert response.data.type == "webhook"

