package com.talend.components.dataset;

import java.io.Serializable;

import com.talend.components.datastore.TDCBasicAuthDataStore;

import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@Data
@DataSet("TDCLoginDataset")
@GridLayout({
    // the generated layout put one configuration entry per line,
    // customize it as much as needed
    @GridLayout.Row({ "dataStore" })
})
@Documentation("TODO fill the documentation for this configuration")
public class TDCLoginDataset implements Serializable {
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private TDCBasicAuthDataStore dataStore;
}