package com.talend.components.TDC.source;

import com.talend.components.TDC.client.TDCAPIClient;
import com.talend.components.TDC.configuration.LogoutMapperConfiguration;
import com.talend.components.TDC.service.LoginService;
import com.talend.components.TDC.service.LogoutService;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.http.Response;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import javax.annotation.PostConstruct;
import javax.json.JsonObject;
import java.io.Serializable;

public class LogoutSource  implements Serializable {
    private final LogoutMapperConfiguration configuration;
    private final LogoutService service;

    private final RecordBuilderFactory recordBuilderFactory;

    private JsonObject response;
    private boolean recordConsumed = false;

    public LogoutSource(LogoutMapperConfiguration configuration, RecordBuilderFactory recordBuilderFactory, LogoutService service) {
        this.configuration = configuration;
        this.recordBuilderFactory = recordBuilderFactory;
        this.service = service;
    }

    @PostConstruct
    public void init() {
        Schema.Builder schemaBuilder = recordBuilderFactory.newSchemaBuilder(Schema.Type.RECORD);
        schemaBuilder
                .withEntry(recordBuilderFactory.newEntryBuilder().withName("logoutStatus").withType(Schema.Type.STRING).withNullable(false).build())
                .withEntry(recordBuilderFactory.newEntryBuilder().withName("logoutError").withType(Schema.Type.STRING).withNullable(false).build())
                ;
        schemaBuilder.build();

        response = service.logout(configuration.getToken());
    }

    @Producer
    public Record produces() {
        Record record = null;

        String result = response.getJsonString("result").getString();
        String recordStatus = "";
        String recordMessage = "";

        if (recordConsumed)
            return null;
        else {
            if (result != null) {
                recordStatus = "SUCCESS";
                recordConsumed = true;
            } else {
                JsonObject jsonError = response.getJsonObject("error");
                recordStatus = "FAILED";
                recordMessage = "Error Code: \"" + jsonError.getString("errorCode") + "\" Error Message: \"" + jsonError.getString("message") + "\"";
            }

            record = recordBuilderFactory.newRecordBuilder()
                    .withString("logoutStatus", recordStatus)
                    .withString("logoutError", recordMessage)
                    .build();

            recordConsumed = true;
        }
        return record;
    }
}
