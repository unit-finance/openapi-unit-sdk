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
    @BeforeAll
    static void init() {
        String access_token = System.getenv("access_token");
        ApiClient cl = new ApiClient();
        cl.setBearerToken(access_token);
        Configuration.setDefaultApiClient(cl);
    }

    @Test
    public void GetApplicationListApiTest() throws ApiException {
        GetListApplicationsApi api = new GetListApplicationsApi();
        
        ListPageParametersObject page = new  ListPageParametersObject();
        page.setLimit(20);
        page.setOffset(3);

        UnitListApplicationsResponse response = api.execute(page, null, null);
        assert response.getData().size() <= 20;
    }

    @Test
    public void GetApplicationListWithFilterApiTest() throws ApiException {
        GetListApplicationsApi api = new GetListApplicationsApi();
        ExecuteFilterParameter filter = new ExecuteFilterParameter();
        ListPageParametersObject page = new  ListPageParametersObject();
        page.setLimit(20);
        page.setOffset(3);

        ArrayList statuses = new ArrayList();
        statuses.add(ExecuteFilterParameter.StatusEnum.APPROVED);
        filter.setQuery("John");
        filter.setStatus(statuses);

//        UnitListApplicationsResponse response = api.execute(null,null, statuses,
//                null, null , null, null );
        UnitListApplicationsResponse response = api.execute(page, filter ,null);
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
        GetListApplicationsApi api = new GetListApplicationsApi();

        UnitListApplicationsResponse response = api.execute(null, null, null);
        assert response.getData().size() != 0;

        GetApplicationApi getApi = new GetApplicationApi();

        response.getData().forEach(x -> {
            try {
                UnitApplicationResponseWithIncluded app = getApi.execute(x.getId(), null);
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
        GetListApplicationsApi api = new GetListApplicationsApi();
        ExecuteFilterParameter filters = new ExecuteFilterParameter();
        // filters.status(new ArrayList<StatusEnum>(){new ExecuteFilterParameter.StatusEnum[]{ExecuteFilterParameter.StatusEnum.APPROVED}})
        UnitListApplicationsResponse response = api.execute(null, null, null);
        assert response.getData().size() != 0;

        UpdateApplicationApi updateApi = new UpdateApplicationApi();

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

                UnitApplicationResponseWithIncluded app = updateApi.execute(x.getId(), ua);
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
        CreateApplicationApi apiClient = new CreateApplicationApi();
        UnitCreateApplicationResponse res = apiClient.execute(CreateApplicationRequest());
        assert res.getData().getType().equals("individualApplication");
    }

    @Test
    public void CreateDocumentForApplicationApiTest() throws ApiException {
        CreateApplicationApi apiClient = new CreateApplicationApi();
        UnitCreateApplicationResponse res = apiClient.execute(CreateApplicationRequest());
        assert res.getData().getType().equals("individualApplication");

        CreateADocumentForAnApplicationApi createApi = new CreateADocumentForAnApplicationApi();
        UnitDocumentResponse document = createApi.execute(res.getData().getId());
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
        GetListOfDocumentsApi api = new GetListOfDocumentsApi();
        GetListApplicationsApi listApplicationsApi = new GetListApplicationsApi();

        UnitListApplicationsResponse response = listApplicationsApi.execute(null, null, null);
        assert response.getData().size() != 0;

        response.getData().forEach(x -> {
            try {
                List<Document> documents = api.execute(x.getId()).getData();
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

        UploadAPngDocumentForAnApplicationApi api = new UploadAPngDocumentForAnApplicationApi();
        UnitDocumentResponse response = api.execute("applicationId", "documentId", data);

        assert response.getData().getType().equals("document");
    }
}