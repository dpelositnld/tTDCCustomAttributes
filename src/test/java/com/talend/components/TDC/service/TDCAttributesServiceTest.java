package com.talend.components.TDC.service;

import com.talend.components.TDC.client.TDCAPIClient;
import com.talend.components.TDC.configuration.TDCAttributesOutputConfiguration;
import com.talend.components.TDC.dataset.TDCAttributesDataSet;
import com.talend.components.TDC.datastore.BasicAuthDataStore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.http.Response;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.runtime.record.RecordImpl;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WithComponents("com.talend.components.TDC")
class TDCAttributesServiceTest {
    @Service
    private TDCAttributesService service;

    @Service
    private AuthenticationService authService;

    TDCAttributesOutputConfiguration config;

    @BeforeAll
    void setup() {
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "8888");
        System.setProperty("https.proxyPort", "8888");

        config = new TDCAttributesOutputConfiguration();
        TDCAttributesDataSet ds = new TDCAttributesDataSet();

        ds.setDataStore(new BasicAuthDataStore());

        ds.getDataStore().setEndpoint("http://192.168.1.5:11480");
        ds.getDataStore().setUsername("Administrator");
        ds.getDataStore().setPassword("Administrator");

        config.setDataSet(ds);
    }

    @Test
    public void loadCustomAttributesTest() {
        try {
            service.getClient().base(config.getDataSet().getDataStore().getEndpoint());
            service.loadCustomAttributes(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void setAttributesTest(){
        Record record;
        String username = "Administrator";
        String password = "Administrator";
        String TDCObjectID = "AAAEBADDDHj3dSV6ezKACGvnt3Kwrh3pAA";
        String contentType = "application/json";
        String token;
        List<TDCAttributesOutputConfiguration.TDCAttribute> TDCAttributes = new ArrayList<>();

        TDCAttributesOutputConfiguration.TDCAttribute attr = new TDCAttributesOutputConfiguration.TDCAttribute();
        attr.setAttribute("CustomAttribute1");
        attr.setField("0.9");
        TDCAttributes.add(attr);

        authService.getClient().base(config.getDataSet().getDataStore().getEndpoint());

        token = authService.getToken(username, password);

        String payload = "{" +
                "\"values\": [{" +
                "  \"attributeType\": {" +
                "    \"name\": \"CustomAttribute1\"," +
                "    \"type\": \"CUSTOM_ATTRIBUTE\"" +
                "  }," +
                "  \"value\": \"0.45\"" +
                "}],\n" +
                "\"comment\": \"\"," +
                "\"id\": \"AAAEBADDDHj3dSV6ezKACGvnt3Kwrh3pAA\"" +
                "}";

        JsonReader jsonReader = Json.createReader(new StringReader(payload));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();

        TDCAPIClient client = service.getClient();
        Response<JsonObject> response = client.setCustomAttributes("application/json", token, object);
        System.out.println(payload);
    }

}