package com.talend.components.TDC.output;

import static org.talend.sdk.component.api.component.Icon.IconType.CUSTOM;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.talend.components.TDC.configuration.CustomAttributesOutputConfiguration;
import com.talend.components.TDC.dataset.LoginDataset;
import org.json.JSONArray;
import org.json.JSONObject;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.AfterGroup;
import org.talend.sdk.component.api.processor.BeforeGroup;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Input;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.api.record.Record;

import com.talend.components.TDC.service.CustomAttributesService;
import org.talend.sdk.component.api.record.Schema;

@Version(1) // default version is 1, if some configuration changes happen between 2 versions you can add a migrationHandler
@Icon(value = CUSTOM, custom = "CustomAttributesOutput") // icon is located at src/main/resources/icons/CustomAttributesOutput.svg
@Processor(name = "CustomAttributesOutput", family = "TDC")
@Documentation("TODO fill the documentation for this processor")
public class CustomAttributesOutput implements Serializable {
    private final CustomAttributesOutputConfiguration configuration;
    private final CustomAttributesService service;

    private LoginDataset dataset;

    String TDCEndpoint;
    String TDCUsername;
    String TDCPassword;
    boolean isUseProxy;
    String proxyAddress;
    int proxyPort;

    String TDCObjectID;

    List<CustomAttributesOutputConfiguration.TDCAttribute> TDCAttributes;

    public CustomAttributesOutput(@Option("configuration") final CustomAttributesOutputConfiguration configuration,
                                  final CustomAttributesService service) {

        this.configuration = configuration;
        this.service = service;
        this.dataset = configuration.getDataSet();
        this.TDCEndpoint = dataset.getDataStore().getTDC_Endpoint();
        this.TDCUsername = dataset.getDataStore().getTDC_username();
        this.TDCPassword = dataset.getDataStore().getTDC_password();
        this.isUseProxy = dataset.getDataStore().isUseProxy();
        this.proxyAddress = dataset.getDataStore().getProxyAddress();
        this.proxyPort = dataset.getDataStore().getProxyPort();
        this.TDCObjectID = configuration.getTDCObjectID();
        this.TDCAttributes = configuration.getTDCAttributes();
    }

    @PostConstruct
    public void init() {
        // this method will be executed once for the whole component execution,
        // this is where you can establish a connection for instance
        // Note: if you don't need it you can delete it
    }

    @BeforeGroup
    public void beforeGroup() {
        // if the environment supports chunking this method is called at the beginning if a chunk
        // it can be used to start a local transaction specific to the backend you use
        // Note: if you don't need it you can delete it
    }

    @ElementListener
    public void onNext(
            @Input final Record defaultInput) {
        // this is the method allowing you to handle the input(s) and emit the output(s)
        // after some custom logic you put here, to send a value to next element you can use an
        // output parameter and call emit(value).

        String token = "";
        try {
            if (configuration.isUseToken())
                token = configuration.getToken();
            else
                token = TDCRest_login();
            TDCRest_setAttributes(token, defaultInput);
            TDCRest_logout(token);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @AfterGroup
    public void afterGroup() {
        // symmetric method of the beforeGroup() executed after the chunk processing
        // Note: if you don't need it you can delete it
    }

    @PreDestroy
    public void release() {
        // this is the symmetric method of the init() one,
        // release potential connections you created or data you cached
        // Note: if you don't need it you can delete it
    }

    String buildSetAttributesTDCRestBody(Record record) {
        String message;
        JSONObject jsonSetAttributes = new JSONObject();

        JSONArray jsonValues = new JSONArray();

        Schema schema = record.getSchema();
        List<Schema.Entry> entries = schema.getEntries();

        for (Schema.Entry entry: entries) {
            String name = entry.getName();
            String value = record.getString(name);

            if (isTDCAttribute(name)) {
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

        message = jsonSetAttributes.toString();

        System.out.println("SetAttributesTDCRestBody:" + message);

        return message;
    }

    String TDCRest_login() throws Exception {
        String token = "";
        String api_path = "/auth/login";
        String urlString = TDCEndpoint + api_path + "?user=" + TDCUsername + "&password=" + TDCPassword + "&forceLogin=true";
        URL url = new URL(urlString);

        HttpURLConnection con = getHttpConnection(url);

        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();

        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else {

            String inline = "";
            Scanner scanner = new Scanner(con.getInputStream());

            //Write all the JSON data into a string using a scanner
            while (scanner.hasNext()) {
                inline += scanner.nextLine();
            }

            //Close the scanner
            scanner.close();
            con.disconnect();

            JSONObject jsonObject = new JSONObject(inline);

            token = jsonObject.getJSONObject("result").getString("token");
        }

        System.out.println("User " + TDCUsername + " connected with Token: " + token);

        return token;
    }

    void TDCRest_logout(String token) throws Exception {
        String api_path = "/auth/logout";
        String urlString = TDCEndpoint + api_path;
        URL url = new URL(urlString);

        HttpURLConnection con = getHttpConnection(url);

        con.setDoOutput(false);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "text/plain");
        con.setRequestProperty("api_key", token);

        int responseCode = con.getResponseCode();

        con.disconnect();

        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        }

        System.out.println("User " + TDCUsername + " with token " + token + " disconnected.");
    }

    int TDCRest_setAttributes(String token, Record record) throws Exception {
        String api_path = "/repository/setAttributes";
        URL url = new URL(TDCEndpoint + api_path);

        HttpURLConnection con = getHttpConnection(url);

        con.setDoOutput(true);
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("api_key", token);

        String message = buildSetAttributesTDCRestBody(record);

        OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
        wr.write(message);
        wr.flush();

        int responsecode = con.getResponseCode();

        if (responsecode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responsecode);
        }

        con.disconnect();

        return responsecode;
    }

    private boolean isTDCAttribute(String name) {
        boolean isAttribute = false;
        for (CustomAttributesOutputConfiguration.TDCAttribute attr : TDCAttributes) {
            if (attr.getName().equals(name)) {
                isAttribute = true;
                break;
            }
        }
        return isAttribute;
    }

    private HttpURLConnection getHttpConnection(URL url) throws Exception{
        HttpURLConnection con;

        if (this.isUseProxy) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(this.proxyAddress, this.proxyPort));
            con = (HttpURLConnection) url.openConnection(proxy);
        } else
            con = (HttpURLConnection) url.openConnection();

        return con;
    }

    public static void main(String[] args) {
        // new CustomAttributesOutput(new CustomAttributesOutputConfiguration(), new CustomAttributesService()).onNext(r);
    }

}