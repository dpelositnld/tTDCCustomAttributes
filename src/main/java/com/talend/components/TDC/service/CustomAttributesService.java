package com.talend.components.TDC.service;

import com.talend.components.TDC.client.TDCAPIClient;
import com.talend.components.TDC.configuration.CustomAttributesOutputConfiguration;
import com.talend.components.TDC.dataset.BasicAuthDataSet;
import lombok.Data;
import org.json.JSONArray;
import org.json.JSONObject;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.asyncvalidation.AsyncValidation;
import org.talend.sdk.component.api.service.asyncvalidation.ValidationResult;
import org.talend.sdk.component.api.service.completion.DynamicValues;
import org.talend.sdk.component.api.service.completion.SuggestionValues;
import org.talend.sdk.component.api.service.completion.Suggestions;
import org.talend.sdk.component.api.service.completion.Values;
import org.talend.sdk.component.api.service.http.Response;

import javax.json.JsonObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

@Data
@Service
public class CustomAttributesService {
    @Service
    TDCAPIClient client;

    @Service
    LoginService loginService;

    @Service
    LogoutService logoutService;

    // you can put logic here you can reuse in components
    @Suggestions("loadModules")
    public SuggestionValues loadModules(@Option final BasicAuthDataSet dataset) {
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

    public JsonObject setAttributes(
        boolean isUseExistingSession,
        String username,
        String password,
        String token,
        Record record,
        String TDCObjectID,
        List<CustomAttributesOutputConfiguration.TDCAttribute> TDCAttributes){
        JsonObject responseBody;

        if (!isUseExistingSession)
            token = loginService.getToken(username, password);

        JSONObject payload = buildSetAttributesTDCRestBody(record, TDCObjectID, TDCAttributes);

        final Response<JsonObject> response = client.setAttributes("application/json", token, payload);
        if (response.status() == 200) {
            responseBody = response.body();
        } else
            throw new RuntimeException(response.error(String.class));

        if (isUseExistingSession)
            logoutService.logout(token);

        return responseBody;
    }

    private JSONObject buildSetAttributesTDCRestBody(Record record, String TDCObjectID, List<CustomAttributesOutputConfiguration.TDCAttribute> TDCAttributes) {
        String message;
        JSONObject jsonSetAttributes = new JSONObject();

        JSONArray jsonValues = new JSONArray();

        Schema schema = record.getSchema();
        List<Schema.Entry> entries = schema.getEntries();

        for (Schema.Entry entry: entries) {
            String name = entry.getName();
            String value = record.getString(name);

            if (isTDCAttribute(name, TDCAttributes)) {
                JSONObject jsonValue = new JSONObject();
                JSONObject jsonAttributeType = new JSONObject();

                jsonAttributeType.put("type", "CUSTOM_ATTRIBUTE");
                jsonAttributeType.put("name", name);

                jsonValue.put("attributeType", jsonAttributeType);
                jsonValue.put("value", value);
                jsonValues.put(jsonValue);
            }

        }

        jsonSetAttributes.put("id", record.getString(TDCObjectID));
        jsonSetAttributes.put("values", jsonValues);
        jsonSetAttributes.put("comment", "");

        return jsonSetAttributes;
    }

    private boolean isTDCAttribute(String name, List<CustomAttributesOutputConfiguration.TDCAttribute> TDCAttributes) {
        boolean isAttribute = false;
        for (CustomAttributesOutputConfiguration.TDCAttribute attr : TDCAttributes) {
            if (attr.getName().equals(name)) {
                isAttribute = true;
                break;
            }
        }
        return isAttribute;
    }
}