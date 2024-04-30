package org.openapitools.client;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.*;
import org.openapitools.client.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.openapitools.client.TestHelpers.CreateApplicationRequest;

public class ApplicationTests {
    UnitApi unitApi = null;

    @BeforeAll
    void init() {
        String access_token = System.getenv("access_token");
        ApiClient cl = new ApiClient();
        cl.setBearerToken(access_token);
        unitApi = new UnitApi(cl);
    }

    @Test
    public void GetApplicationListApiTest() throws ApiException {
        ExecuteFilterParameter filter = new ExecuteFilterParameter();
        ListPageParametersObject page = new  ListPageParametersObject();
        page.setLimit(20);
        page.setOffset(3);

        ArrayList statuses = new ArrayList();
        statuses.add(ExecuteFilterParameter.StatusEnum.APPROVED);
        statuses.add(ExecuteFilterParameter.StatusEnum.AWAITINGDOCUMENTS);
        filter.setQuery("John");
        filter.setStatus(statuses);

        UnitListApplicationsResponse response = unitApi.getApplications(page, filter, null);
        assert response.getData().size() <= 20;
    }

    @Test
    public void GetApplicationListWithFilterApiTest() throws ApiException {
        ExecuteFilterParameter filter = new ExecuteFilterParameter();
        ListPageParametersObject page = new  ListPageParametersObject();
        page.setLimit(20);
        page.setOffset(3);

        ArrayList statuses = new ArrayList();
        statuses.add(ExecuteFilterParameter.StatusEnum.APPROVED);
        filter.setQuery("John");
        filter.setStatus(statuses);

        UnitListApplicationsResponse response = unitApi.getApplications(page, filter ,null);
        assert response.getData().size() != 0;

        response.getData().forEach(x -> {
            if(!x.getType().equals("individualApplication"))
                return;

            IndividualApplication individualApp = (IndividualApplication)x;
            String status = individualApp.getAttributes().getStatus().getValue();
            assert status.equals("Approved");
        });
    }

    @Test
    public void GetApplicationApiTest() throws ApiException {
        UnitListApplicationsResponse response = unitApi.getApplications(null, null, null);
        assert response.getData().size() != 0;

        response.getData().forEach(x -> {
            try {
                UnitApplicationResponseWithIncluded app = unitApi.getApplicationById(x.getId(), null);
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
        ExecuteFilterParameter filters = new ExecuteFilterParameter();
        UnitListApplicationsResponse response = unitApi.getApplications(null, null, null);
        assert response.getData().size() != 0;

        response.getData().forEach(x -> {
            try {
                if(!x.getType().equals("individualApplication"))
                    return;

                IndividualApplication individualApp = (IndividualApplication)x;
                String status = individualApp.getAttributes().getStatus().getValue();
                if(!(status.equals("Approved") || status.equals("PendingReview") || status.equals("AwaitingDocuments")))
                    return;

                PatchIndividualApplication body = new PatchIndividualApplication();
                PatchIndividualApplicationAttributes attributes = new PatchIndividualApplicationAttributes();
                attributes.setOccupation(Occupation.ARCHITECTORENGINEER);
                body.setAttributes(attributes);

                UpdateApplicationData d = new UpdateApplicationData(body);
                UpdateApplication ua = new UpdateApplication();
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
        UnitCreateApplicationResponse res = unitApi.createApplication(CreateApplicationRequest());
        assert res.getData().getType().equals("individualApplication");
    }

    @Test
    public void CreateDocumentForApplicationApiTest() throws ApiException {
        UnitCreateApplicationResponse res = unitApi.createApplication(CreateApplicationRequest());
        assert res.getData().getType().equals("individualApplication");

        UnitDocumentResponse document = unitApi.create(res.getData().getId());
        assert document.getData().getType().equals("document");
    }

    @Test
    public void GetApplicationDocumentsApiTest() throws ApiException {
        GetListApplicationsApi api = new GetListApplicationsApi();

        UnitListApplicationsResponse response = api.execute(null, null, null);
        assert response.getData().size() != 0;
    }

    @Test
    public void ListDocumentsApiTest() throws ApiException {


        UnitListApplicationsResponse response = unitApi.getApplications(null, null, null);
        assert response.getData().size() != 0;

        response.getData().forEach(x -> {
            try {
                List<Document> documents = unitApi.getApplicationDocuments(x.getId()).getData();
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
        Path path = Paths.get("file_path");
        byte[] data = Files.readAllBytes(path);

        UnitDocumentResponse response = unitApi.uploadApplicationDocumentFile("applicationId", "documentId", data);

        assert response.getData().getType().equals("document");
    }
}