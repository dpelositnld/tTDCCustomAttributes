package com.talend.components.TDC.dataset;

import java.io.Serializable;

import com.talend.components.TDC.configuration.LogoutConfiguration;
import com.talend.components.TDC.datastore.BasicAuthDataStore;

import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@Data
@DataSet("TDCInputDataSet")
@GridLayout({
        @GridLayout.Row({ "dataStore" }),
        @GridLayout.Row({ "operationType" }),
        @GridLayout.Row({ "logoutConfiguration" })
})
@Documentation("TODO fill the documentation for this configuration")
public class TDCInputDataSet implements Serializable {
    public enum OperationType {
        Login,
        Logout
    }

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private BasicAuthDataStore dataStore;

    @Option
    @Documentation("")
    OperationType operationType = OperationType.Login;

    @Option
    @ActiveIf(target = "operationType", value = "Logout")
    @Documentation("TODO fill the documentation for this parameter")
    private LogoutConfiguration logoutConfiguration;
}