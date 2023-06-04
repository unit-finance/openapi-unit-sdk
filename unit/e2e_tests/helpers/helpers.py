import uuid
import os

from datetime import date, timedelta
from swagger_client import configuration, api_client, Address, CreateIndividualApplicationAttributes, FullName, Phone \
    , CreateIndividualApplication, CreateBusinessApplication, CreateBusinessApplicationAttributes, BeneficialOwner, \
    Contact, Officer

ac = None


def create_api_client():
    global ac

    if not ac:
        access_token = os.environ.get("TOKEN")
        _configuration = configuration.Configuration()
        _configuration.api_key['Authorization'] = access_token
        _configuration.api_key_prefix['Authorization'] = 'Bearer'

        ac = api_client.ApiClient(configuration=_configuration)

    return ac


def create_address(street, city, state, postal_code, country):
    return Address(street=street, city=city, state=state, postal_code=postal_code, country=country)


def create_individual_application_request(ssn):
    address = Address(street="1600 Pennsylvania Avenue Northwest", city="Washington", state="CA",
                      postal_code="20500",
                      country="US")
    attr = CreateIndividualApplicationAttributes(FullName("Peter", "Parker"), "jone.doe1@unit-finance.com",
                                                 Phone("1", "2025550108"), ssn,
                                                 address=address, date_of_birth="2001-08-10", dba="Piedpiper Inc",
                                                 ein="123456789", sole_proprietorship=False,
                                                 idempotency_key=str(uuid.uuid1()),
                                                 jwt_subject="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9fQ")

    return {"data": CreateIndividualApplication(attributes=attr)}


def create_business_application_request():
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
        BeneficialOwner(full_name=FullName("Richard", "Hendricks"),
                        date_of_birth=date.today() - timedelta(days=20 * 365),
                        address=create_address("470 Allerton Street", "Redwood City", "CA", "94063", "US"),
                        phone=Phone("1", "2025550158"), email="richard@unit-finance.com", ssn="574572795")
    ]

    attr = CreateBusinessApplicationAttributes(name="Acme Inc.", phone=Phone("1", "9294723497"), address=address,
                                               state_of_incorporation="CA", entity_type="Corporation",
                                               ein="123456789", officer=officer, contact=contact,
                                               beneficial_owners=beneficial_owners,
                                               idempotency_key=str(uuid.uuid1()))

    return {"data": CreateBusinessApplication(attributes=attr)}


def create_relationship(_type, _id):
    return {"data": {"type": _type, "id": _id}}


def create_relationship_with_relation(_type, _id, relation=None):
    if relation:
        return {relation: {"data": {"type": _type, "id": _id}}}
    else:
        return {_type: {"data": {"type": _type, "id": _id}}}


def create_counterparty_dto(routing_number, account_number, account_type, name):
    return {
        "routingNumber": routing_number,
        "accountNumber": account_number,
        "accountType": account_type,
        "name": name
    }


def create_wire_counterparty_dto(name, routing_number, account_number, address):
    return {
        "name": name,
        "routingNumber": routing_number,
        "accountNumber": account_number,
        "address": address
    }
#
# def create_individual_customer(client):
#     request = CreateIndividualApplicationRequest(
#         FullName("Jhon", "Doe"), date.today() - timedelta(days=20 * 365),
#         Address("1600 Pennsylvania Avenue Northwest", "Washington", "CA", "20500", "US"),
#         "jone.doe1@unit-finance.com",
#         Phone("1", "2025550108"), ssn="721074426",
#     )
#     response = client.applications.create(request)
#     for key, value in response.data.relationships.items():
#         if key == "customer":
#             return value.id
#
#     return ""
#
#
# def create_deposit_account(client):
#     customer_id = create_individual_customer(client)
#     request = CreateDepositAccountRequest("checking",
#                                           {"customer": Relationship("customer", customer_id)},
#                                           {"purpose": "credit_operating"})
#     return client.accounts.create(request)
#
