import uuid

from datetime import date, timedelta
from swagger_client import configuration, api_client, Address, CreateIndividualApplicationAttributes, FullName, Phone \
    , CreateIndividualApplication, CreateBusinessApplication, CreateBusinessApplicationAttributes, BeneficialOwner, \
    Contact, Officer

ac = None


def create_api_client():
    global ac

    if not ac:
        access_token = "v2.public.eyJyb2xlIjoiYWRtaW4iLCJ1c2VySWQiOiIyNTIiLCJzdWIiOiJhYWxleHNoYW5pQGdtYWlsLmNvbSIsImV4cCI6IjIwMjMtMDYtMjlUMDg6NDY6MTYuNTI0WiIsImp0aSI6IjE2MDAzOSIsIm9yZ0lkIjoiMTI2Iiwic2NvcGUiOiJhcHBsaWNhdGlvbnMgYXBwbGljYXRpb25zLXdyaXRlIGN1c3RvbWVycyBjdXN0b21lcnMtd3JpdGUgY3VzdG9tZXItdGFncy13cml0ZSBjdXN0b21lci10b2tlbi13cml0ZSBhY2NvdW50cyBhY2NvdW50cy13cml0ZSBjYXJkcyBjYXJkcy13cml0ZSBjYXJkcy1zZW5zaXRpdmUgY2FyZHMtc2Vuc2l0aXZlLXdyaXRlIHRyYW5zYWN0aW9ucyB0cmFuc2FjdGlvbnMtd3JpdGUgYXV0aG9yaXphdGlvbnMgc3RhdGVtZW50cyBwYXltZW50cyBwYXltZW50cy13cml0ZSBwYXltZW50cy13cml0ZS1jb3VudGVycGFydHkgcGF5bWVudHMtd3JpdGUtYWNoLWRlYml0IGNvdW50ZXJwYXJ0aWVzIGNvdW50ZXJwYXJ0aWVzLXdyaXRlIGJhdGNoLXJlbGVhc2VzIGJhdGNoLXJlbGVhc2VzLXdyaXRlIHdlYmhvb2tzIHdlYmhvb2tzLXdyaXRlIGV2ZW50cyBldmVudHMtd3JpdGUgYXV0aG9yaXphdGlvbi1yZXF1ZXN0cyBhdXRob3JpemF0aW9uLXJlcXVlc3RzLXdyaXRlIGNoZWNrLWRlcG9zaXRzIGNoZWNrLWRlcG9zaXRzLXdyaXRlIHJlY2VpdmVkLXBheW1lbnRzIHJlY2VpdmVkLXBheW1lbnRzLXdyaXRlIGRpc3B1dGVzIGNoYXJnZWJhY2tzIGNoYXJnZWJhY2tzLXdyaXRlIHJld2FyZHMgcmV3YXJkcy13cml0ZSIsIm9yZyI6IlNESyIsInNvdXJjZUlwIjoiIiwidXNlclR5cGUiOiJvcmciLCJpc1VuaXRQaWxvdCI6ZmFsc2V9Eqrud74zwXrSVdlBuvYMXLGA4MBFWp6yREbR7CKhlapifchnn3HRczjKIRT9Nkc-C8p7J8e6FZ56TmBB9lRfBg";

        _configuration = configuration.Configuration()
        _configuration.api_key['Authorization'] = access_token
        _configuration.api_key_prefix['Authorization'] = 'Bearer'

        ac = api_client.ApiClient(configuration=_configuration)

    return ac


def create_address(street, city, state, postal_code, country):
    return Address(street=street, city=city, state=state, postal_code=postal_code, country=country)


def create_individual_application_request():
    address = Address(street="1600 Pennsylvania Avenue Northwest", city="Washington", state="CA",
                      postal_code="20500",
                      country="US")
    attr = CreateIndividualApplicationAttributes(FullName("Peter", "Parker"), "jone.doe1@unit-finance.com",
                                                 Phone("1", "2025550108"), "721074426",
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
