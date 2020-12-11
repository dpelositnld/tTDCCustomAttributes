package com.talend.components.TDC.datastore;

import java.io.Serializable;

import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Checkable;
import org.talend.sdk.component.api.configuration.action.Validable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.meta.Documentation;

@Data
@DataStore("BasicAuthDataStore")
@Checkable
@GridLayout({
        @GridLayout.Row({ "endpoint" }),
        @GridLayout.Row({ "username" }),
        @GridLayout.Row({ "password" }),
        @GridLayout.Row({ "isUseExistingSession" }),
        @GridLayout.Row({ "token" })
})
@Documentation("TODO fill the documentation for this configuration")
public class BasicAuthDataStore implements Serializable {
    @Option
    @Required
    @Validable("url")
    @Documentation("TODO fill the documentation for this parameter")
    private String endpoint;

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String username;

    @Option
    @Credential
    @Documentation("TODO fill the documentation for this parameter")
    private String password;

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private boolean isUseExistingSession = false;

    @Option
    @ActiveIf(target = "isUseExistingSession", value = "true")
    @Documentation("TODO fill the documentation for this parameter")
    private String token;
}