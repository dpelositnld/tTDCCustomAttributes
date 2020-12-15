package com.talend.components.TDC.dataset;

import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;
import java.util.List;

@Data
@GridLayout({
        @GridLayout.Row({ "configurationPath" }),
        @GridLayout.Row({ "profiles" }),
        @GridLayout.Row({ "attributes" }),
        @GridLayout.Row({ "categories" })
})
public class MQLQueryBuilder implements Serializable {
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    @Suggestable(value = "loadTDCConfigurations", parameters = { "../../dataStore" })
    //@Required
    private String configurationPath;

    @Option
    @ActiveIf(target = "../../queryConfiguratorType", value = "ADVANCED")
    @Suggestable(value = "loadTDCProfiles", parameters = { "../../dataStore" })
    //@Required
    @Documentation("TODO fill the documentation for this parameter")
    private List<String> profiles;

    @Option
    @Suggestable(value = "loadAttributes", parameters = { "../../dataStore", "../../queryConfiguratorType", "profiles" })
    //@Required
    @Documentation("")
    private List<String> attributes;

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    @Suggestable(value = "loadTDCCategories", parameters = { "../../dataStore" })
    //@Required
    private List<String> categories;
}
