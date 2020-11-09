package com.talend.components.output;

import java.io.Serializable;

import com.talend.components.dataset.CustomDataset;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@GridLayout({
    // the generated layout put one configuration entry per line,
    // customize it as much as needed
    @GridLayout.Row({ "dataset" })
})
@Documentation("TODO fill the documentation for this configuration")
public class TTMCCustomAttributesOutputConfiguration implements Serializable {
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private CustomDataset dataset;

    public CustomDataset getDataset() {
        return dataset;
    }

    public TTMCCustomAttributesOutputConfiguration setDataset(CustomDataset dataset) {
        this.dataset = dataset;
        return this;
    }
}