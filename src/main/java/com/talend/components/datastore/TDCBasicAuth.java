package com.talend.components.datastore;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Checkable;
import org.talend.sdk.component.api.configuration.action.Validable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.meta.Documentation;

@DataStore("TDCBasicAuth")
@Checkable
@GridLayout({
        @GridLayout.Row({ "TDC_Endpoint" }),
        @GridLayout.Row({ "TDC_username" }),
        @GridLayout.Row({ "TDC_password" }),
        @GridLayout.Row({ "isUseProxy" }),
        @GridLayout.Row({ "proxyAddress", "proxyPort" })
})
@Documentation("TODO fill the documentation for this configuration")
public class TDCBasicAuth implements Serializable {
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

    public String getTDC_Endpoint() {
        return TDC_Endpoint;
    }

    public String getTDC_username() {
        return TDC_username;
    }

    public String getTDC_password() {
        return TDC_password;
    }

    public TDCBasicAuth setTDC_Endpoint(String TDC_Endpoint) {
        this.TDC_Endpoint = TDC_Endpoint;
        return this;
    }

    public TDCBasicAuth setTDC_username(String TDC_username) {
        this.TDC_username = TDC_username;
        return this;
    }

    public TDCBasicAuth setTDC_password(String TDC_password) {
        this.TDC_password = TDC_password;
        return this;
    }

    public boolean isUseProxy() {
        return isUseProxy;
    }

    public String getProxyAddress() {
        return proxyAddress;
    }

    public int getProxyPort() {
        return proxyPort;
    }
}