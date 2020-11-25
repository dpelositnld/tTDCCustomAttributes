package com.talend.components.TDC.source;

import com.talend.components.TDC.client.TDCAPIClient;
import com.talend.components.TDC.configuration.LoginMapperConfiguration;
import com.talend.components.TDC.service.LoginService;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.http.Response;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;


import javax.annotation.PostConstruct;
import javax.json.JsonObject;
import java.io.Serializable;

public class LoginSource implements Serializable {
    private final LoginMapperConfiguration configuration;
    private final LoginService service;

    private final RecordBuilderFactory recordBuilderFactory;

    private JsonObject result;
    private boolean isTokenEmitted = false;

    private String token;

    public LoginSource(LoginMapperConfiguration configuration, RecordBuilderFactory recordBuilderFactory, LoginService service) {
        this.configuration = configuration;
        this.recordBuilderFactory = recordBuilderFactory;
        this.service = service;
    }

    @PostConstruct
    public void init() {
        Schema.Builder schemaBuilder = recordBuilderFactory.newSchemaBuilder(Schema.Type.RECORD);
        schemaBuilder.withEntry(recordBuilderFactory.newEntryBuilder().withName("token").withType(Schema.Type.STRING).withNullable(false).build());
        schemaBuilder.build();

        token = service.getToken(configuration.getDataSet().getDataStore().getUsername(), configuration.getDataSet().getDataStore().getPassword());
    }

    @Producer
    public Record produces() {
        Record record = null;
        if (!isTokenEmitted) {
            record = recordBuilderFactory.newRecordBuilder()
                    .withString("token", token)
                    .build();
            isTokenEmitted = true;
        }

        return record;
    }



}