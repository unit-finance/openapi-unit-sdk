import os
import unittest
from e2e_tests.python.helpers.helpers import *
from swagger_client import *

CustomerTypes = ["individualCustomer", "businessCustomer"]

authorized_users = [
    {
        "fullName": {
            "first": "Jared",
            "last": "Dunn"
        },
        "email": "jared@piedpiper.com",
        "phone": {
            "countryCode": "1",
            "number": "1555555590"
        }
    }
]


class TestCustomerApi(unittest.TestCase):
    """CustomerApi unit test stubs"""

    def setUp(self):
        self.api_client = create_api_client()

    def tearDown(self):
        pass

    def list_customers(self):
        return GetListCustomersApi(self.api_client).execute()

    def test_get_customer(self):
        customers = self.list_customers()
        for customer in customers.data:
            GetCustomerApi(self.api_client).execute(customer.id)
            assert customer.type in CustomerTypes

    def test_list_customers(self):
        customers = self.list_customers()
        for customer in customers.data:
            assert customer.type in CustomerTypes

    def get_customer(self, customer_type='individualCustomer'):
        customers = self.list_customers()

        for customer in customers.data:
            if customer.type == customer_type:
                return GetCustomerApi(self.api_client).execute(customer.id)

        return None

    def test_update_individual_customer(self):
        _address = create_address("1818 Pennsylvania Avenue Northwest", "Washington", "CA", "21500", "US")
        _attributes = UpdateIndividualCustomerAttributes(address=_address, tags={"test": "updated"})
        request = UpdateIndividualCustomer(type="individualCustomer", attributes=_attributes)

        response = UpdateCustomerApi(self.api_client).execute(customer_id=self.get_customer().data.id, body={"data": request})
        assert response.data.type == "individualCustomer"

    if __name__ == '__main__':
        unittest.main()
#
#
# def get_customer_by_type(type: str):
#     response = client.customers.list(ListCustomerParams(0, 1000))
#     for c in response.data:
#         if c.type == type:
#             return c
#     return None
#
# def test_update_business_customer():
#     business_customer_id = get_customer_by_type("businessCustomer").id
#     request = PatchBusinessCustomerRequest(business_customer_id, phone=Phone("1", "1115551111"))
#     response = client.customers.update(request)
#     assert response.data.type == "businessCustomer"
#
#
# def test_business_customer():
#     business_customer_api_response = {
#         "type": "businessCustomer",
#         "id": "1",
#         "attributes": {
#             "createdAt": "2020-05-10T12:28:37.698Z",
#             "name": "Pied Piper",
#             "address": {
#                 "street": "5230 Newell Rd",
#                 "street2": None,
#                 "city": "Palo Alto",
#                 "state": "CA",
#                 "postalCode": "94303",
#                 "country": "US"
#             },
#             "phone": {
#                 "countryCode": "1",
#                 "number": "1555555578"
#             },
#             "stateOfIncorporation": "DE",
#             "ein": "123456789",
#             "entityType": "Corporation",
#             "contact": {
#                 "fullName": {
#                     "first": "Richard",
#                     "last": "Hendricks"
#                 },
#                 "email": "richard@piedpiper.com",
#                 "phone": {
#                     "countryCode": "1",
#                     "number": "1555555578"
#                 }
#             },
#             "authorizedUsers": authorized_users,
#             "status": "Active",
#             "tags": {
#                 "userId": "106a75e9-de77-4e25-9561-faffe59d7814"
#             }
#         },
#         "relationships": {
#             "org": {
#                 "data": {
#                     "type": "org",
#                     "id": "1"
#                 }
#             },
#             "application": {
#                 "data": {
#                     "type": "businessApplication",
#                     "id": "1"
#                 }
#             }
#         }
#     }
#
#     id = business_customer_api_response["id"]
#     _type = business_customer_api_response["type"]
#
#     customer = DtoDecoder.decode(business_customer_api_response)
#
#     assert customer.id == id
#     assert customer.type == _type
#     assert customer.attributes["address"].street == "5230 Newell Rd"
#     assert customer.attributes["name"] == "Pied Piper"
#     assert customer.attributes["stateOfIncorporation"] == "DE"
#     assert customer.attributes["contact"].full_name.first == "Richard"
#     assert customer.attributes["authorizedUsers"][0].full_name.first == "Jared"
#     assert customer.attributes["status"] == "Active"
#
#
# def test_individual_customer():
#     individual_customer_api_response = {
#         "type": "individualCustomer",
#         "id": "8",
#         "attributes": {
#             "createdAt": "2020-05-12T19:41:04.123Z",
#             "fullName": {
#                 "first": "Peter",
#                 "last": "Parker"
#             },
#             "ssn": "721074426",
#             "address": {
#                 "street": "20 Ingram St",
#                 "street2": None,
#                 "city": "Forest Hills",
#                 "state": "NY",
#                 "postalCode": "11375",
#                 "country": "US"
#             },
#             "dateOfBirth": "2001-08-10",
#             "email": "peter@oscorp.com",
#             "phone": {
#                 "countryCode": "1",
#                 "number": "1555555578"
#             },
#             "authorizedUsers": [],
#             "status": "Active",
#             "tags": {
#                 "userId": "106a75e9-de77-4e25-9561-faffe59d7814"
#             }
#         },
#         "relationships": {
#             "org": {
#                 "data": {
#                     "type": "org",
#                     "id": "1"
#                 }
#             },
#             "application": {
#                 "data": {
#                     "type": "individualApplication",
#                     "id": "8"
#                 }
#             }
#         }
#     }
#
#     id = individual_customer_api_response["id"]
#     _type = individual_customer_api_response["type"]
#
#     customer = DtoDecoder.decode(individual_customer_api_response)
#
#     assert customer.id == id
#     assert customer.type == _type
#     assert customer.attributes["address"].street == "20 Ingram St"
#     assert customer.attributes["fullName"].first == "Peter"
#     assert customer.attributes["fullName"].last == "Parker"
#     assert customer.attributes["email"] == "peter@oscorp.com"
#     assert customer.attributes["phone"].number == "1555555578"
#     assert customer.attributes["authorizedUsers"] == []
#     assert customer.attributes["status"] == "Active"
#
#
# def add_authorized_users_to_individual_customer():
#     individual_customer_id = create_individual_customer(client)
#     request = AddAuthorizedUsersRequest(individual_customer_id, authorized_users)
#     return client.customers.add_authorized_users(request)
#
#
# def test_add_authorized_users_to_individual_customer():
#     response = add_authorized_users_to_individual_customer()
#     assert response.data.type == "individualCustomer"
#     assert len(response.data.attributes.get("authorizedUsers")) == 1
#     assert response.data.attributes.get("authorizedUsers")[0].email == authorized_users[0].get("email")
#
#
# def test_remove_authorized_users_to_individual_customer():
#     add_response = add_authorized_users_to_individual_customer()
#     assert add_response.data.type == "individualCustomer"
#     assert len(add_response.data.attributes.get("authorizedUsers")) == 1
#     assert add_response.data.attributes.get("authorizedUsers")[0].email == authorized_users[0].get("email")
#     authorized_users_emails = [authorized_users[0].get("email")]
#     request = RemoveAuthorizedUsersRequest(add_response.data.id, authorized_users_emails)
#     remove_response = client.customers.remove_authorized_users(request)
#     assert remove_response.data.id == add_response.data.id
#     assert remove_response.data.type == add_response.data.type
#     assert len(remove_response.data.attributes.get("authorizedUsers")) == 0
