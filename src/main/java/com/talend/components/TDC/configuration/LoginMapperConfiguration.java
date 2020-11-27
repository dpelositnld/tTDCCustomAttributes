package com.talend.components.TDC.configuration;

import java.io.Serializable;

import com.talend.components.TDC.dataset.BasicAuthDataSet;
import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@Data
@GridLayout({
        // the generated layout put one configuration entry per line,
        // customize it as much as needed
        @GridLayout.Row({ "dataSet" })
})
@Documentation("TODO fill the documentation for this configuration")
public class LoginMapperConfiguration implements Serializable {

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private BasicAuthDataSet dataSet;
}