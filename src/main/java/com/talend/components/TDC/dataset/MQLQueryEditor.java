package com.talend.components.TDC.dataset;

import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.TextArea;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

@Data
@GridLayout({
        @GridLayout.Row({ "MQL" })
})
public class MQLQueryEditor implements Serializable {
    @Option
    @ActiveIf(target = "../../queryConfiguratorType", value = "CUSTOM")
    @TextArea
    @Required
    @DefaultValue("{}")
    @Documentation("")
    private String MQL;
}
