package unit.java.sdk;

import static unit.java.sdk.TestHelpers.GenerateUnitApiClient;

import org.junit.Test;
import unit.java.sdk.api.UnitApi;
import unit.java.sdk.model.ListPageParameters;
import unit.java.sdk.model.UnitWebhookResponse;
import unit.java.sdk.model.UnitWebhooksListResponse;
import unit.java.sdk.model.Webhook;

public class WebhookTests {
    UnitApi unitApi = GenerateUnitApiClient();

    @Test
    public void GetWebhooksApiTest() throws ApiException {
        ListPageParameters params = new ListPageParameters();
        params.setLimit(20);
        UnitWebhooksListResponse res = unitApi.getWebhooksList(params, null, null);
        res.getData().forEach(webhook -> {
            try {
                UnitWebhookResponse webhookRes = unitApi.getWebhook(webhook.getId());
                assert webhookRes.getData().getType().equals(Webhook.TypeEnum.WEBHOOK);
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
