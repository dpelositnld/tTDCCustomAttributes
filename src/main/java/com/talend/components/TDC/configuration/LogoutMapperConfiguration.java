package com.talend.components.TDC.configuration;

import com.talend.components.TDC.dataset.LoginDataset;
import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataSet;
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
    private LoginDataset dataSet;

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String token;
}
