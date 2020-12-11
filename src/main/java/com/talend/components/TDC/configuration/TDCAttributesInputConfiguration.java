package com.talend.components.TDC.configuration;

import com.talend.components.TDC.dataset.TDCAttributesDataSet;
import com.talend.components.TDC.dataset.TDCInputDataSet;
import com.talend.components.TDC.input.TDCAttributesInput;
import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

@Data
@GridLayout({
        // the generated layout put one configuration entry per line,
        // customize it as much as needed
        @GridLayout.Row({ "dataSet" })
})
public class TDCAttributesInputConfiguration implements Serializable {
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private TDCAttributesDataSet dataSet;
}