package com.talend.components.TDC.dataset;

import com.talend.components.TDC.datastore.BasicAuthDataStore;
import com.talend.components.TDC.service.TDCAttributesService;
import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.BuiltInSuggestable;
import org.talend.sdk.component.api.configuration.action.Proposable;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.condition.ActiveIfs;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.constraint.Uniques;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.TextArea;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

@Data
@DataSet("TDCAttributesDataSet")
@GridLayout(names = GridLayout.FormType.MAIN, value = {
        @GridLayout.Row({ "dataStore" }),
        @GridLayout.Row({ "queryConfiguratorType" }),
        @GridLayout.Row({ "queryBuilder" }),
        @GridLayout.Row({ "queryEditor" })
})
@GridLayout(names = GridLayout.FormType.ADVANCED, value = {
        @GridLayout.Row({ "numOfRecords" })
})
@Documentation("TODO fill the documentation for this configuration")
public class TDCAttributesDataSet implements Serializable {
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private BasicAuthDataStore dataStore;

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    @Proposable("loadMQLQueryConfiguratorTypes")
    @Required
    private String queryConfiguratorType;

    @Option
    @ActiveIfs(operator = ActiveIfs.Operator.OR, value = {
            @ActiveIf(target = "queryConfiguratorType", value = "SIMPLE"),
            @ActiveIf(target = "queryConfiguratorType", value = "ADVANCED")
    })
    @Documentation("TODO fill the documentation for this parameter")
    private MQLQueryBuilder queryBuilder = new MQLQueryBuilder();

    @Option
    @ActiveIf(target = "queryConfiguratorType", value = "CUSTOM")
    @Documentation("TODO fill the documentation for this parameter")
    private MQLQueryEditor queryEditor = new MQLQueryEditor();

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private int numOfRecords = 1000;
}