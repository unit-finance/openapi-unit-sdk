package unit.java.sdk;

import static unit.java.sdk.TestHelpers.GenerateUnitApiClient;

import java.io.InputStream;

import org.junit.Test;

import unit.java.sdk.api.UnitApi;
import unit.java.sdk.model.ListPageParameters;
import unit.java.sdk.model.TaxForm;
import unit.java.sdk.model.UnitTaxFormResponse;
import unit.java.sdk.model.UnitTaxFormsListResponse;

public class TaxFormTests {
    UnitApi unitApi = GenerateUnitApiClient();

    @Test
    public void GetTaxFormsApiTest() throws ApiException {
        ListPageParameters params = new ListPageParameters();
        params.setLimit(20);
        UnitTaxFormsListResponse res = unitApi.getTaxFormsList(params, null);
        res.getData().forEach(taxForm -> {
            assert taxForm.getType().equals(TaxForm.TypeEnum.TAX_FORM);
            try {
                UnitTaxFormResponse getRes = unitApi.getTaxForm(taxForm.getId());
                assert getRes.getData().getType().equals(TaxForm.TypeEnum.TAX_FORM);
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void GetTaxFormPdfApiTest() throws ApiException {
        ListPageParameters params = new ListPageParameters();
        params.setLimit(1);
        UnitTaxFormsListResponse res = unitApi.getTaxFormsList(params, null);
        res.getData().forEach(taxForm -> {
            assert taxForm.getType().equals(TaxForm.TypeEnum.TAX_FORM);
            try {
                InputStream getRes = unitApi.getTaxFormPdf(taxForm.getId());
                assert getRes != null;
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
