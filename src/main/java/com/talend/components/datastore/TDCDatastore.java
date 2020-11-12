package com.talend.components.datastore;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Checkable;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.meta.Documentation;

@DataStore("TDCDatastore")
@Checkable
@GridLayout({
        @GridLayout.Row({ "TDC_Endpoint" }),
        @GridLayout.Row({ "TDC_username" }),
        @GridLayout.Row({ "TDC_password" })
})
@Documentation("TODO fill the documentation for this configuration")
public class TDCDatastore implements Serializable {
    @Option
    @Required
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

    public String getTDC_Endpoint() {
        return TDC_Endpoint;
    }

    public String getTDC_username() {
        return TDC_username;
    }

    public String getTDC_password() {
        return TDC_password;
    }

    public TDCDatastore setTDC_Endpoint(String TDC_Endpoint) {
        this.TDC_Endpoint = TDC_Endpoint;
        return this;
    }

    public TDCDatastore setTDC_username(String TDC_username) {
        this.TDC_username = TDC_username;
        return this;
    }

    public TDCDatastore setTDC_password(String TDC_password) {
        this.TDC_password = TDC_password;
        return this;
    }
}