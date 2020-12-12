package com.talend.components.TDC.source;

import com.talend.components.TDC.configuration.TDCAttributesInputConfiguration;
import com.talend.components.TDC.configuration.TDCAttributesOutputConfiguration;
import com.talend.components.TDC.dataset.TDCAttributesDataSet;
import com.talend.components.TDC.service.TDCAttributesService;
import lombok.extern.slf4j.Slf4j;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Assessor;
import org.talend.sdk.component.api.input.Emitter;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import javax.annotation.PostConstruct;
import javax.json.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@Slf4j
public class TDCAttributesInputSource implements Serializable {
    final TDCAttributesInputConfiguration config;
    final RecordBuilderFactory recordBuilderFactory;
    final TDCAttributesService service;

    int objectIdsIdx = 0;
    JsonArray MQLRecords;

    public TDCAttributesInputSource(@Option("configuration") final TDCAttributesInputConfiguration config, final TDCAttributesService service, final RecordBuilderFactory recordBuilderFactory) {
        this.config = config;
        this.recordBuilderFactory = recordBuilderFactory;
        this.service = service;
    }

    @PostConstruct
    public void init(){
        JsonObject response = service.getCustomAttributes(config.getDataSet());
        System.out.println(response.toString());
        MQLRecords = response.getJsonArray("result");
    }

    @Producer
    public Record produces() {
        Record record = null;
        if (objectIdsIdx < MQLRecords.size()) {
            JsonArray MQLRecord = MQLRecords.getJsonObject(objectIdsIdx).getJsonArray("attributes");

            Record.Builder builder = recordBuilderFactory.newRecordBuilder();
            for (ListIterator<JsonValue> it = MQLRecord.listIterator(); it.hasNext(); ) {
                JsonObject attribute = it.next().asJsonObject();
                JsonObject attrTypeJson = attribute.getJsonObject("attributeType");
                String attr = attrTypeJson.getString("name");
                JsonValue value = attribute.get("value");
                builder.withString(attr, value.toString());
            }

            record = builder.build();
         }
        objectIdsIdx++;
        return record;
    }
}
