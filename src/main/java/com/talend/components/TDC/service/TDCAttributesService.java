package com.talend.components.TDC.service;

import com.talend.components.TDC.client.TDCAPIClient;
import com.talend.components.TDC.configuration.TDCAttributesOutputConfiguration;
import lombok.Data;

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

        /*
        System.setProperty("http.proxyHost", "host.docker.internal");
        System.setProperty("https.proxyHost", "host.docker.internal");
        System.setProperty("http.proxyPort", "8888");
        System.setProperty("https.proxyPort", "8888");
        */

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
    public SuggestionValues loadCustomAttributes(@Option final TDCAttributesOutputConfiguration config) {
        SuggestionValues values = new SuggestionValues();
        List<SuggestionValues.Item> items = new ArrayList<SuggestionValues.Item>();

        //String username = config.getDataSet().getDataStore().getUsername();
        //String password = config.getDataSet().getDataStore().getPassword();
        String username = "Administrator";
        String password = "Administrator";
        String encodedPassword = "71fhv%106LJT%115jUF%111VDY%116edM%111fiY%121yoc%122sZB%120HMf%103WRx%122Wuo%117Oqz%120hHQ";

        client.base("http://192.168.1.5:11480");

        JSONArray responseObject = getCustomAttributesResponse(username, password, encodedPassword);
        for (int i=0; i < responseObject.length(); i++) {
            JSONObject attrJson = (JSONObject) responseObject.get(i);
            items.add(new SuggestionValues.Item(attrJson.getString("name"), attrJson.getString("name")));
        }
        values.setItems(items);

        return values;
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