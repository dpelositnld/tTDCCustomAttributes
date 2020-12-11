package com.talend.components.TDC.service;

import com.talend.components.TDC.client.TDCAPIClient;
import com.talend.components.TDC.configuration.TDCAttributesOutputConfiguration;
import com.talend.components.TDC.dataset.TDCAttributesDataSet;
import com.talend.components.TDC.datastore.BasicAuthDataStore;
import lombok.Data;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.asyncvalidation.AsyncValidation;
import org.talend.sdk.component.api.service.asyncvalidation.ValidationResult;
import org.talend.sdk.component.api.service.completion.DynamicValues;
import org.talend.sdk.component.api.service.completion.SuggestionValues;
import org.talend.sdk.component.api.service.completion.Suggestions;
import org.talend.sdk.component.api.service.completion.Values;
import org.talend.sdk.component.api.service.http.Response;

import javax.json.*;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@Slf4j
@Data
@Service
public class TDCAttributesService {
    public static final String INCOMING_PATHS_DYNAMIC = "INCOMING_PATHS_DYNAMIC";

    @Service
    TDCAPIClient client;

    @Service
    AuthenticationService authService;

    @Suggestions(INCOMING_PATHS_DYNAMIC)
    public SuggestionValues getIncomingPathsSuggestions() {
        return new SuggestionValues(false, Collections.EMPTY_LIST);
    }

    // This INCOMING_PATHS_DYNAMIC service is a flag for inject incoming paths dynamic, won't be called
    @DynamicValues(INCOMING_PATHS_DYNAMIC)
    public Values getIncomingPathsDynamicValues() {
        return new Values(Collections.EMPTY_LIST);
    }
/*
    @Suggestions("loadFieldsFake")
    public SuggestionValues loadFields(@Option final TDCInputDataSet dataset) {

        return new SuggestionValues(false,
                Arrays
                        .asList(new SuggestionValues.Item("Id", "Id"),
                                new SuggestionValues.Item("CustomAttribute1", "CustomAttribute1"),
                                new SuggestionValues.Item("CustomAttribute2", "CustomAttribute2"),
                                new SuggestionValues.Item("CustomAttribute3", "CustomAttribute3")));


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
        List<TDCAttributesOutputConfiguration.TDCAttribute> TDCAttributes){
        JsonObject responseBody;

        if (!isUseExistingSession)
            token = authService.getToken(username, password);

        JsonObject payload = buildSetAttributesTDCRestBody(record, TDCObjectID, TDCAttributes);

        final Response<JsonObject> response = client.setCustomAttributes("application/json", token, payload);
        if (response.status() == 200) {
            responseBody = response.body();
        } else
            throw new RuntimeException(response.error(String.class));

        if (!isUseExistingSession)
            authService.logout(token);

        return responseBody;
    }

    @Suggestions("loadCustomAttributes")
    public SuggestionValues loadCustomAttributes(@Option final BasicAuthDataStore dataStore) {
        SuggestionValues values = new SuggestionValues();
        List<SuggestionValues.Item> items = new ArrayList<SuggestionValues.Item>();

        String username = dataStore.getUsername();
        String password = dataStore.getPassword();

        String encodedPassword = "71fhv%106LJT%115jUF%111VDY%116edM%111fiY%121yoc%122sZB%120HMf%103WRx%122Wuo%117Oqz%120hHQ";

        client.base(dataStore.getEndpoint());

        JSONArray responseObject = getCustomAttributesResponse(username, password, encodedPassword);
        for (int i=0; i < responseObject.length(); i++) {
            JSONObject attrJson = (JSONObject) responseObject.get(i);
            items.add(new SuggestionValues.Item(attrJson.getString("name"), attrJson.getString("name")));
        }
        values.setItems(items);

        return values;
    }

    @Suggestions("loadChosenAttributes")
    public SuggestionValues loadChosenAttributes(@Option final TDCAttributesDataSet dataset) {
        SuggestionValues values = new SuggestionValues();
        List<SuggestionValues.Item> items = new ArrayList<SuggestionValues.Item>();

        for (String attr: dataset.getAttributes()) {
            items.add(new SuggestionValues.Item(attr, attr));
        }

        values.setItems(items);

        return values;
    }

    @Suggestions("loadTDCConfigurations")
    public SuggestionValues loadTDCConfigurationPaths(@Option final BasicAuthDataStore dataStore) {
        SuggestionValues values = new SuggestionValues();
        List<SuggestionValues.Item> items = new ArrayList<SuggestionValues.Item>();

        String TDCObjectTypes = "Configuration";
        String TDCRepositoryPath = "/";

        client.base(dataStore.getEndpoint());

        Response<JsonObject> response = client.repositoryBrowse(dataStore, client, TDCObjectTypes, TDCRepositoryPath);
        JsonObject responseBody;

        if (response.status() == 200)
            responseBody = response.body();
        else
            throw new RuntimeException(response.error(String.class));

        JsonObject resultJson = responseBody.getJsonObject("result");

        List<String> configurationPaths = new ArrayList<String>();

        getConfigurationPaths("", resultJson.getJsonArray("links"), configurationPaths);

        for (String path: configurationPaths) {
            items.add(new SuggestionValues.Item(path, path));
        }
        values.setItems(items);

        return values;
    }

    public JsonObject getCustomAttributes(List<String> attributes, String configurationPath){
        String selectClause = "";
        String fromClause = configurationPath;
        String whereClause = "";
        int pageSize = 100;
        int pageNumber = 1;

        for (String attr: attributes) {
            selectClause = !selectClause.isEmpty()? selectClause + ",": selectClause;
            selectClause += "{" + attr + "}";

            whereClause = !selectClause.isEmpty()? selectClause + " OR ": selectClause;
            selectClause += "{" + attr + "} exists";
        }

        return executeMQL(selectClause, fromClause, whereClause, pageSize, pageNumber);
    }

    public JsonObject executeMQL(String selectClause, String fromClause, String whereClause, int pageSize, int pageNumber){
        JsonObject payload = buildExecuteMQLPayload(selectClause, fromClause, whereClause, pageSize, pageNumber);

        System.out.println("***********************************" + payload.toString());

        JsonObject responseBody;
        Response<JsonObject> response = client.executeMQLQuery(payload);

        if (response.status() == 200)
            responseBody = response.body();
        else
            throw new RuntimeException(response.error(String.class));

        return responseBody;
    }

    private JsonObject buildExecuteMQLPayload(String selectClause, String fromClause, String whereClause, int pageSize, int pageNumber) {
        JsonObjectBuilder jsonPayload = Json.createObjectBuilder();

        jsonPayload.add("select", selectClause);
        jsonPayload.add("from", fromClause);
        jsonPayload.add("where", whereClause);
        jsonPayload.add("pageSize", pageSize);
        jsonPayload.add("pageNumber", pageNumber);

        return jsonPayload.build();
    }

    private void getConfigurationPaths(String parentPath, JsonArray links, List<String> configurationPaths){
        if (links.isEmpty()) {
            configurationPaths.add(parentPath);
        }

        for (int i = 0; i < links.size(); i++){
            JsonObject link = links.getJsonObject(i);

            String objectName = link.getString("objectName");
            String id = link.getString("id");
            String path = parentPath.concat("/" + objectName);
            JsonArray objectLinks = link.getJsonArray("links");

            getConfigurationPaths(path, objectLinks, configurationPaths);
        }
    }

    public JsonObject getEntity(
            boolean isUseExistingSession,
            String username,
            String password,
            String token,
            String objectId,
            boolean includeAttributes){
        JsonObject responseBody;

        if (!isUseExistingSession)
            token = authService.getToken(username, password);

        final Response<JsonObject> response = client.getEntity(
                "application/json",
                token,
                objectId,
                includeAttributes
        );

        if (response.status() == 200) {
            responseBody = response.body();
        } else
            throw new RuntimeException(response.error(String.class));

        if (!isUseExistingSession)
            authService.logout(token);

        return response.body();
    }

    public JSONArray getCustomAttributesResponse(String username, String password, String encodedPassword){
        JSONArray response;

        Map<String, String> params = new HashMap<String, String>();
        params.put("name", username);
        params.put("pwd", password);
        params.put("password", encodedPassword);

        Response<JsonObject> responseLogin = client.loginWithCredentials("application/json", params);
        if (responseLogin.status() == 200) {
            String cookieHeader = (String) (((Collection) responseLogin.headers().get("Set-Cookie")).toArray())[0];
            List<HttpCookie> cookies = HttpCookie.parse(cookieHeader);
            HttpCookie xAuth = cookies.get(0);

            Response<String> responseAttrib = client.listCustomAttributes("application/json", xAuth.toString());

            if (responseLogin.status() == 200)
                response = new JSONArray(responseAttrib.body());
            else
                throw new RuntimeException(responseAttrib.error(String.class));
        } else
            throw new RuntimeException(responseLogin.error(String.class));

        return response;
    }

    private JsonObject buildSetAttributesTDCRestBody(Record record, String TDCObjectID, List<TDCAttributesOutputConfiguration.TDCAttribute> TDCAttributes) {
        JsonObjectBuilder jsonSetAttributes = Json.createObjectBuilder();
        JsonArrayBuilder jsonValues = Json.createArrayBuilder();

        for (TDCAttributesOutputConfiguration.TDCAttribute entry: TDCAttributes) {
            String name = entry.getAttribute();
            String value = record.getString(entry.getField());

            JsonObjectBuilder jsonValue = Json.createObjectBuilder();
            JsonObjectBuilder jsonAttributeType = Json.createObjectBuilder();

            jsonAttributeType.add("type", "CUSTOM_ATTRIBUTE");
            jsonAttributeType.add("name", name);

            jsonValue.add("attributeType", jsonAttributeType);
            jsonValue.add("value", value);
            jsonValues.add(jsonValue);
        }

        jsonSetAttributes.add("id", record.getString(TDCObjectID));
        jsonSetAttributes.add("values", jsonValues);
        jsonSetAttributes.add("comment", "");

        return jsonSetAttributes.build();
    }


}