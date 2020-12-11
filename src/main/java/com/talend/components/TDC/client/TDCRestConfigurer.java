package com.talend.components.TDC.client;

import com.talend.components.TDC.datastore.BasicAuthDataStore;
import lombok.extern.slf4j.Slf4j;
import org.talend.sdk.component.api.service.http.Configurer;
import org.talend.sdk.component.api.service.http.Response;

import javax.json.JsonObject;

@Slf4j
public class TDCRestConfigurer implements Configurer {

    TDCAPIClient client;

    @Override
    public void configure(final Connection connection, final ConfigurerConfiguration configuration) {
        final BasicAuthDataStore dataStore = configuration.get("dataStore", BasicAuthDataStore.class);
        client = configuration.get("httpClient", TDCAPIClient.class);

        // Add Content-Type of body
        if ((connection.getMethod() == "PUT" || connection.getMethod() == "POST") && connection.getHeaders().containsKey("Content-Type")){
            connection.withHeader("Content-Type", "application/json");
        }

        //Set token
        String token = "";
        if (!dataStore.isUseExistingSession()) {
            token = getToken(dataStore.getUsername(), dataStore.getPassword());
        } else {
            token = dataStore.getToken();
        }
        connection.withHeader("api_key", token);
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