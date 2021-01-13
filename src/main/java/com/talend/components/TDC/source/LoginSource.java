package com.talend.components.TDC.source;

import com.talend.components.TDC.configuration.TDCLoginInputConfiguration;
import com.talend.components.TDC.service.AuthenticationService;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;


import javax.annotation.PostConstruct;
import javax.json.JsonObject;
import java.io.Serializable;

public class LoginSource implements Serializable {
    final TDCLoginInputConfiguration configuration;
    final RecordBuilderFactory recordBuilderFactory;
    final AuthenticationService service;

    private JsonObject result;
    private boolean isTokenEmitted = false;
    private String token;


    public LoginSource(TDCLoginInputConfiguration configuration, RecordBuilderFactory recordBuilderFactory, AuthenticationService service) {
        this.configuration = configuration;
        this.recordBuilderFactory = recordBuilderFactory;
        this.service = service;
    }

    @PostConstruct
    public void init(){
        service.getClient().base(configuration.getDataSet().getDataStore().getEndpoint());
        token = service.getToken(configuration.getDataSet().getDataStore().getUsername(), configuration.getDataSet().getDataStore().getPassword());
        configuration.getDataSet().getDataStore().setToken(token);
    }


    @Producer
    public Record produces() {
        Record record = null;
        if (!isTokenEmitted) {
            record = recordBuilderFactory.newRecordBuilder()
                    .withString("token", configuration.getDataSet().getDataStore().getToken())
                    .build();
            isTokenEmitted = true;
        }

        return record;
    }
}