package com.talend.components.TDC.configuration;

import java.io.Serializable;
import java.util.List;

import com.talend.components.TDC.dataset.BasicAuthDataSet;

import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.BuiltInSuggestable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.constraint.Uniques;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

// IMPORTANT: do not change the order o layout TDCObjectID and TDCAttributes. There is a kind of bug that happens otherwise
@Data
@GridLayout({
        @GridLayout.Row({ "dataSet" }),
        @GridLayout.Row({ "isUseExistingSession" }),
        @GridLayout.Row({ "token" }),
        @GridLayout.Row({ "TDCObjectID" }),
        @GridLayout.Row({ "TDCAttributes" })
})
@Documentation("TODO fill the documentation for this configuration")
public class CustomAttributesOutputConfiguration implements Serializable {
    @Option
    @ActiveIf(target = "isUseExistingSession", value = "false")
    @Documentation("TODO fill the documentation for this parameter")
    private BasicAuthDataSet dataSet;

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private boolean isUseExistingSession = false;

    @Option
    @ActiveIf(target = "isUseExistingSession", value = "true")
    @Documentation("TODO fill the documentation for this parameter")
    private String token;

    @Option
    @Required
    @Uniques
    @Documentation("")
    List<TDCAttribute> TDCAttributes;

    @Option
    @Required
    @BuiltInSuggestable(value = BuiltInSuggestable.Name.INCOMING_SCHEMA_ENTRY_NAMES)
    @Documentation("")
    String TDCObjectID;

    /*
    @Option
    @Suggestable(value = "suggestionsProvider", parameters = { "dataset" })
    @Documentation("")
    String TDC_ObjectID;

    @Option
    @Proposable(value = "valuesProvider")
    @Documentation("")
    String TDC_ObjectID2;
    */

    @Data
    @GridLayout({
            @GridLayout.Row({ "name" }),
            @GridLayout.Row({ "comment" })
    })
    public static class TDCAttribute {
        /*
        @Option
        @Proposable("valuesProvider")
        @Documentation("")
        public String options1;
        */

        @Option
        @BuiltInSuggestable(value = BuiltInSuggestable.Name.INCOMING_SCHEMA_ENTRY_NAMES)
        @Documentation("")
        private String name;

        @Option
        @DefaultValue("")
        @Documentation("")
        private String comment;
    }
}