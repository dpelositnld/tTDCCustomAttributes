package com.talend.components.service;

import com.talend.components.dataset.TDCDataset;
import com.talend.components.datastore.TDCDatastore;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.asyncvalidation.AsyncValidation;
import org.talend.sdk.component.api.service.asyncvalidation.ValidationResult;
import org.talend.sdk.component.api.service.completion.DynamicValues;
import org.talend.sdk.component.api.service.completion.SuggestionValues;
import org.talend.sdk.component.api.service.completion.Suggestions;
import org.talend.sdk.component.api.service.completion.Values;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

@Service
public class TTDCCustomAttributesService {

    // you can put logic here you can reuse in components
    @Suggestions("loadModules")
    public SuggestionValues loadModules(@Option final TDCDataset dataset) {
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
    @AsyncValidation("url")
    public ValidationResult doValidate(@Option("url") String url) {
        // validate the property
        try {
            new URL(url); // important: in a real component don't validate urls this way
        } catch (MalformedURLException e) {
            return new ValidationResult(ValidationResult.Status.KO, e.getMessage());
        }
        return new ValidationResult(ValidationResult.Status.OK, "Valid URL");
    }

    @HealthCheck
    public HealthCheckStatus testConnection(TDCDatastore datastore) {

        if (datastore == null || datastore.getTDC_username().equals("invalidtest")) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, "Connection not ok, datastore can't be null");
        }
        return new HealthCheckStatus(HealthCheckStatus.Status.OK, "Connection ok");
    }


}