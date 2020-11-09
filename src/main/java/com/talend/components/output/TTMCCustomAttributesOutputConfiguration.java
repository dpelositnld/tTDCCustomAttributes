package com.talend.components.output;

import java.io.Serializable;
import java.util.List;

import com.talend.components.dataset.CustomDataset;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Proposable;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.configuration.ui.widget.Structure;
import org.talend.sdk.component.api.meta.Documentation;

@GridLayout({
    // the generated layout put one configuration entry per line,
    // customize it as much as needed
        @GridLayout.Row({ "dataset" }),
        @GridLayout.Row({ "TDC_Endpoint" }),
        @GridLayout.Row({ "TDC_username" }),
        @GridLayout.Row({ "TDC_password" }),
        @GridLayout.Row({ "TDC_ObjectID" }),
        @GridLayout.Row({ "TDC_Attributes" })

})
@Documentation("TODO fill the documentation for this configuration")
public class TTMCCustomAttributesOutputConfiguration implements Serializable {
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private CustomDataset dataset;

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String TDC_Endpoint = "http://localhost:11480/MM/rest/v1";

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String TDC_username = "Administrator";

    @Option
    @Credential
    @Documentation("TODO fill the documentation for this parameter")
    private String TDC_password = "Administrator";

    @Option
    @Structure
    @Documentation("")
    private List<String> TDCAttributes;

    @Option

    String TDC_ObjectID = "AAAEBADDDHj3dSV6ezKACGvnt3Kwrh3pAA";

    public String getTDC_Endpoint() {
        return TDC_Endpoint;
    }

    public String getTDC_username() {
        return TDC_username;
    }

    public String getTDC_password() {
        return TDC_password;
    }

    public CustomDataset getDataset() {
        return dataset;
    }

    public TTMCCustomAttributesOutputConfiguration setDataset(CustomDataset dataset) {
        this.dataset = dataset;
        return this;
    }
}