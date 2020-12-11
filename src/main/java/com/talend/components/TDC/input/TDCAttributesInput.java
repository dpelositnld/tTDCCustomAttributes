package com.talend.components.TDC.input;

import com.talend.components.TDC.configuration.TDCAttributesInputConfiguration;
import com.talend.components.TDC.service.TDCAttributesService;
import com.talend.components.TDC.source.TDCAttributesInputSource;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.*;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import javax.annotation.PostConstruct;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static org.talend.sdk.component.api.component.Icon.IconType.CUSTOM;

@Version(1) // default version is 1, if some configuration changes happen between 2 versions you can add a migrationHandler
@Icon(value = CUSTOM, custom = "TDC") // icon is located at src/main/resources/icons/TDCAttributesOutput.svg
@PartitionMapper(name = "AttributesInput", family = "TDC")
@Documentation("TODO fill the documentation for this processor")
public class TDCAttributesInput implements Serializable {
    private final TDCAttributesInputConfiguration config;
    private final TDCAttributesService service;
    private final RecordBuilderFactory recordBuilderFactory;

    public TDCAttributesInput(@Option("configuration") final TDCAttributesInputConfiguration config,
                              final RecordBuilderFactory recordBuilderFactory,
                              final TDCAttributesService service) {
        this.config = config;
        this.recordBuilderFactory = recordBuilderFactory;
        this.service = service;
    }

    @PostConstruct
    public void init() {
        service.getClient().base(config.getDataSet().getDataStore().getEndpoint());
    }

    @Assessor
    public long estimateSize() {
        return 1L;
    }

    @Split
    public List<TDCAttributesInput> split(@PartitionSize final long bundles) {
        return Collections.singletonList(this);
    }

    @Emitter
    public TDCAttributesInputSource create() {
        return new TDCAttributesInputSource(config, service, recordBuilderFactory);
    }
}
