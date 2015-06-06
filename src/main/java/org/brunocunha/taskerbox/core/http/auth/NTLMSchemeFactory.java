package org.brunocunha.taskerbox.core.http.auth;

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.impl.auth.NTLMScheme;
import org.apache.http.params.HttpParams;

/**
 * NTLM Scheme Factory
 * 
 * From: https://hc.apache.org/httpcomponents-client-ga/ntlm.html
 * 
 */
public class NTLMSchemeFactory implements AuthSchemeFactory {

    public AuthScheme newInstance(final HttpParams params) {
        return new NTLMScheme(new JCIFSEngine());
    }

}

