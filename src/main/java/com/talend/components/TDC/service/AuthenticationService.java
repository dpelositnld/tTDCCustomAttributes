package com.talend.components.TDC.service;

import com.talend.components.TDC.client.TDCAPIClient;
import com.talend.components.TDC.dataset.TDCInputDataSet;
import com.talend.components.TDC.datastore.BasicAuthDataStore;
import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;
import org.talend.sdk.component.api.service.http.Response;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.api.service.schema.DiscoverSchema;

import javax.json.JsonObject;
import java.util.Properties;

@Data
@Service
public class AuthenticationService {
    @Service
    TDCAPIClient client;

    @DiscoverSchema
    public Schema guessSchema(@Option TDCInputDataSet dataSet, final RecordBuilderFactory recordBuilderFactory) {
        Schema schema = null;
        if (dataSet.getOperationType().equals(TDCInputDataSet.OperationType.Login))
            schema = guessSchemaLogin(dataSet, recordBuilderFactory);
        else if (dataSet.getOperationType().equals(TDCInputDataSet.OperationType.Logout))
            schema = guessSchemaLogout(dataSet, recordBuilderFactory);
        return schema;
    }

    /*
    @DynamicValues("operationProvider")
    public Values proposals() {
        return new Values(Arrays
                .asList(new Values.Item("1", "Login"),
                        new Values.Item("2", "Logout")
                        ));
    }
*/
    public JsonObject logout(String token) {
        final Response<JsonObject> response = client.logout("text/plain", token);
        if (response.status() == 200) {
            return response.body();
        }

        throw new RuntimeException(response.error(String.class));
    }

    @HealthCheck
    public HealthCheckStatus testConnection(BasicAuthDataStore dataStore) {
        client.base(dataStore.getEndpoint());
        if (dataStore == null)
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, "Connection not ok, datastore can't be null.");
        else if (dataStore.getUsername().isEmpty() || dataStore.getPassword().isEmpty())
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, "Connection not ok, Please be sure that username and password are properly set.");
        else {
            try {
                getToken(dataStore.getUsername(), dataStore.getPassword());
            } catch (Exception e) {
                return new HealthCheckStatus(HealthCheckStatus.Status.KO, "Connection not ok, Please verify the connection parameters.");
            }
        }
        return new HealthCheckStatus(HealthCheckStatus.Status.OK, "Connection ok");
    }

    public JsonObject login(String username, String password) {
        /*
        Properties systemProps = System.getProperties();
        systemProps.put("javax.net.ssl.keyStorePassword","password");
        systemProps.put("javax.net.ssl.keyStore","c:/keystore.jks");
        systemProps.put("javax.net.ssl.trustStore", "c:/keystore.jks");
        systemProps.put("javax.net.ssl.trustStorePassword","password");
        System.setProperties(systemProps);
        */
        final Response<JsonObject> response = client.login(username, password, true);
        if (response.status() == 200) {
            return response.body();
        }

        throw new RuntimeException(response.error(String.class));
    }

    public String getToken(String username, String password) {
        JsonObject response = login(username, password);
        return response.getJsonObject("result").getString("token");
    }

    private Schema guessSchemaLogin(TDCInputDataSet dataSet, final RecordBuilderFactory recordBuilderFactory) {
        Schema.Builder schemaBuilder = recordBuilderFactory.newSchemaBuilder(Schema.Type.RECORD);
        schemaBuilder.withEntry(recordBuilderFactory.newEntryBuilder().withName("token").withType(Schema.Type.STRING).withNullable(false).build());
        return schemaBuilder.build();
    }

    private Schema guessSchemaLogout(TDCInputDataSet dataSet, final RecordBuilderFactory recordBuilderFactory) {
        Schema.Builder schemaBuilder = recordBuilderFactory.newSchemaBuilder(Schema.Type.RECORD);
        schemaBuilder
                .withEntry(recordBuilderFactory.newEntryBuilder().withName("logoutStatus").withType(Schema.Type.STRING).withNullable(false).build())
                .withEntry(recordBuilderFactory.newEntryBuilder().withName("logoutError").withType(Schema.Type.STRING).withNullable(false).build())
        ;
        return schemaBuilder.build();
    }
}
