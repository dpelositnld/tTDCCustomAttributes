package com.talend.components.input;

import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.http.Response;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;


import javax.annotation.PostConstruct;
import javax.json.JsonObject;
import java.io.Serializable;

public class TTDCLoginSource implements Serializable {
    private final TTDCLoginInputConfiguration configuration;
    private final TTDCLoginClient loginClient;

    private JsonObject result;
    private boolean isTokenEmitted = false;

    private final RecordBuilderFactory recordBuilderFactory;

    public TTDCLoginSource(TTDCLoginInputConfiguration configuration, RecordBuilderFactory recordBuilderFactory, TTDCLoginClient loginClient) {
        this.configuration = configuration;
        this.recordBuilderFactory = recordBuilderFactory;
        this.loginClient = loginClient;
    }

    @PostConstruct
    public void init() {
        Schema.Builder schemaBuilder = recordBuilderFactory.newSchemaBuilder(Schema.Type.RECORD);
        schemaBuilder.withEntry(recordBuilderFactory.newEntryBuilder().withName("token").withType(Schema.Type.STRING).withNullable(false).build());
        schemaBuilder.build();

        result = login(configuration.getDataStore().getTDC_username(), configuration.getDataStore().getTDC_password());
        //token = result.getJsonObject("result").getString("token");
        //System.out.println(token);
    }

    @Producer
    public Record produces() {
        //JsonObject output = isTokenEmitted? null: result;
        String token = result.getJsonObject("result").getString("token");
        Record record = null;
        if (!isTokenEmitted) {
            record = recordBuilderFactory.newRecordBuilder()
                    .withString("token", token)
                    .build();
            isTokenEmitted = true;
        }

        return record;
    }

    private JsonObject login(String username, String password) {
        final Response<JsonObject> response = loginClient.login(username, password);
        if (response.status() == 200) {
            return response.body();
        }

        throw new RuntimeException(response.error(String.class));
    }

}