package org.openapitools.client;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.CreateApplicationApi;
import org.openapitools.client.api.GetApplicationApi;
import org.openapitools.client.api.GetListApplicationsApi;
import org.openapitools.client.api.UpdateApplicationApi;
import org.openapitools.client.model.*;

import java.time.LocalDate;
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

        UnitListApplicationsResponse response = api.execute(null, null, null);
        assert response.getData().size() != 0;
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
        CreateIndividualApplication createIndividualApplication = new CreateIndividualApplication();
        CreateIndividualApplicationAttributes attr = new CreateIndividualApplicationAttributes();

        FullName fn = new FullName();
        fn.setFirst("Peter");
        fn.setLast("Parker");
        attr.setFullName(fn);

        Address address = new Address();
        address.setStreet("20 Ingram St");
        address.setCity("Forest Hills");
        address.setPostalCode("11375");
        address.setCountry("US");
        address.setState("NY");
        attr.setAddress(address);

        attr.setSsn("721074426");
        attr.setDateOfBirth(LocalDate.parse("2001-08-10"));
        attr.setEmail("peter@oscorp.com");
        Phone p = new Phone();
        p.setNumber("5555555555");
        p.setCountryCode("1");
        attr.setPhone(p);
        attr.setIdempotencyKey("3a1a33be-4e12-4603-9ed0-820922389fb8");
        attr.setOccupation(Occupation.ARCHITECTORENGINEER);

        createIndividualApplication.setAttributes(attr);

        CreateApplication ca = new CreateApplication();
        ca.data(new CreateApplicationData(createIndividualApplication));

        CreateApplicationApi apiClient = new CreateApplicationApi();
        UnitCreateApplicationResponse res = apiClient.execute(ca);
        assert res.getData().getType().equals("individualApplication");
    }

}
