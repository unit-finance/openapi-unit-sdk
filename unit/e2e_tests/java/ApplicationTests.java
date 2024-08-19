package unit.java.sdk;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static unit.java.sdk.TestHelpers.GenerateCreateIndividualApplicationRequest;
import static unit.java.sdk.TestHelpers.GenerateCreateSoleProprietorApplicationRequest;
import static unit.java.sdk.TestHelpers.GenerateCreateBusinessApplicationRequest;
import static unit.java.sdk.TestHelpers.GenerateUnitApiClient;
import unit.java.sdk.api.UnitApi;
import unit.java.sdk.model.AnnualIncome;
import unit.java.sdk.model.Application;
import unit.java.sdk.model.ApplicationDocument;
import unit.java.sdk.model.ApplicationForm;
import unit.java.sdk.model.ApplicationRelationship;
import unit.java.sdk.model.ApplicationRelationshipData;
import unit.java.sdk.model.ApplicationStatus;
import unit.java.sdk.model.BusinessAnnualRevenue;
import unit.java.sdk.model.BusinessApplication;
import unit.java.sdk.model.BusinessOfficer;
import unit.java.sdk.model.CancelApplicationRequest;
import unit.java.sdk.model.CancelApplicationRequestData;
import unit.java.sdk.model.CancelApplicationRequestDataAttributes;
import unit.java.sdk.model.CreateApplicationFormRequest;
import unit.java.sdk.model.CreateApplicationFormRequestData;
import unit.java.sdk.model.CreateApplicationFormRequestDataAttributes;
import unit.java.sdk.model.DefaultContentType;
import unit.java.sdk.model.GetApplicationsListFilterParameter;
import unit.java.sdk.model.IncludedResourceInner;
import unit.java.sdk.model.IndividualApplication;
import unit.java.sdk.model.ListPageParameters;
import unit.java.sdk.model.Occupation;
import unit.java.sdk.model.SoleProprietorshipAnnualRevenue;
import unit.java.sdk.model.UnitApplicationFormResponse;
import unit.java.sdk.model.UnitApplicationFormResponseWithIncluded;
import unit.java.sdk.model.UnitApplicationFormsListResponse;
import unit.java.sdk.model.UnitApplicationResponseWithIncluded;
import unit.java.sdk.model.UnitBeneficialOwnerResponse;
import unit.java.sdk.model.UnitBeneficialOwnerResponseData;
import unit.java.sdk.model.UnitCancelApplicationResponse;
import unit.java.sdk.model.UnitCreateApplicationResponse;
import unit.java.sdk.model.UnitDocumentResponse;
import unit.java.sdk.model.UnitListApplicationsResponse;
import unit.java.sdk.model.UnitListDocumentsResponse;
import unit.java.sdk.model.UpdateApplicationRequest;
import unit.java.sdk.model.UpdateApplicationRequestData;
import unit.java.sdk.model.UpdateBusinessApplication;
import unit.java.sdk.model.UpdateBusinessApplicationAttributes;
import unit.java.sdk.model.UpdateBusinessBeneficialOwner;
import unit.java.sdk.model.UpdateBusinessBeneficialOwnerAttributes;
import unit.java.sdk.model.UpdateBusinessBeneficialOwnerRelationships;
import unit.java.sdk.model.UpdateBusinessBeneficialOwnerRequest;
import unit.java.sdk.model.UpdateBusinessBeneficialOwnerRequestData;
import unit.java.sdk.model.UpdateBusinessOfficer;
import unit.java.sdk.model.UpdateBusinessOfficerAttributes;
import unit.java.sdk.model.UpdateIndividualApplication;
import unit.java.sdk.model.UpdateIndividualApplicationAttributes;
import unit.java.sdk.model.UpdateSoleProprietorApplication;
import unit.java.sdk.model.UpdateSoleProprietorApplicationAttributes;
import unit.java.sdk.model.CreateApplicationFormRequestDataAttributes.AllowedApplicationTypesEnum;
import unit.java.sdk.model.CreateApplicationFormRequestDataAttributes.LangEnum;

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

        ArrayList<GetApplicationsListFilterParameter.StatusEnum> statuses = new ArrayList<GetApplicationsListFilterParameter.StatusEnum>();
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
                if(x == null) return;
                UnitApplicationResponseWithIncluded app = unitApi.getApplication(x.getId(), null);
                assert app.getData().getId().equals(x.getId());
                assert app.getData().getType().toString().toLowerCase()
                        .equals(app.getData().getClass().getSimpleName().toLowerCase());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void UpdateIndividualApplicationApiTest() throws ApiException {
        UnitCreateApplicationResponse res = unitApi.createApplication(GenerateCreateIndividualApplicationRequest(null));
        assert res.getData().getType().equals(unit.java.sdk.model.Application.TypeEnum.INDIVIDUALAPPLICATION);

        IndividualApplication individualApp = (IndividualApplication) res.getData();
        ApplicationStatus status = individualApp.getAttributes().getStatus();
        if(!status.equals(ApplicationStatus.APPROVED)) return;

        UpdateIndividualApplication body = new UpdateIndividualApplication();
        UpdateIndividualApplicationAttributes attributes = new UpdateIndividualApplicationAttributes();
        attributes.setOccupation(Occupation.ARCHITECTORENGINEER);
        body.setAttributes(attributes);

        UpdateApplicationRequestData d = new UpdateApplicationRequestData(body);
        UpdateApplicationRequest ua = new UpdateApplicationRequest();
        ua.data(d);

        UnitApplicationResponseWithIncluded app = unitApi.updateApplication(res.getData().getId(), ua);
        assert app.getData().getId().equals(res.getData().getId());
        assert app.getData().getType().toString().toLowerCase()
                .equals(app.getData().getClass().getSimpleName().toLowerCase());   
        IndividualApplication resApplication = (IndividualApplication) app.getData();        
        assert resApplication.getAttributes().getOccupation().equals(Occupation.ARCHITECTORENGINEER);
    }

    @Test
    public void UpdateSoleProprietorApplicationApiTest() throws ApiException {
        UnitCreateApplicationResponse res = unitApi.createApplication(GenerateCreateSoleProprietorApplicationRequest());
        assert res.getData().getType().equals(unit.java.sdk.model.Application.TypeEnum.INDIVIDUALAPPLICATION);

        IndividualApplication individualApp = (IndividualApplication) res.getData();
        ApplicationStatus status = individualApp.getAttributes().getStatus();
        if(!status.equals(ApplicationStatus.APPROVED)) return;

        UpdateSoleProprietorApplication body = new UpdateSoleProprietorApplication();
        UpdateSoleProprietorApplicationAttributes attributes = new UpdateSoleProprietorApplicationAttributes();
        attributes.setAnnualRevenue(SoleProprietorshipAnnualRevenue.UPTO50K);
        body.setAttributes(attributes);

        UpdateApplicationRequestData d = new UpdateApplicationRequestData(body);
        UpdateApplicationRequest ua = new UpdateApplicationRequest();
        ua.data(d);

        UnitApplicationResponseWithIncluded app = unitApi.updateApplication(res.getData().getId(), ua);
        assert app.getData().getId().equals(res.getData().getId());
        assert app.getData().getType().toString().toLowerCase()
                .equals(app.getData().getClass().getSimpleName().toLowerCase());   
        IndividualApplication resApplication = (IndividualApplication) app.getData(); 

        assert resApplication.getAttributes().getAnnualRevenue().equals(SoleProprietorshipAnnualRevenue.UPTO50K);
    } 

    @Test
    public void UpdateBusinessApplicationApiTest() throws ApiException {
        UnitCreateApplicationResponse res = unitApi.createApplication(GenerateCreateBusinessApplicationRequest());
        assert res.getData().getType().equals(unit.java.sdk.model.Application.TypeEnum.BUSINESSAPPLICATION);

        BusinessApplication businessApp = (BusinessApplication) res.getData();
        ApplicationStatus status = businessApp.getAttributes().getStatus();
        if(!status.equals(ApplicationStatus.APPROVED)) return;

        UpdateBusinessApplication body = new UpdateBusinessApplication();
        UpdateBusinessApplicationAttributes attributes = new UpdateBusinessApplicationAttributes();
        attributes.setAnnualRevenue(BusinessAnnualRevenue.OVER5M);
        attributes.setCountriesOfOperation(businessApp.getAttributes().getCountriesOfOperation());
        body.setAttributes(attributes);

        UpdateApplicationRequestData d = new UpdateApplicationRequestData(body);
        UpdateApplicationRequest ua = new UpdateApplicationRequest();
        ua.setData(d);

        UnitApplicationResponseWithIncluded app = unitApi.updateApplication(res.getData().getId(), ua);
        assert app.getData().getId().equals(res.getData().getId());
        assert app.getData().getType().toString().toLowerCase()
                .equals(app.getData().getClass().getSimpleName().toLowerCase());
        BusinessApplication resApplication = (BusinessApplication) app.getData();
        assert resApplication.getAttributes().getAnnualRevenue().equals(BusinessAnnualRevenue.OVER5M);  
    }

    @Test
    public void UpdateBusinessOfficerApiTest() throws ApiException {
        UnitCreateApplicationResponse res = unitApi.createApplication(GenerateCreateBusinessApplicationRequest());
        assert res.getData().getType().equals(unit.java.sdk.model.Application.TypeEnum.BUSINESSAPPLICATION);

        BusinessApplication businessApp = (BusinessApplication) res.getData();
        ApplicationStatus status = businessApp.getAttributes().getStatus();
        if(!status.equals(ApplicationStatus.APPROVED)) return;

        UpdateBusinessOfficer body = new UpdateBusinessOfficer();
        UpdateBusinessOfficerAttributes attributes = new UpdateBusinessOfficerAttributes();
        BusinessOfficer officer = new BusinessOfficer();
        AnnualIncome income = AnnualIncome.BETWEEN100KAND250K;
        officer.setAnnualIncome(income);
        
        attributes.setOfficer(officer);
        body.setType(UpdateBusinessOfficer.TypeEnum.BUSINESSAPPLICATION);
        body.setAttributes(attributes);

        UpdateApplicationRequestData d = new UpdateApplicationRequestData(body);
        UpdateApplicationRequest ua = new UpdateApplicationRequest();
        ua.setData(d);

        UnitApplicationResponseWithIncluded app = unitApi.updateApplication(res.getData().getId(), ua);
        assert app.getData().getId().equals(res.getData().getId());
        assert app.getData().getType().toString().toLowerCase()
                .equals(app.getData().getClass().getSimpleName().toLowerCase());
        BusinessApplication resApplication = (BusinessApplication) app.getData();
        assert resApplication.getAttributes().getOfficer().getAnnualIncome().equals(income);  
    }

    @Test 
    public void UpdateBusinessBeneficialOwnerApiTest() throws ApiException {
        UnitCreateApplicationResponse res = unitApi.createApplication(GenerateCreateBusinessApplicationRequest());
        assert res.getData().getType().equals(unit.java.sdk.model.Application.TypeEnum.BUSINESSAPPLICATION);

        BusinessApplication businessApp = (BusinessApplication) res.getData();
        ApplicationStatus status = businessApp.getAttributes().getStatus();
        if(!status.equals(ApplicationStatus.APPROVED)) return;

        UpdateBusinessBeneficialOwner body = new UpdateBusinessBeneficialOwner();
        UpdateBusinessBeneficialOwnerAttributes attributes = new UpdateBusinessBeneficialOwnerAttributes();
        AnnualIncome income = AnnualIncome.BETWEEN100KAND250K;
        attributes.setAnnualIncome(income);
        
        body.setType(UpdateBusinessBeneficialOwner.TypeEnum.BENEFICIALOWNER);
        UpdateBusinessBeneficialOwnerRelationships relationships = new UpdateBusinessBeneficialOwnerRelationships();
        ApplicationRelationship applicationRelationship = new ApplicationRelationship();
        ApplicationRelationshipData applicationRelationshipData = new ApplicationRelationshipData();
        applicationRelationshipData.setId(res.getData().getId());
        applicationRelationshipData.setType(ApplicationRelationshipData.TypeEnum.BUSINESSAPPLICATION);
        applicationRelationship.setData(applicationRelationshipData);
        relationships.setApplication(applicationRelationship);
        body.setRelationships(relationships);
        body.setAttributes(attributes);

        UpdateBusinessBeneficialOwnerRequestData data = new UpdateBusinessBeneficialOwnerRequestData(body);
        UpdateBusinessBeneficialOwnerRequest request = new UpdateBusinessBeneficialOwnerRequest();
        request.setData(data);

        UnitApplicationResponseWithIncluded getRes = unitApi.getApplication(res.getData().getId(), null);
        IncludedResourceInner beneficialOwner = getRes.getIncluded().stream().filter(resource -> 
            resource.getType().equals("beneficialOwner")
        ).findFirst().orElse(null);

        assert beneficialOwner != null;
        UnitBeneficialOwnerResponse ownerRes = unitApi.updateBusinessBeneficialOwner(beneficialOwner.getId(), request);
        assert ownerRes.getData().getId().equals(beneficialOwner.getId());
        UnitBeneficialOwnerResponseData resBeneficialOwner = (UnitBeneficialOwnerResponseData) ownerRes.getData();
        assert resBeneficialOwner.getAttributes().getAnnualIncome().equals(income);
    }

    @Test
    public void CreateIndividualApplicationApiTest() throws ApiException {
        UnitCreateApplicationResponse res = unitApi.createApplication(GenerateCreateIndividualApplicationRequest(null));
        assert res.getData().getType().equals(Application.TypeEnum.INDIVIDUALAPPLICATION);
    }

    @Test
    public void CreateSoleProprietorApplicationApiTest() throws ApiException {
        UnitCreateApplicationResponse res = unitApi.createApplication(GenerateCreateSoleProprietorApplicationRequest());
        assert res.getData().getType().equals(Application.TypeEnum.INDIVIDUALAPPLICATION);
    }

    @Test
    public void CreateBusinessApplicationApiTest() throws ApiException {
        UnitCreateApplicationResponse res = unitApi.createApplication(GenerateCreateBusinessApplicationRequest());
        assert res.getData().getType().equals(Application.TypeEnum.BUSINESSAPPLICATION);
    }

    @Test 
    public void CancelApplicationApiTest() throws ApiException {
        UnitCreateApplicationResponse res = unitApi.createApplication(GenerateCreateIndividualApplicationRequest("000000004"));
        Application application = res.getData();
        assert application.getType().equals(Application.TypeEnum.INDIVIDUALAPPLICATION);

        CancelApplicationRequest cancelApplicationRequest = new CancelApplicationRequest();
        CancelApplicationRequestData cancelApplicationRequestData = new CancelApplicationRequestData();
        CancelApplicationRequestDataAttributes cancelApplicationRequestDataAttributes = new CancelApplicationRequestDataAttributes();
        cancelApplicationRequestDataAttributes.setReason("By Org");
        cancelApplicationRequestData.setAttributes(cancelApplicationRequestDataAttributes);
        cancelApplicationRequestData.setType(CancelApplicationRequestData.TypeEnum.APPLICATIONCANCEL);
        cancelApplicationRequest.setData(cancelApplicationRequestData);
        
        
        UnitCancelApplicationResponse cancelRes = unitApi.cancelApplication(application.getId(), cancelApplicationRequest);
        assert cancelRes.getData().getType().equals(Application.TypeEnum.INDIVIDUALAPPLICATION);
    }

    @Test
    public void CreateAndGetDocumentForApplicationApiTest() throws ApiException {
        UnitCreateApplicationResponse res = unitApi.createApplication(GenerateCreateIndividualApplicationRequest(null));
        assert res.getData().getType().equals(Application.TypeEnum.INDIVIDUALAPPLICATION);

        UnitDocumentResponse document = unitApi.createApplicationDocument(res.getData().getId(), DefaultContentType.APPLICATION_VND_API_JSON);
        assert document.getData().getType().equals(ApplicationDocument.TypeEnum.DOCUMENT);

        UnitListDocumentsResponse response = unitApi.getApplicationDocuments(res.getData().getId());
        assert !response.getData().isEmpty();
    }

    @Test
    public void ListDocumentsApiTest() throws ApiException {
        UnitListApplicationsResponse response = unitApi.getApplicationsList(null, null, null);
        assert !response.getData().isEmpty();

        response.getData().forEach(x -> {
            try {
                if(x == null) return;
                List<ApplicationDocument> documents = unitApi.getApplicationDocuments(x.getId()).getData();
                documents.forEach(doc -> {
                   assert doc.getType().equals(ApplicationDocument.TypeEnum.DOCUMENT);
                });
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void CreateApplicationFormApiTest() throws ApiException {
        CreateApplicationFormRequest req = new CreateApplicationFormRequest();
        CreateApplicationFormRequestData data = new CreateApplicationFormRequestData();
        CreateApplicationFormRequestDataAttributes attributes = new CreateApplicationFormRequestDataAttributes();

        List<AllowedApplicationTypesEnum> allowedApplicationTypes = new ArrayList<>();
        allowedApplicationTypes.add(AllowedApplicationTypesEnum.INDIVIDUAL);
        attributes.setAllowedApplicationTypes(allowedApplicationTypes);
        attributes.setLang(LangEnum.EN);
        data.setAttributes(attributes);
        req.setData(data);

        UnitApplicationFormResponse res = unitApi.createApplicationForm(req);
        assert res.getData().getType().equals(ApplicationForm.TypeEnum.APPLICATIONFORM);
    }

    @Test 
    public void ListApplicationFormsApiTest() throws ApiException {
        UnitApplicationFormsListResponse res = unitApi.getApplicationFormsList(null, null, null);
        assert !res.getData().isEmpty();
    }

    @Test 
    public void GetApplicationFormByIdApiTest() throws ApiException {
        ListPageParameters page = new ListPageParameters();
        page.setLimit(20);
        UnitApplicationFormsListResponse res = unitApi.getApplicationFormsList(page, null, null);
        assert !res.getData().isEmpty();

        res.getData().forEach(form -> {
            try { 
                UnitApplicationFormResponseWithIncluded getByIdRes = unitApi.getApplicationForm(form.getId(), null);
                assert getByIdRes.getData().getType().equals(form.getType());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }
}