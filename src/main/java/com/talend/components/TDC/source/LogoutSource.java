package com.talend.components.TDC.source;

import com.talend.components.TDC.configuration.TDCInputConfiguration;
import com.talend.components.TDC.service.AuthenticationService;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import javax.annotation.PostConstruct;
import javax.json.JsonObject;

public class LogoutSource extends TDCInputSource {
    private JsonObject response;
    private boolean recordConsumed = false;

    public LogoutSource(TDCInputConfiguration configuration, RecordBuilderFactory recordBuilderFactory, AuthenticationService service) {
        super(configuration, recordBuilderFactory, service);
    }

    @PostConstruct
    public void init() {
        String token = configuration.getDataSet().getLogoutConfiguration().getToken();
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
                recordStatus = "SUCCESS";
            } else {
                JsonObject jsonError = response.getJsonObject("error");
                recordStatus = "FAILED";
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
