package com.talend.components.TDC.input;

import com.talend.components.TDC.client.TDCAPIClient;
import com.talend.components.TDC.configuration.LogoutMapperConfiguration;
import com.talend.components.TDC.service.LogoutService;
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
@Icon(value = CUSTOM, custom = "CustomAttributesOutput") // icon is located at src/main/resources/icons/CustomAttributesOutput.svg
@PartitionMapper(name = "Logout", family = "TDC")
@Documentation("TODO fill the documentation for this processor")
public class LogoutMapper implements Serializable {
    private final LogoutMapperConfiguration configuration;
    private final LogoutService service;
    private final RecordBuilderFactory recordBuilderFactory;

    public LogoutMapper(@Option("logoutConfiguration") final LogoutMapperConfiguration configuration,
                        final RecordBuilderFactory recordBuilderFactory,
                        final LogoutService service) {
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
    public List<LogoutMapper> split(@PartitionSize final long bundles) {
        return Collections.singletonList(this);
    }

    @Emitter
    public LogoutSource create() {
        return new LogoutSource(configuration, recordBuilderFactory, service);
    }
}
