package com.talend.components.TDC.input;

import com.talend.components.TDC.configuration.TDCLoginInputConfiguration;
import com.talend.components.TDC.configuration.TDCLogoutInputConfiguration;
import com.talend.components.TDC.service.AuthenticationService;
import com.talend.components.TDC.source.LogoutSource;
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
@PartitionMapper(name = "Logout", family = "TDC")
@Documentation("TODO fill the documentation for this processor")
public class TDCLogoutInput implements Serializable {
    private final TDCLogoutInputConfiguration configuration;
    private final AuthenticationService service;
    private final RecordBuilderFactory recordBuilderFactory;

    public TDCLogoutInput(@Option("configuration") final TDCLogoutInputConfiguration configuration,
                         final RecordBuilderFactory recordBuilderFactory,
                         final AuthenticationService service) {
        this.configuration = configuration;
        this.recordBuilderFactory = recordBuilderFactory;
        this.service = service;
    }

    @PostConstruct
    public void init() {
        service.getClient().base(configuration.getDataSet().getDataStore().getEndpoint());
    }

    @Assessor
    public long estimateSize() {
        return 1L;
    }

    @Split
    public List<TDCLogoutInput> split(@PartitionSize final long bundles) {
        return Collections.singletonList(this);
    }

    @Emitter
    public LogoutSource create() {
        return new LogoutSource(configuration, recordBuilderFactory, service);
    }
}
