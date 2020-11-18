package com.talend.components.input;

import java.io.Serializable;

import com.talend.components.datastore.TDCBasicAuth;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@GridLayout({
        // the generated layout put one configuration entry per line,
        // customize it as much as needed
        @GridLayout.Row({ "datastore" })
})
@Documentation("TODO fill the documentation for this configuration")
public class TTDCLoginInputConfiguration implements Serializable {

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private TDCBasicAuth datastore;

    public TDCBasicAuth getDatastore() {
        return datastore;
    }


}