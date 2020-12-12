package com.talend.components.TDC.dataset;

import com.talend.components.TDC.datastore.BasicAuthDataStore;
import com.talend.components.TDC.service.TDCAttributesService;
import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.BuiltInSuggestable;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.constraint.Uniques;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;
import java.util.List;

@Data
@DataSet("TDCAttributesDataSet")
@GridLayout({
        @GridLayout.Row({ "dataStore" }),
        @GridLayout.Row({ "configurationPath" }),
        @GridLayout.Row({ "attributes" })
})
@Documentation("TODO fill the documentation for this configuration")
public class TDCAttributesDataSet implements Serializable {
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private BasicAuthDataStore dataStore;

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    @Suggestable(value = "loadTDCConfigurations", parameters = { "dataStore" })
    @Required
    private String configurationPath;

    @Option
    @Suggestable(value = "loadAttributes", parameters = { "dataStore" })
    @Required
    @Documentation("")
    private List<String> attributes;
}