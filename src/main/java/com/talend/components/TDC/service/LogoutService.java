package com.talend.components.TDC.service;

import com.talend.components.TDC.client.TDCAPIClient;
import com.talend.components.TDC.dataset.BasicAuthDataSet;
import lombok.Data;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.http.Response;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.api.service.schema.DiscoverSchema;

import javax.json.JsonObject;

@Data
@Service
public class LogoutService {
    @Service
    TDCAPIClient client;

    @DiscoverSchema
    public Schema guessSchema(BasicAuthDataSet dataSet, final RecordBuilderFactory recordBuilderFactory) {
        Schema.Builder schemaBuilder = recordBuilderFactory.newSchemaBuilder(Schema.Type.RECORD);
        schemaBuilder
                .withEntry(recordBuilderFactory.newEntryBuilder().withName("logoutStatus").withType(Schema.Type.STRING).withNullable(false).build())
                .withEntry(recordBuilderFactory.newEntryBuilder().withName("logoutError").withType(Schema.Type.STRING).withNullable(false).build())
        ;
        return schemaBuilder.build();
    }

    public JsonObject logout(String token) {
        final Response<JsonObject> response = client.logout("application/json", token);
        if (response.status() == 200) {
            return response.body();
        }

        throw new RuntimeException(response.error(String.class));
    }
}
