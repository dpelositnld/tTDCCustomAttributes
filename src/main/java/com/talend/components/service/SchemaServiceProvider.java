package com.talend.components.service;

import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.completion.DynamicValues;
import org.talend.sdk.component.api.service.completion.Values;

import java.util.Arrays;

@Service
public class SchemaServiceProvider {

    // you can put logic here you can reuse in components
    @DynamicValues("schemaProvider")
    public Values actions() {
        return new Values(Arrays
                .asList(new Values.Item("1", "Delete"), new Values.Item("2", "Insert"),
                        new Values.Item("3", "Update")));
    }
}