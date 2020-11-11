package com.talend.components.output;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.talend.components.dataset.CustomDataset;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.BuiltInSuggestable;
import org.talend.sdk.component.api.configuration.action.Proposable;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.configuration.ui.widget.Structure;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.completion.DynamicValues;
import org.talend.sdk.component.api.service.schema.DiscoverSchema;

@GridLayout({
    // the generated layout put one configuration entry per line,
    // customize it as much as needed
        @GridLayout.Row({ "dataset" }),
        @GridLayout.Row({ "TDCAttributes" }),
        @GridLayout.Row({ "TDC_ObjectID" }),
        @GridLayout.Row({ "TDC_ObjectID2" })
})
@Documentation("TODO fill the documentation for this configuration")
public class TTMCCustomAttributesOutputConfiguration implements Serializable {
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private CustomDataset dataset;

    @Option
    @Documentation("")
    List<MyObject> TDCAttributes;

    @Option
    @Suggestable(value = "suggestionsProvider", parameters = { "dataset" })
    @Documentation("")
    String TDC_ObjectID = "AAAEBADDDHj3dSV6ezKACGvnt3Kwrh3pAA";

    @Option
    @Proposable(value = "valuesProvider")
    @Documentation("")
    String TDC_ObjectID2 = "AAAEBADDDHj3dSV6ezKACGvnt3Kwrh3pAA";

    public CustomDataset getDataset() {
        return dataset;
    }

    /*
    @DiscoverSchema
    public Schema guessSchema(@Option CustomDataset dataset) {
        return myCustomService.loadFirstData().getRecord().getSchema();
    }
    */

    public TTMCCustomAttributesOutputConfiguration setDataset(CustomDataset dataset) {
        this.dataset = dataset;
        //this.TDCAttributes.put("pippo", "pippo");
        //this.TDCAttributes.put("pluto", "pluto");
        //this.TDCAttributes.put("paperino", "paperino");
        return this;
    }

    public static class MyObject {
        @Option
        @Proposable("valuesProvider")
        @Documentation("")
        public String options1;

        @Option
        @BuiltInSuggestable(value = BuiltInSuggestable.Name.INCOMING_SCHEMA_ENTRY_NAMES)
        @Documentation("")
        public String options2;


    }
}