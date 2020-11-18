package com.talend.components.input;

import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.*;
import org.talend.sdk.component.api.meta.Documentation;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static org.talend.sdk.component.api.component.Icon.IconType.CUSTOM;

@Version(1) // default version is 1, if some configuration changes happen between 2 versions you can add a migrationHandler
@Icon(value = CUSTOM, custom = "tTDCCustomAttributesOutput") // icon is located at src/main/resources/icons/tTDCCustomAttributesOutput.svg
@PartitionMapper(name = "Login")
@Documentation("TODO fill the documentation for this processor")
public class TTDCLoginInput implements Serializable {
    private final TTDCLoginInputConfiguration configuration;
    private final TTDCLoginClient loginClient;

    public TTDCLoginInput(@Option("configuration") final TTDCLoginInputConfiguration configuration,
                          final TTDCLoginClient loginClient) {
        this.configuration = configuration;
        this.loginClient = loginClient;
    }

    @PostConstruct
    public void init() {
        loginClient.base(configuration.getDatastore().getTDC_Endpoint());
    }

    @Assessor
    public long estimateSize() {
        return 1L;
    }

    @Split
    public List<TTDCLoginInput> split(@PartitionSize final long bundles) {
        return Collections.singletonList(this);
    }

    @Emitter
    public TTDCLoginSource create() {
        return new TTDCLoginSource(configuration, loginClient);
    }
}
