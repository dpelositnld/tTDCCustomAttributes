package com.talend.components.TDC.dataset;

import com.talend.components.TDC.configuration.LogoutConfiguration;
import com.talend.components.TDC.datastore.BasicAuthDataStore;
import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

@Data
@DataSet("TDCOutputDataSet")
@GridLayout({
        @GridLayout.Row({ "dataStore" }),
        @GridLayout.Row({ "isUseExistingSession" }),
        @GridLayout.Row({ "token" }),
        @GridLayout.Row({ "operationType" })
})
@Documentation("TODO fill the documentation for this configuration")
public class TDCOutputDataSet implements Serializable {
    public enum OperationType {
        SetAttributes
    }

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private BasicAuthDataStore dataStore;

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private boolean isUseExistingSession = false;

    @Option
    @ActiveIf(target = "isUseExistingSession", value = "true")
    @Documentation("TODO fill the documentation for this parameter")
    private String token;

    @Option
    @Documentation("")
    OperationType operationType = OperationType.SetAttributes;
}