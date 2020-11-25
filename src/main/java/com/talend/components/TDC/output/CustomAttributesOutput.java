package com.talend.components.TDC.output;

import static org.talend.sdk.component.api.component.Icon.IconType.CUSTOM;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.json.JsonObject;

import com.talend.components.TDC.client.TDCAPIClient;
import com.talend.components.TDC.configuration.CustomAttributesOutputConfiguration;
import com.talend.components.TDC.dataset.LoginDataset;
import com.talend.components.TDC.service.LoginService;
import com.talend.components.TDC.service.LogoutService;
import org.json.JSONArray;
import org.json.JSONObject;
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

import com.talend.components.TDC.service.CustomAttributesService;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.http.Response;

@Version(1) // default version is 1, if some configuration changes happen between 2 versions you can add a migrationHandler
@Icon(value = CUSTOM, custom = "CustomAttributesOutput") // icon is located at src/main/resources/icons/CustomAttributesOutput.svg
@Processor(name = "CustomAttributesOutput", family = "TDC")
@Documentation("TODO fill the documentation for this processor")
public class CustomAttributesOutput implements Serializable {
    private final CustomAttributesOutputConfiguration configuration;
    private final CustomAttributesService service;

    private LoginDataset dataset;

    List<CustomAttributesOutputConfiguration.TDCAttribute> TDCAttributes;

    public CustomAttributesOutput(@Option("configuration")
                                  final CustomAttributesOutputConfiguration configuration,
                                  final CustomAttributesService service) {

        this.configuration = configuration;
        this.service = service;
        this.dataset = configuration.getDataSet();
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
            @Input final Record defaultInput) {
        service.setAttributes(
                configuration.isUseExistingSession(),
                configuration.getDataSet().getDataStore().getUsername(),
                configuration.getDataSet().getDataStore().getPassword(),
                configuration.getToken(),
                defaultInput,
                configuration.getTDCObjectID(),
                configuration.getTDCAttributes()
                );
    }

    @AfterGroup
    public void afterGroup() {
        // symmetric method of the beforeGroup() executed after the chunk processing
        // Note: if you don't need it you can delete it
    }

    @PreDestroy
    public void release() {
        // this is the symmetric method of the init() one,
        // release potential connections you created or data you cached
        // Note: if you don't need it you can delete it
    }
}