package com.talend.components.TDC.output;

import static org.talend.sdk.component.api.component.Icon.IconType.CUSTOM;

import java.io.*;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.talend.components.TDC.configuration.TDCAttributesOutputConfiguration;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.AfterGroup;
import org.talend.sdk.component.api.processor.BeforeGroup;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Input;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.api.record.Record;

import com.talend.components.TDC.service.TDCAttributesService;
import org.talend.sdk.component.api.record.Schema;

@Version(1) // default version is 1, if some configuration changes happen between 2 versions you can add a migrationHandler
@Icon(value = CUSTOM, custom = "TDCAttributesOutput") // icon is located at src/main/resources/icons/TDCAttributesOutput.svg
@Processor(name = "Output", family = "TDC")
@Documentation("TODO fill the documentation for this processor")
public class TDCAttributesOutput implements Serializable {
    private final TDCAttributesOutputConfiguration configuration;
    private final TDCAttributesService service;

    List<TDCAttributesOutputConfiguration.TDCAttribute> TDCAttributes;

    public TDCAttributesOutput(@Option("customAttributesConfiguration")
                                  final TDCAttributesOutputConfiguration configuration,
                               final TDCAttributesService service) {

        this.configuration = configuration;
        this.service = service;
    }

    @PostConstruct
    public void init() {
        service.getClient().base(configuration.getDataSet().getDataStore().getEndpoint());
    }

    @BeforeGroup
    public void beforeGroup() {
        // if the environment supports chunking this method is called at the beginning if a chunk
        // it can be used to start a local transaction specific to the backend you use
        // Note: if you don't need it you can delete it
    }

    @ElementListener
    public void onNext(
            @Input final Record record) {

        service.setAttributes(
                configuration.getDataSet().isUseExistingSession(),
                configuration.getDataSet().getDataStore().getUsername(),
                configuration.getDataSet().getDataStore().getPassword(),
                configuration.getDataSet().getToken(),
                record,
                configuration.getTDCObjectID(),
                configuration.getTDCAttributes()
                );
    }

    @AfterGroup
    public void afterGroup() {

    }

    @PreDestroy
    public void release() {
        // this is the symmetric method of the init() one,
        // release potential connections you created or data you cached
        // Note: if you don't need it you can delete it
    }
}