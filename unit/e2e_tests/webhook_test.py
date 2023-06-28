import unittest

from e2e_tests.helpers.helpers import create_api_client
from swagger_client import GetListWebhooksApi, CreateWebhookApi


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
        response = CreateWebhookApi(self.api_client).execute({"data": {"type": "webhook",
            "attributes": {
                "label": "some label",
                "url": "https://webhook.site/81ee6b53-fde4-4b7d-85a0-0b6249a4488d",
                "token": "MyToken",
                "contentType": "Json",
                "deliveryMode": "AtLeastOnce",
                "includeResources": False,
                "subscriptionType": "OnlyAuthorizationRequest"
            }}})

        assert response.data.type == "webhook"

