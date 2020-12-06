package com.talend.components.TDC.source;

import com.talend.components.TDC.configuration.TDCAttributesInputConfiguration;
import com.talend.components.TDC.configuration.TDCAttributesOutputConfiguration;
import com.talend.components.TDC.configuration.TDCInputConfiguration;
import com.talend.components.TDC.service.AuthenticationService;
import com.talend.components.TDC.service.TDCAttributesService;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import javax.annotation.PostConstruct;
import java.io.Serializable;

public class TDCAttributesInputSource implements Serializable {
    final RecordBuilderFactory recordBuilderFactory;
    final TDCAttributesService service;

    private String token;

    public TDCAttributesInputSource(RecordBuilderFactory recordBuilderFactory, TDCAttributesService service) {
        this.recordBuilderFactory = recordBuilderFactory;
        this.service = service;
    }

    @PostConstruct
    public void init(){
    }

    @Producer
    public Record produces() {
        Record record = null;
        return record;
    }
}
