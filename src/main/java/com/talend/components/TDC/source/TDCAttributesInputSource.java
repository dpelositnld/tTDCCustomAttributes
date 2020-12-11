package com.talend.components.TDC.source;

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
@Version
@Icon(value = Icon.IconType.CUSTOM, custom = "TDCAttributesOutput")
@Emitter(name = "Input", family = "TDC")
@Documentation("")
public class TDCAttributesInputSource implements Serializable {
    final TDCAttributesOutputConfiguration config;
    final RecordBuilderFactory recordBuilderFactory;
    final TDCAttributesService service;

    int objectIdsIdx = 0;
    JsonArray MQLRecords;

    public TDCAttributesInputSource(@Option("configuration") final TDCAttributesOutputConfiguration config, final TDCAttributesService service, final RecordBuilderFactory recordBuilderFactory) {
        this.config = config;
        this.recordBuilderFactory = recordBuilderFactory;
        this.service = service;
    }

    @PostConstruct
    public void init(){
        System.out.println("********************TDCAttributesInputSource.init() begin");

        JsonObject response = service.getCustomAttributes(config.getDataSet().getAttributes(), config.getDataSet().getConfigurationPath());
        MQLRecords = response.getJsonObject("result").getJsonArray("attributes");
        System.out.println("********************TDCAttributesInputSource.init() end");
    }

    @Assessor
    public long estimateSize() {
        return 1l;
    }

    @Producer
    public Record produces() {
        Record record = null;
        if (objectIdsIdx < MQLRecords.size()) {
            for (ListIterator<JsonValue> it = MQLRecords.listIterator(); it.hasNext(); ) {
                JsonObject MQLRecord = it.next().asJsonObject();
                JsonObject attrTypeJson = MQLRecord.getJsonObject("attributeType");
                String attr = attrTypeJson.getString("name");
                String value = MQLRecord.getString("value");

                record = recordBuilderFactory.newRecordBuilder().withString(attr, value).build();
            }
        }
        objectIdsIdx++;
        return record;
    }
}
