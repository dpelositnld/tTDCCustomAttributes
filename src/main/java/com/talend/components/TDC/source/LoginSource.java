package com.talend.components.TDC.source;

import com.talend.components.TDC.configuration.TDCInputConfiguration;
import com.talend.components.TDC.service.AuthenticationService;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;


import javax.annotation.PostConstruct;
import javax.json.JsonObject;

public class LoginSource extends TDCInputSource {
    private JsonObject result;
    private boolean isTokenEmitted = false;

    private String token;

    public LoginSource(TDCInputConfiguration configuration, RecordBuilderFactory recordBuilderFactory, AuthenticationService service) {
        super(configuration, recordBuilderFactory, service);
    }

    @PostConstruct
    public void init() {
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