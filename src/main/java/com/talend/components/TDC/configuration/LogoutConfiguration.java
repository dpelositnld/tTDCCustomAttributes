package com.talend.components.TDC.configuration;

import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@Data
@GridLayout({
        @GridLayout.Row({ "token" })
})
public class LogoutConfiguration {
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String token;
}
