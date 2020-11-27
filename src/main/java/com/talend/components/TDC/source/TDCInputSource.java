package com.talend.components.TDC.source;

import com.talend.components.TDC.configuration.TDCInputConfiguration;
import com.talend.components.TDC.service.AuthenticationService;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import javax.annotation.PostConstruct;
import java.io.Serializable;

public abstract class TDCInputSource implements Serializable {
    protected final TDCInputConfiguration configuration;
    protected final AuthenticationService service;
    protected final RecordBuilderFactory recordBuilderFactory;

    public TDCInputSource(TDCInputConfiguration configuration, RecordBuilderFactory recordBuilderFactory, AuthenticationService service) {
        this.configuration = configuration;
        this.recordBuilderFactory = recordBuilderFactory;
        this.service = service;
    }

    @PostConstruct
    abstract public void init();

    @Producer
    abstract public Record produces();
}
