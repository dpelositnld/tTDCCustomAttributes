package com.talend.components.TDC.service;

import com.talend.components.TDC.dataset.LoginDataset;
import com.talend.components.TDC.datastore.BasicAuthDataStore;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.api.service.schema.DiscoverSchema;

@Service
public class LoginService {
    @DiscoverSchema
    public Schema guessSchema(LoginDataset dataSet, final RecordBuilderFactory recordBuilderFactory) {
        Schema.Builder schemaBuilder = recordBuilderFactory.newSchemaBuilder(Schema.Type.RECORD);
        schemaBuilder.withEntry(recordBuilderFactory.newEntryBuilder().withName("token").withType(Schema.Type.STRING).withNullable(false).build());
        return schemaBuilder.build();
    }

    @HealthCheck
    public HealthCheckStatus testConnection(BasicAuthDataStore dataStore) {

        if (dataStore == null || dataStore.getTDC_username().equals("invalidtest")) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, "Connection not ok, datastore can't be null");
        }
        return new HealthCheckStatus(HealthCheckStatus.Status.OK, "Connection ok");
    }
}
