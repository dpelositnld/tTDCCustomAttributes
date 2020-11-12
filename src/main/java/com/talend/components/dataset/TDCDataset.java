package com.talend.components.dataset;

import java.io.Serializable;

import com.talend.components.datastore.TDCDatastore;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@DataSet("TDCDataset")
@GridLayout({
    // the generated layout put one configuration entry per line,
    // customize it as much as needed
    @GridLayout.Row({ "datastore" })
})
@Documentation("TODO fill the documentation for this configuration")
public class TDCDataset implements Serializable {
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private TDCDatastore datastore;

    public TDCDatastore getDatastore() {
        return datastore;
    }

    public TDCDataset setDatastore(TDCDatastore datastore) {
        this.datastore = datastore;
        return this;
    }
}