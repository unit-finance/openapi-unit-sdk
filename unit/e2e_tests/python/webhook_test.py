import unittest

from helpers.helpers import *
from dist.pythonsdk.openapi_client.api.unit_api import UnitApi
from dist.pythonsdk.openapi_client.models import *


class TestWebhookApi(unittest.TestCase):
    """WebhookApi unit test stubs"""

    def setUp(self):
        self.api_client = create_api_client()

    def tearDown(self):
        pass

    def test_get_list_webhooks(self):
        response = UnitApi.get_webhooks_list(self.api_client).execute()
        for w in response.data:
            assert w.type == "webhook"

    def test_create_webhook(self):
        body = CreateWebhook(attributes=CreateWebhookDataAttributes(
            "some label", "https://webhook.site/81ee6b53-fde4-4b7d-85a0-0b6249a4488d", "myToken", "Json", "AtLeastOnce",
            False, "OnlyAuthorizationRequest"))
        response = UnitApi.create_webhook(self.api_client).execute({"data": body})

        assert response.data.type == "webhook"

