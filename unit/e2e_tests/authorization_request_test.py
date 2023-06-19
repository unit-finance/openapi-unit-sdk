from __future__ import absolute_import

import os
import unittest

from e2e_tests.helpers.helpers import create_api_client
from swagger_client import GetAuthorizationRequestApi, GetListAuthorizationRequestsApi, ApproveAuthorizationRequestApi, \
    AuthorizationIdApproveBody, ApproveAuthorizationRequest, DeclineAuthorizationRequestApi, \
    DeclineAuthorizationRequestAttributes
from swagger_client.models.decline_authorization_request import DeclineAuthorizationRequest


class TestAuthorizationRequestsApi(unittest.TestCase):
    """AuthorizationRequestsAPI unit test stubs"""

    def setUp(self):
        self.api_client = create_api_client()

    def tearDown(self):
        pass

    def test_list_authorization_requests(self):
        res = GetListAuthorizationRequestsApi(self.api_client).execute()
        for app in res.data:
            assert "AuthorizationRequest" in app.type

    def test_list_and_get_authorization_requests(self):
        res = GetListAuthorizationRequestsApi(self.api_client).execute()
        get_application_request_api = GetAuthorizationRequestApi(self.api_client)

        for app in res.data:
            assert "AuthorizationRequest" in app.type
            get_response = get_application_request_api.execute(app.id).data
            assert get_response.type == app.type
            assert get_response.id == app.id

    # def test_approve_authorization_requests(self):
    #     req = ApproveAuthorizationRequest(attributes={"amount": 50})
    #     res = ApproveAuthorizationRequestApi(self.api_client).execute({"data": req}, authorization_id).data
    #     assert "AuthorizationRequest" in res.type
    #     assert res.id == authorization_id
    #
    # def test_decline_authorization_requests(self):
    #     attr = DeclineAuthorizationRequestAttributes(reason="RestrictedCard")
    #     req = DeclineAuthorizationRequest(attributes=attr)
    #     res = DeclineAuthorizationRequestApi(self.api_client).execute({"data": req}, authorization_id).data
    #     assert "AuthorizationRequest" in res.type
    #     assert res.id == authorization_id

    if __name__ == '__main__':
        unittest.main()



