from __future__ import absolute_import

import base64
import unittest

from e2e_tests.python.helpers.helpers import create_api_client, create_individual_application_request,\
    create_business_application_request
from swagger_client import GetListApplicationsApi, CreateApplicationApi, CreateSoleProprietorApplication, \
    CreateSoleProprietorApplicationAttributes, Address, FullName, Phone, \
    CreateTrustApplication, CreateTrustApplicationAttributes, Grantor, Trustee, Beneficiary, TrustContact, \
    UploadAPNGDocumentForAnApplicationApi, UploadAJPEGDocumentForAnApplicationApi, UploadAPDFDocumentForAnApplicationApi
from swagger_client.api.get_application_api import GetApplicationApi  # noqa: E501

ApplicationTypes = ["individualApplication", "businessApplication", "trustApplication"]


class TestApplicationApi(unittest.TestCase):
    """ApplicationApi unit test stubs"""

    def setUp(self):
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

        image_file = open("unit_photo.png", 'rb').read()

        res = UploadAPNGDocumentForAnApplicationApi(self.api_client).execute(image_file.decode('latin-1'), application_id, document_id)
        assert res.data.type == "document"

    def test_upload_jpg_document(self):
        app = self.create_individual_application("000000003")

        application_id = app.id
        document_id = app.relationships.documents.data[0].id

        image_file = open("Unit_Logo.jpg", "r", encoding='latin-1').read()

        res = UploadAJPEGDocumentForAnApplicationApi(self.api_client).execute(str(image_file), application_id, document_id)
        assert res.data.type == "document"

    def test_upload_pdf_document(self):
        app = self.create_individual_application("000000003")

        application_id = app.id
        document_id = app.relationships.documents.data[0].id

        image_file = open("sample.pdf", "r").read()

        res = UploadAPDFDocumentForAnApplicationApi(self.api_client).execute(str(image_file), application_id, document_id)
        assert res.data.type == "document"

    def test_create_sole_proprietor_application(self):
        address = Address(street="1600 Pennsylvania Avenue Northwest", city="Washington", state="CA",
                          postal_code="20500",
                          country="US")
        attr = CreateSoleProprietorApplicationAttributes(FullName("Peter", "Parker"), "jone.doe1@unit-finance.com",
                                                         Phone("1", "2025550108"), "721074426",
                                                         address=address, date_of_birth="2001-08-10",
                                                         dba="Piedpiper Inc", ein="123456789",
                                                         annual_income="Between50kAnd100k",
                                                         source_of_income="EmploymentOrPayrollIncome",
                                                         annual_revenue="Between100kAnd200k",
                                                         sole_proprietorship=True,
                                                         number_of_employees="Between5And10",
                                                         business_vertical="TechnologyMediaOrTelecom",
                                                         jwt_subject="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9fQ")

        req = {"data": CreateSoleProprietorApplication(attributes=attr)}

        app = CreateApplicationApi(self.api_client).execute(req)

        assert app.data.type == "individualApplication"

    def test_create_trust_application(self):
        address = Address(street="1600 Pennsylvania Avenue Northwest", city="Washington", state="CA",
                          postal_code="20500",
                          country="US")
        full_name = FullName("Richard", "Hendricks")

        attr = CreateTrustApplicationAttributes("Trust me Inc.", "CA", "Revocable", "Salary", "123456789",
                                                Grantor(full_name, "richard@piedpiper.com",
                                                        Phone("1", "2025550108"), "000000002",
                                                        date_of_birth="2000-01-01", address=address),
                                                [Trustee(full_name, "richard@piedpiper.com", address=address,
                                                         date_of_birth="2000-01-01", ssn="000000002",
                                                         phone=Phone("1", "2025550108"))],
                                                [Beneficiary(FullName("Dinesh","Chugtai"), "2000-01-01"),
                                                Beneficiary(FullName("Gilfoyle","Unknown"), "2000-01-01")],
                                                TrustContact(FullName("Jared","Dann"), "jared@piedpiper.com",
                                                             Phone("1", "2025550108"),
                                                             address=Address("5230 Newell Rd", "", "Palo Alto", "CA",
                                                                             "94303")))

        req = {"data": CreateTrustApplication(attributes=attr)}

        app = CreateApplicationApi(self.api_client).execute(req)

        assert app.data.type == "trustApplication"

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



