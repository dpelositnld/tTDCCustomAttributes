package com.talend.components.TDC.service;

import com.talend.components.TDC.dataset.LoginDataset;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.api.service.schema.DiscoverSchema;

@Service
public class LogoutService {
    @DiscoverSchema
    public Schema guessSchema(LoginDataset dataSet, final RecordBuilderFactory recordBuilderFactory) {
        Schema.Builder schemaBuilder = recordBuilderFactory.newSchemaBuilder(Schema.Type.RECORD);
        schemaBuilder
                .withEntry(recordBuilderFactory.newEntryBuilder().withName("logoutStatus").withType(Schema.Type.STRING).withNullable(false).build())
                .withEntry(recordBuilderFactory.newEntryBuilder().withName("logoutError").withType(Schema.Type.STRING).withNullable(false).build())
        ;
        return schemaBuilder.build();
    }
}
