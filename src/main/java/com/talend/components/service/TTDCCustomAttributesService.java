package com.talend.components.service;

import com.talend.components.dataset.CustomDataset;
import com.talend.components.datastore.CustomDatastore;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Proposable;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.completion.DynamicValues;
import org.talend.sdk.component.api.service.completion.SuggestionValues;
import org.talend.sdk.component.api.service.completion.Suggestions;
import org.talend.sdk.component.api.service.completion.Values;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;
import org.talend.sdk.component.api.service.schema.DiscoverSchema;

import java.util.Arrays;

@Service
public class TTDCCustomAttributesService {

    // you can put logic here you can reuse in components
    @Suggestions("loadModules")
    public SuggestionValues loadModules(@Option final CustomDataset dataset) {
        //dataset.
        return new SuggestionValues(false,
                Arrays
                        .asList(new SuggestionValues.Item("1", "Delete"), new SuggestionValues.Item("2", "Insert"),
                                new SuggestionValues.Item("3", "Update")));
    }

    @DynamicValues("valuesProvider")
    public Values proposals() {
        return new Values(Arrays
                .asList(new Values.Item("1", "Delete"), new Values.Item("2", "Insert"),
                        new Values.Item("3", "Update")));
    }

    @Suggestions("suggestionsProvider")
    public SuggestionValues suggestions() {
        SuggestionValues suggestions = new SuggestionValues();
        suggestions.setItems(Arrays
                .asList(new SuggestionValues.Item("1", "Delete"), new SuggestionValues.Item("2", "Insert"),
                        new SuggestionValues.Item("3", "Update")));
        return suggestions;
    }

    @Suggestions("builtInSuggestable")
    public SuggestionValues builtInSuggestions() {
        SuggestionValues suggestions = new SuggestionValues();
        suggestions.setItems(Arrays
                .asList(new SuggestionValues.Item("1", "Delete"), new SuggestionValues.Item("2", "Insert"),
                        new SuggestionValues.Item("3", "Update")));
        return suggestions;
    }

    /*
    @DiscoverSchema
    public Schema guessSchema(@Option MyDataset dataset, final MyDataLoaderService myCustomService) {
        return myCustomService.loadFirstData().getRecord().getSchema();
    }
*/
    @HealthCheck
    public HealthCheckStatus testConnection(CustomDatastore datastore) {

        if (datastore == null || datastore.getTDC_username().equals("invalidtest")) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, "Connection not ok, datastore can't be null");
        }
        return new HealthCheckStatus(HealthCheckStatus.Status.OK, "Connection ok");
    }


}