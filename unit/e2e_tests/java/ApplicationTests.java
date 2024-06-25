package unit.java.sdk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static unit.java.sdk.TestHelpers.GenerateCreateApplicationRequest;
import static unit.java.sdk.TestHelpers.GenerateUnitApiClient;
import unit.java.sdk.api.UnitApi;
import unit.java.sdk.model.ApplicationDocument;
import unit.java.sdk.model.DefaultContentType;
import unit.java.sdk.model.GetApplicationsListFilterParameter;
import unit.java.sdk.model.IndividualApplication;
import unit.java.sdk.model.ListPageParameters;
import unit.java.sdk.model.Occupation;
import unit.java.sdk.model.UnitApplicationResponseWithIncluded;
import unit.java.sdk.model.UnitCreateApplicationResponse;
import unit.java.sdk.model.UnitDocumentResponse;
import unit.java.sdk.model.UnitListApplicationsResponse;
import unit.java.sdk.model.UnitListDocumentsResponse;
import unit.java.sdk.model.UpdateApplicationRequest;
import unit.java.sdk.model.UpdateApplicationRequestData;
import unit.java.sdk.model.UpdateIndividualApplication;
import unit.java.sdk.model.UpdateIndividualApplicationAttributes;
import unit.java.sdk.model.UploadApplicationDocumentContentType;

public class ApplicationTests {
    UnitApi unitApi = GenerateUnitApiClient();

    @Test
    public void GetApplicationListApiTest() throws ApiException {
        UnitListApplicationsResponse response = unitApi.getApplicationsList(null, null, null);
        assert !response.getData().isEmpty();
    }

    @Test
    public void GetApplicationListWithFilterApiTest() throws ApiException {
        GetApplicationsListFilterParameter filter = new GetApplicationsListFilterParameter();
        ListPageParameters page = new  ListPageParameters();
        page.setLimit(20);
        page.setOffset(3);

        ArrayList<GetApplicationsListFilterParameter.StatusEnum> statuses = new ArrayList();
        statuses.add(GetApplicationsListFilterParameter.StatusEnum.APPROVED);
        filter.setQuery("John");
        filter.setStatus(statuses);

        UnitListApplicationsResponse response = unitApi.getApplicationsList(page, filter ,null);
        assert response.getData().size() <= 20;
    }

    @Test
    public void GetApplicationApiTest() throws ApiException {
        UnitListApplicationsResponse response = unitApi.getApplicationsList(null, null, null);
        assert !response.getData().isEmpty();

        response.getData().forEach(x -> {
            try {
                UnitApplicationResponseWithIncluded app = unitApi.getApplication(x.getId(), null);
                assert app.getData().getId().equals(x.getId());
                assert app.getData().getType().toLowerCase()
                        .equals(app.getData().getClass().getSimpleName().toLowerCase());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void UpdateApplicationApiTest() throws ApiException {
        UnitListApplicationsResponse response = unitApi.getApplicationsList(null, null, null);
        assert !response.getData().isEmpty();

        response.getData().forEach(x -> {
            try {
                if(!x.getType().equals("individualApplication"))
                    return;

                IndividualApplication individualApp = (IndividualApplication)x;
                String status = individualApp.getAttributes().getStatus().getValue();
                if(!(status.equals("Approved") || status.equals("PendingReview") || status.equals("AwaitingDocuments")))
                    return;

                UpdateIndividualApplication body = new UpdateIndividualApplication();
                UpdateIndividualApplicationAttributes attributes = new UpdateIndividualApplicationAttributes();
                attributes.setOccupation(Occupation.ARCHITECTORENGINEER);
                body.setAttributes(attributes);

                UpdateApplicationRequestData d = new UpdateApplicationRequestData(body);
                UpdateApplicationRequest ua = new UpdateApplicationRequest();
                ua.data(d);

                UnitApplicationResponseWithIncluded app = unitApi.updateApplication(x.getId(), ua);
                assert app.getData().getId().equals(x.getId());
                assert app.getData().getType().toLowerCase()
                        .equals(app.getData().getClass().getSimpleName().toLowerCase());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }


    @Test
    public void CreateApplicationApiTest() throws ApiException {
        UnitCreateApplicationResponse res = unitApi.createApplication(GenerateCreateApplicationRequest());
        assert res.getData().getType().equals("individualApplication");
    }

    @Test
    public void CreateAndGetDocumentForApplicationApiTest() throws ApiException {
        UnitCreateApplicationResponse res = unitApi.createApplication(GenerateCreateApplicationRequest());
        assert res.getData().getType().equals("individualApplication");

        UnitDocumentResponse document = unitApi.createApplicationDocument(res.getData().getId(), DefaultContentType.APPLICATION_VND_API_JSON);
        assert document.getData().getType().equals("document");

        UnitListDocumentsResponse response = unitApi.getApplicationDocuments(res.getData().getId());
        assert !response.getData().isEmpty();
    }

    @Test
    public void ListDocumentsApiTest() throws ApiException {
        UnitListApplicationsResponse response = unitApi.getApplicationsList(null, null, null);
        assert !response.getData().isEmpty();

        response.getData().forEach(x -> {
            try {
                List<ApplicationDocument> documents = unitApi.getApplicationDocuments(x.getId()).getData();
                documents.forEach(doc -> {
                   assert doc.getType().equals("document");
                });
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void uploadPngFile() throws ApiException, IOException {
        Path path = Paths.get("./src/test/java/unit/java/sdk/unit_photo.png").toAbsolutePath();
        byte[] data = Files.readAllBytes(path);

        UnitCreateApplicationResponse application = unitApi.createApplication(GenerateCreateApplicationRequest());
        assert application.getData().getType().equals("individualApplication");

        String applicationdId = application.getData().getId();
        UnitDocumentResponse document = unitApi.createApplicationDocument(applicationdId, DefaultContentType.APPLICATION_VND_API_JSON);
        assert document.getData().getType().equals("document");

        UnitDocumentResponse response = unitApi.uploadApplicationDocumentFile(applicationdId, document.getData().getId(), UploadApplicationDocumentContentType.IMAGE_PNG, data);

        assert response.getData().getType().equals("document");
    }
}