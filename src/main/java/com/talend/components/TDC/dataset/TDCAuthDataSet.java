package com.talend.components.TDC.dataset;

import java.io.Serializable;

import com.talend.components.TDC.datastore.BasicAuthDataStore;

import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@Data
@DataSet("TDCAuthDataSet")
@GridLayout({
        @GridLayout.Row({ "dataStore" })
})
@Documentation("TODO fill the documentation for this configuration")
public class TDCAuthDataSet implements Serializable {
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private BasicAuthDataStore dataStore;
}