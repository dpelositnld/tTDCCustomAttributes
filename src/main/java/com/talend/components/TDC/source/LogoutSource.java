package com.talend.components.TDC.source;

import com.talend.components.TDC.configuration.TDCLoginInputConfiguration;
import com.talend.components.TDC.configuration.TDCLogoutInputConfiguration;
import com.talend.components.TDC.service.AuthenticationService;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import javax.annotation.PostConstruct;
import javax.json.JsonObject;
import java.io.Serializable;

public class LogoutSource implements Serializable {
    final TDCLogoutInputConfiguration configuration;
    final RecordBuilderFactory recordBuilderFactory;
    final AuthenticationService service;

    private JsonObject response;
    private boolean recordConsumed = false;

    public LogoutSource(TDCLogoutInputConfiguration configuration, RecordBuilderFactory recordBuilderFactory, AuthenticationService service) {
        this.configuration = configuration;
        this.recordBuilderFactory = recordBuilderFactory;
        this.service = service;
    }

    @PostConstruct
    public void init() {
        String token = configuration.getDataSet().getDataStore().getToken();
        if (token != null && !token.isEmpty())
            response = service.logout(token);
    }

    @Producer
    public Record produces() {
        Record record = null;
        String recordStatus = "";
        String recordMessage = "";

        if (recordConsumed)
            return null;

        if (response != null) {
            String result = response.getJsonString("result").getString();

            if (result != null) {
                recordStatus = "Logout successful";
                recordMessage = "Token: " + configuration.getDataSet().getDataStore().getToken();
            } else {
                JsonObject jsonError = response.getJsonObject("error");
                recordStatus = "Logout failed";
                recordMessage = "Error Code: \"" + jsonError.getString("errorCode") + "\" Error Message: \"" + jsonError.getString("message") + "\"";
            }
            recordConsumed = true;
        }

        record = recordBuilderFactory.newRecordBuilder()
                .withString("logoutStatus", recordStatus)
                .withString("logoutError", recordMessage)
                .build();

        return record;
    }
}
