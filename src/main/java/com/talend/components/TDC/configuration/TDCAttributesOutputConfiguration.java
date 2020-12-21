package com.talend.components.TDC.configuration;

import java.io.Serializable;
import java.util.List;

import com.talend.components.TDC.dataset.TDCAttributesDataSet;

import com.talend.components.TDC.service.TDCAttributesService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.BuiltInSuggestable;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.constraint.Uniques;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

// IMPORTANT: do not change the order o layout TDCObjectID and TDCAttributes. There is a kind of bug that happens otherwise
@Data
@GridLayout({
        @GridLayout.Row({ "dataSet" }),
        @GridLayout.Row({ "TDCObjectIDField" }),
        @GridLayout.Row({ "TDCAttributes" })
})
@Documentation("TODO fill the documentation for this configuration")
public class TDCAttributesOutputConfiguration implements Serializable {
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    TDCAttributesDataSet dataSet;

    @Option
    @Required
    @BuiltInSuggestable(value = BuiltInSuggestable.Name.INCOMING_SCHEMA_ENTRY_NAMES)
    @Suggestable(TDCAttributesService.INCOMING_PATHS_DYNAMIC)
    @Documentation("")
    String TDCObjectIDField;

    @Option
    @Required
    @Uniques
    @Documentation("")
    List<TDCAttribute> TDCAttributes;

    @Data
    @GridLayout({
            @GridLayout.Row({ "field" }),
            @GridLayout.Row({ "attribute" })
    })
    public static class TDCAttribute implements Serializable {

        @Option
        @BuiltInSuggestable(value = BuiltInSuggestable.Name.INCOMING_SCHEMA_ENTRY_NAMES)
        @Suggestable(TDCAttributesService.INCOMING_PATHS_DYNAMIC)
        @Required
        @Documentation("")
        private String field;

        @Option
        //@Required
        @Suggestable(value = "loadChosenAttributes", parameters = { "../../dataSet" })
        @Documentation("")
        private String attribute;
    }
}