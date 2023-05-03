import unittest

from e2e_tests.helpers.helpers import create_api_client
from swagger_client import GetListStatementsApi, GetStatementPDFApi, GetStatementHTMLApi


class TestStatementApi(unittest.TestCase):
    """StatementAPI unit test stubs"""

    def setUp(self):
        self.api_client = create_api_client()

    def tearDown(self):
        pass

    def test_list_and_get_statements(self):
        statements = GetListStatementsApi(self.api_client).execute().data

        for s in statements:
            assert s.type == "accountStatementDTO"

            pdf_statement = GetStatementPDFApi(self.api_client).execute(s.id)
            assert "PDF" in pdf_statement

            # account_id = s.relationships["account"].id
            # pdf_response = client.statements.get_bank_verification(account_id).data

            html_statement = GetStatementHTMLApi(self.api_client).execute(s.id)
            assert "<!DOCTYPE html>" in html_statement

    if __name__ == '__main__':
        unittest.main()

