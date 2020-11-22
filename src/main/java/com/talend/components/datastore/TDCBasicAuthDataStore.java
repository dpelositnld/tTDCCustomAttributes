package com.talend.components.datastore;

import java.io.Serializable;

import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Checkable;
import org.talend.sdk.component.api.configuration.action.Validable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.meta.Documentation;

@Data
@DataStore("TDCBasicAuthDataStore")
@Checkable
@GridLayout({
        @GridLayout.Row({ "TDC_Endpoint" }),
        @GridLayout.Row({ "TDC_username" }),
        @GridLayout.Row({ "TDC_password" }),
        @GridLayout.Row({ "isUseProxy" }),
        @GridLayout.Row({ "proxyAddress", "proxyPort" })
})
@Documentation("TODO fill the documentation for this configuration")
public class TDCBasicAuthDataStore implements Serializable {
    @Option
    @Required
    @Validable("url")
    @Documentation("TODO fill the documentation for this parameter")
    private String TDC_Endpoint;

    @Option
    @Required
    @Documentation("TODO fill the documentation for this parameter")
    private String TDC_username;

    @Option
    @Required
    @Credential
    @Documentation("TODO fill the documentation for this parameter")
    private String TDC_password;

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private boolean isUseProxy;

    @Option
    @ActiveIf(target = "isUseProxy", value = "true")
    @Documentation("TODO fill the documentation for this parameter")
    private String proxyAddress;

    @Option
    @ActiveIf(target = "isUseProxy", value = "true")
    @Documentation("TODO fill the documentation for this parameter")
    private int proxyPort;
}