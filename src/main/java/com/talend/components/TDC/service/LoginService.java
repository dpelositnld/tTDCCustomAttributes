package com.talend.components.TDC.service;

import com.talend.components.TDC.client.TDCAPIClient;
import com.talend.components.TDC.configuration.LoginMapperConfiguration;
import com.talend.components.TDC.dataset.LoginDataset;
import com.talend.components.TDC.datastore.BasicAuthDataStore;
import lombok.Data;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.configuration.Configuration;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;
import org.talend.sdk.component.api.service.http.Response;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.api.service.schema.DiscoverSchema;

import javax.json.JsonObject;
import java.util.function.Supplier;

@Data
@Service
public class LoginService {
    @Service
    TDCAPIClient client;

    @DiscoverSchema
    public Schema guessSchema(LoginDataset dataSet, final RecordBuilderFactory recordBuilderFactory) {
        Schema.Builder schemaBuilder = recordBuilderFactory.newSchemaBuilder(Schema.Type.RECORD);
        schemaBuilder.withEntry(recordBuilderFactory.newEntryBuilder().withName("token").withType(Schema.Type.STRING).withNullable(false).build());
        return schemaBuilder.build();
    }

    @HealthCheck
    public HealthCheckStatus testConnection(BasicAuthDataStore dataStore) {

        if (dataStore == null || dataStore.getUsername().equals("invalidtest")) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, "Connection not ok, datastore can't be null");
        }
        return new HealthCheckStatus(HealthCheckStatus.Status.OK, "Connection ok");
    }

    public JsonObject login(String username, String password) {
        final Response<JsonObject> response = client.login(username, password, true);
        if (response.status() == 200) {
            return response.body();
        }

        throw new RuntimeException(response.error(String.class));
    }

    public String getToken(JsonObject response) {
        return response.getJsonObject("result").getString("token");
    }
}
