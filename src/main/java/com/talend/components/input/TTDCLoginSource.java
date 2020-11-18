package com.talend.components.input;

import org.talend.sdk.component.api.base.BufferizedProducerSupport;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.service.http.Response;

import javax.annotation.PostConstruct;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.io.Serializable;

public class TTDCLoginSource implements Serializable {
    private final TTDCLoginInputConfiguration configuration;
    private final TTDCLoginClient loginClient;

    private BufferizedProducerSupport<JsonValue> bufferedReader;

    public TTDCLoginSource(TTDCLoginInputConfiguration configuration, TTDCLoginClient loginClient) {
        this.configuration = configuration;
        this.loginClient = loginClient;
    }

    @PostConstruct
    public void init() {
        bufferedReader = new BufferizedProducerSupport<>(() -> {
            JsonObject result = null;
            result = login(configuration.getDatastore().getTDC_username(), configuration.getDatastore().getTDC_password());

            if (result != null) {
                return null;
            }

            return result.getJsonArray("results").iterator();
        });
    }

    @Producer
    public JsonObject produces() {
        final JsonValue next = bufferedReader.next();
        return next == null ? null : next.asJsonObject();
    }

    private JsonObject login(String username, String password) {
        final Response<JsonObject> response = loginClient.login(username, password);
        if (response.status() == 200) {
            return response.body();
        }

        throw new RuntimeException(response.error(String.class));
    }

}