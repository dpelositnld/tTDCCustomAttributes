package com.talend.components.output;

import static org.talend.sdk.component.api.component.Icon.IconType.CUSTOM;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.talend.components.dataset.TDCDataset;
import com.talend.utilities.ParameterStringBuilder;
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

import com.talend.components.service.TTDCCustomAttributesService;
import org.talend.sdk.component.api.record.Schema;

@Version(1) // default version is 1, if some configuration changes happen between 2 versions you can add a migrationHandler
@Icon(value = CUSTOM, custom = "tTDCCustomAttributesOutput") // icon is located at src/main/resources/icons/tTDCCustomAttributesOutput.svg
@Processor(name = "CustomAttributesOutput")
@Documentation("TODO fill the documentation for this processor")
public class TTDCCustomAttributesOutput implements Serializable {
    private final TTDCCustomAttributesOutputConfiguration configuration;
    private final TTDCCustomAttributesService service;

    private TDCDataset dataset;

    String TDCEndpoint;
    String TDCUsername;
    String TDCPassword;

    String TDCObjectID;

    List<TTDCCustomAttributesOutputConfiguration.TDCAttribute> TDCAttributes;

    String token = "";

    public TTDCCustomAttributesOutput(@Option("configuration") final TTDCCustomAttributesOutputConfiguration configuration,
                                      final TTDCCustomAttributesService service) {

        this.configuration = configuration;
        this.service = service;
        this.dataset = configuration.getDataset();
        this.TDCEndpoint = dataset.getDatastore().getTDC_Endpoint();
        this.TDCUsername = dataset.getDatastore().getTDC_username();
        this.TDCPassword = dataset.getDatastore().getTDC_password();
        this.TDCObjectID = configuration.TDCObjectID;
        this.TDCAttributes = configuration.TDCAttributes;
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

        try {
            token = loginTDCRestCall();
            setAttributesTDCRestCall(defaultInput);
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

    String loginTDCRestCall() throws Exception {
        String auth_path = "/auth/login";
        String urlString = TDCEndpoint + auth_path + "?user=" + TDCUsername + "&password=" + TDCPassword;
        URL url = new URL(urlString);
        //URL url = new URL("http://localhost:11480/MM/rest/v1/auth/login?user=Administrator&password=Administrator");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();

        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else {

            String inline = "";
            Scanner scanner = new Scanner(url.openStream());

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

        System.out.println("Token: " + token);

        return token;
    }

    int setAttributesTDCRestCall(Record record) throws Exception{
        URL url = new URL(TDCEndpoint + "/repository/setAttributes");

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888));
        HttpURLConnection con = (HttpURLConnection) url.openConnection(proxy);
        //HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setDoOutput(true);
        //con.setDoInput(true);
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("api_key", token);

        System.out.println(con.getRequestProperty("api_key"));

        String message = buildSetAttributesTDCRestBody(record);

        OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
        wr.write(message);
        wr.flush();

        int responsecode = con.getResponseCode();

        if (responsecode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responsecode);
        } else {
            con.disconnect();
        }

        return responsecode;
    }

    private boolean isTDCAttribute(String name) {
        boolean isAttribute = false;
        for (TTDCCustomAttributesOutputConfiguration.TDCAttribute attr : TDCAttributes) {
            if (attr.name.equals(name)) {
                isAttribute = true;
                break;
            }
        }
        return isAttribute;
    }

    public static void main(String[] args) {
        // new TTDCCustomAttributesOutput(new TTDCCustomAttributesOutputConfiguration(), new TTDCCustomAttributesService()).onNext(r);
    }

}