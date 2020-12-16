package com.talend.components.TDC.dataset;

import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.action.Updatable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.condition.ActiveIfs;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;
import java.util.Collections;
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
    @ActiveIf(target = "../../queryConfiguratorType", value = {"SIMPLE,","ADVANCED"})
    @Suggestable(value = "loadTDCConfigurations", parameters = { "../../dataStore" })
    @Documentation("TODO fill the documentation for this parameter")
    //FIXME: @Required create an issue with Lists if there is an ActiveIf and it is not active (it is not possible to set a default value)
    //@Required
    private String configurationPath;

    @Option
    @ActiveIf(target = "../../queryConfiguratorType", value = "ADVANCED")
    @Suggestable(value = "loadTDCProfiles", parameters = { "../../dataStore" })
    //FIXME: @Required create an issue with Lists if there is an ActiveIf and it is not active (it is not possible to set a default value)
    //@Required
    @Documentation("TODO fill the documentation for this parameter")
    private List<String> profiles  = Collections.emptyList();

    @Option
    @ActiveIf(target = "../../queryConfiguratorType", value = {"SIMPLE,","ADVANCED"})
    @Suggestable(value = "loadAttributes", parameters = { "../../dataStore", "../../queryConfiguratorType", "profiles" })
    //FIXME: @Required create an issue with Lists if there is an ActiveIf and it is not active (it is not possible to set a default value)
    //@Required
    @Documentation("")
    private List<String> attributes = Collections.emptyList();

    @Option
    @ActiveIf(target = "../../queryConfiguratorType", value = {"SIMPLE,","ADVANCED"})
    @Documentation("TODO fill the documentation for this parameter")
    @Suggestable(value = "loadTDCCategories", parameters = { "../../dataStore" })
    //FIXME: @Required create an issue with Lists if there is an ActiveIf and it is not active (it is not possible to set a default value)
    //@Required
    private List<String> categories = Collections.emptyList();
}
