from __future__ import absolute_import
from datetime import timedelta, date

import os
import uuid
import unittest

from e2e_tests.helpers.helpers import create_api_client, create_individual_application_request
from swagger_client import configuration, api_client, Address, CreateIndividualApplicationAttributes, FullName, Phone, \
    CreateApplicationApi, CreateIndividualApplication, CreateBusinessApplicationAttributes, Officer, Contact, \
    BeneficialOwner, CreateBusinessApplication, GetListApplicationsApi
from swagger_client.api.get_application_api import GetApplicationApi  # noqa: E501

ApplicationTypes = ["individualApplication", "businessApplication", "trustApplication"]


def create_address(street, city, state, postal_code, country):
    return Address(street=street, city=city, state=state, postal_code=postal_code, country=country)


class TestApplicationApi(unittest.TestCase):
    """ApplicationApi unit test stubs"""

    def setUp(self):
        token = os.environ.get('TOKEN')
        self.api_client = create_api_client()

    def tearDown(self):
        pass

    def create_individual_application(self):
        app = CreateApplicationApi(self.api_client).create_application(create_individual_application_request())

        return app.data

    def test_create_individual_application(self):
        res = self.create_individual_application()

        assert res.type == "individualApplication"

    def test_find_application_by_id(self):
        get_application_api = GetApplicationApi(self.api_client)

        app = self.create_individual_application()
        res = get_application_api.find_application_by_id(app.id).data

        assert res.type == app.type
        assert res.id == app.id

    def create_business_application(self):
        address = Address(street="1600 Pennsylvania Avenue Northwest", city="Washington", state="CA",
                          postal_code="20500", country="US")
        officer = Officer(full_name=FullName("Jone", "Doe"), date_of_birth=date.today() - timedelta(days=20 * 365),
                           address=create_address("950 Allerton Street", "Redwood City", "CA", "94063", "US"),
                           phone=Phone("1", "2025550108"), email="jone.doe@unit-finance.com", ssn="123456789")
        contact = Contact(full_name=FullName("Jone", "Doe"), email="jone.doe@unit-finance.com",
                          phone=Phone("1", "2025550108"))
        beneficial_owners = [
            BeneficialOwner(full_name=FullName("James", "Smith"), date_of_birth=date.today() - timedelta(days=20 * 365),
                            address=create_address("650 Allerton Street", "Redwood City", "CA", "94063", "US"),
                            phone=Phone("1", "2025550127"), email="james@unit-finance.com", ssn="574567625"),
            BeneficialOwner(full_name=FullName("Richard", "Hendricks"), date_of_birth=date.today() - timedelta(days=20 * 365),
                            address=create_address("470 Allerton Street", "Redwood City", "CA", "94063", "US"),
                            phone=Phone("1", "2025550158"), email="richard@unit-finance.com", ssn="574572795")
        ]

        attr = CreateBusinessApplicationAttributes(name="Acme Inc.", phone=Phone("1", "9294723497"), address=address,
                                                   state_of_incorporation="CA", entity_type="Corporation",
                                                   ein="123456789", officer=officer, contact=contact,
                                                   beneficial_owners=beneficial_owners,
                                                   idempotency_key=str(uuid.uuid1()))

        app = CreateApplicationApi(self.api_client).create_application(
            {"data": CreateBusinessApplication(attributes=attr)})

        return app.data

    def test_create_business_application(self):
        response = self.create_business_application()
        assert response.type == "businessApplication"

    def test_list_applications(self):
        res = GetListApplicationsApi(self.api_client).find_list_application()
        for app in res.data:
            assert app.type in ApplicationTypes

    def test_list_and_get_applications(self):
        res = GetListApplicationsApi(self.api_client).find_list_application()
        get_application_api = GetApplicationApi(self.api_client)

        for app in res.data:
            assert app.type in ApplicationTypes
            get_response = get_application_api.find_application_by_id(app.id).data
            assert get_response.type in ApplicationTypes
            assert get_response.id == app.id

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



