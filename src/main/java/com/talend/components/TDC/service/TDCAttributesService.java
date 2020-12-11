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
import javax.swing.plaf.synth.SynthTextAreaUI;
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

        JsonObject responseBody = repositoryBrowse(dataStore, TDCRepositoryPath, TDCObjectTypes);

        JsonObject resultJson = responseBody.getJsonObject("result");

        List<String> configurationPaths = new ArrayList<String>();

        getConfigurationPaths("", resultJson.getJsonArray("links"), configurationPaths);

        for (String path: configurationPaths) {
            items.add(new SuggestionValues.Item(path, path));
        }
        values.setItems(items);

        return values;
    }

    private JsonObject repositoryBrowse(BasicAuthDataStore dataStore, String TDCRepositoryPath, String TDCObjectTypes){
        JsonObject responseBody;
        client.base(dataStore.getEndpoint());

        String token = "";
        if (!dataStore.isUseExistingSession())
            token = authService.getToken(dataStore.getUsername(), dataStore.getPassword());
        else
            token = dataStore.getToken();

        Response<JsonObject> response = client.repositoryBrowse("application/json", token, TDCObjectTypes, TDCRepositoryPath);

        if (response.status() == 200)
            responseBody = response.body();
        else
            throw new RuntimeException(response.error(String.class));

        if (!dataStore.isUseExistingSession())
            authService.logout(token);

        return responseBody;
    }

    public JsonObject getCustomAttributes(TDCAttributesDataSet dataSet){
        int pageSize = 100;
        int pageNumber = 1;
        JsonObject MQLQuery = buildExecuteMQLPayload(dataSet, pageSize, pageNumber);
        return executeMQL(dataSet.getDataStore(), MQLQuery);
    }

    public JsonObject executeMQL(BasicAuthDataStore dataStore, JsonObject MQLPayload){
        JsonObject responseBody;
        client.base(dataStore.getEndpoint());

        System.setProperty("http.proxyHost","host.docker.internal");
        System.setProperty("https.proxyHost","host.docker.internal");
        System.setProperty("http.proxyPort","8888");
        System.setProperty("https.proxyPort","8888");

        String token = "";
        if (!dataStore.isUseExistingSession())
            token = authService.getToken(dataStore.getUsername(), dataStore.getPassword());
        else
            token = dataStore.getToken();

        Response<JsonObject> response = client.executeMQLQuery("application/json", token, MQLPayload);

        if (response.status() == 200)
            responseBody = response.body();
        else
            throw new RuntimeException(response.error(String.class));

        if (!dataStore.isUseExistingSession())
            authService.logout(token);

        return responseBody;
    }

    private JsonObject buildExecuteMQLPayload(TDCAttributesDataSet dataSet, int pageSize, int pageNumber) {
        JsonObject jsonPayload;
        JsonObjectBuilder builder = Json.createObjectBuilder();

        String selectClause = "";
        String fromClause = dataSet.getConfigurationPath();
        String whereClause = "";

        selectClause += "#Name#, #Object Stable Id#";
        for (String attr: dataSet.getAttributes()) {
            selectClause = !selectClause.isEmpty()? selectClause + ",": selectClause;
            selectClause += "{" + attr + "}";

            whereClause = !whereClause.isEmpty()? whereClause + " OR ": whereClause;
            whereClause += "{" + attr + "} exists";
        }

        jsonPayload = builder
                .add("select", selectClause)
                .add("from", fromClause)
                .add("where", whereClause)
                .add("pageSize", pageSize)
                .add("pageNumber", pageNumber)
                .build();

        System.out.println(jsonPayload.toString());

        return jsonPayload;
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

    public String getToken(String username, String password) {
        JsonObject response = login(username, password);
        return response.getJsonObject("result").getString("token");
    }

    public JsonObject login(String username, String password) {
        final Response<JsonObject> response = client.login(username, password, true);
        if (response.status() == 200) {
            return response.body();
        }

        throw new RuntimeException(response.error(String.class));
    }
}