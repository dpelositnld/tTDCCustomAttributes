package com.talend.components.TDC.configuration;

import com.talend.components.TDC.dataset.BasicAuthDataSet;
import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

@Data
@GridLayout({
        @GridLayout.Row({ "dataSet" }),
        @GridLayout.Row({ "token" })
})
@Documentation("TODO fill the documentation for this configuration")
public class LogoutMapperConfiguration implements Serializable {
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private BasicAuthDataSet dataSet;

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String token;
}
