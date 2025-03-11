import unittest

from helpers.helpers import create_api_client
from dist.pythonsdk.openapi_client.api.unit_api import UnitApi

class TestApplicationFormApi(unittest.TestCase):
    """ApplicationFormApi unit test stubs"""

    def setUp(self):
        self.api_client = create_api_client()
        self.unit_api = UnitApi(self.api_client)  # Instantiate UnitApi with ApiClient

    def tearDown(self):
        pass

    def test_get_list_application_forms(self):
        response = self.unit_api.get_application_forms_list().execute()
        for w in response.data:
            assert w.type == "applicationForm"
