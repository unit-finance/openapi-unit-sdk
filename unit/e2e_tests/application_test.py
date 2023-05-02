from __future__ import absolute_import

import os
import unittest
import uuid
from datetime import date, timedelta

from e2e_tests.helpers.helpers import create_api_client, create_individual_application_request, create_business_application_request
from swagger_client import GetListApplicationsApi, CreateApplicationApi, UploadADocumentForAnApplicationApi
from swagger_client.api.get_application_api import GetApplicationApi  # noqa: E501

ApplicationTypes = ["individualApplication", "businessApplication", "trustApplication"]


class TestApplicationApi(unittest.TestCase):
    """ApplicationApi unit test stubs"""

    def setUp(self):
        token = os.environ.get('TOKEN')
        self.api_client = create_api_client()

    def tearDown(self):
        pass

    def create_individual_application(self, ssn="721074426"):
        app = CreateApplicationApi(self.api_client).execute(create_individual_application_request(ssn))

        return app.data

    def create_individual_application_with_included(self, ssn="721074426"):
        app = CreateApplicationApi(self.api_client).execute(create_individual_application_request(ssn))

        return app

    def test_create_individual_application(self):
        res = self.create_individual_application()

        assert res.type == "individualApplication"

    def test_find_application_by_id(self):
        get_application_api = GetApplicationApi(self.api_client)

        app = self.create_individual_application()
        res = get_application_api.execute(app.id).data

        assert res.type == app.type
        assert res.id == app.id

    def create_business_application(self):
        app = CreateApplicationApi(self.api_client).execute(create_business_application_request())

        return app.data

    def test_create_business_application(self):
        response = self.create_business_application()
        assert response.type == "businessApplication"

    def test_list_applications(self):
        res = GetListApplicationsApi(self.api_client).execute()
        for app in res.data:
            assert app.type in ApplicationTypes

    def test_list_and_get_applications(self):
        res = GetListApplicationsApi(self.api_client).execute()
        get_application_api = GetApplicationApi(self.api_client)

        for app in res.data:
            assert app.type in ApplicationTypes
            get_response = get_application_api.execute(app.id).data
            assert get_response.type in ApplicationTypes
            assert get_response.id == app.id

    def test_upload_png_document(self):
        app = self.create_individual_application("000000003")

        application_id = app.id
        document_id = app.relationships.documents.data[0].id

        image_file = open("dog.png")

        res = UploadADocumentForAnApplicationApi(self.api_client).execute(image_file.read(), application_id, document_id)
        assert res.data == "document"

    def test_upload_jpg_document(self):
        app = self.create_individual_application("000000003")

        application_id = app.id
        document_id = app.relationships.documents.data[0].id

        image_file = open("Unit_Logo.jpg", "r").read()

        res = UploadADocumentForAnApplicationApi(self.api_client).execute(image_file, application_id, document_id)
        assert res.data == "document"

    def test_upload_pdf_document(self):
        app = self.create_individual_application("000000003")

        application_id = app.id
        document_id = app.relationships.documents.data[0].id

        image_file = open("sample.pdf", "r").read()

        res = UploadADocumentForAnApplicationApi(self.api_client).execute(image_file, application_id, document_id)
        assert res.data == "document"

    if __name__ == '__main__':
        unittest.main()

    # def test_update_individual_application():
    #     app = create_individual_application()
    #     updated = client.applications.update(PatchApplicationRequest(app.data.id, tags={"patch": "test-patch"}))
    #     assert updated.data.type == "individualApplication"
    #
    # def test_update_business_application():
    #     app = create_business_application()
    #     updated = client.applications.update(PatchApplicationRequest(app.data.id, "businessApplication",
    #                                                                  tags={"patch": "test-patch"}))
    #     assert updated.data.type == "businessApplication"



