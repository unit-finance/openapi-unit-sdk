package unit.java.sdk;

import static unit.java.sdk.AccountTests.CreateDepositAccount;
import static unit.java.sdk.TestHelpers.GenerateUnitApiClient;
import static unit.java.sdk.CustomerTests.CreateIndividualCustomer;

import java.io.InputStream;

import org.junit.Test;

import unit.java.sdk.api.UnitApi;
import unit.java.sdk.model.DepositAccount;
import unit.java.sdk.model.ListPageParameters;
import unit.java.sdk.model.Statement;
import unit.java.sdk.model.UnitStatementsResponse;

public class StatementTests {
    UnitApi unitApi = GenerateUnitApiClient();
    
    @Test
    public void GetStatementsApiTest() throws ApiException {
        ListPageParameters params = new ListPageParameters();
        params.setLimit(20);
        UnitStatementsResponse res = unitApi.getStatementsList(params, null, null);
        res.getData().forEach(statement -> {
                assert statement.getType().equals(Statement.TypeEnum.ACCOUNT_STATEMENT_DTO);
        });
    }

    @Test
    public void GetStatementHtmlAndPdfApiTest() throws ApiException {
        ListPageParameters params = new ListPageParameters();
        params.setLimit(1);
        UnitStatementsResponse res = unitApi.getStatementsList(params, null, null);
        res.getData().forEach(statement -> {
            try {
                assert statement.getType().equals(Statement.TypeEnum.ACCOUNT_STATEMENT_DTO);
                String html = unitApi.getStatementHtml(statement.getId());
                assert html != null;
                InputStream pdfInputStream = unitApi.getStatementPdf(statement.getId());
                assert pdfInputStream != null;
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void GetBankVerificationPdfApiTest() throws ApiException {
        DepositAccount account = CreateDepositAccount(unitApi, CreateIndividualCustomer(unitApi));
        InputStream res = unitApi.getStatementBankPdf(account.getId());
        assert res != null;
    }
}
