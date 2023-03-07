package com.google.code.samples.oauth2;
/* Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Modified by Ginigal Godage Vimukthi Pahasara
 * Last updated on: 2022/07/21
 * Current version: 1.0
 */

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.sun.mail.smtp.SMTPTransport;

import javax.mail.MessagingException;
import javax.mail.Session;
import java.io.IOException;
import java.io.Serial;
import java.security.Provider;
import java.security.Security;
import java.util.Properties;


/**
 * Performs OAuth2 authentication.
 *
 * <p>Before using this class, you must call {@code initialize} to install the
 * OAuth2 SASL provider.
 */
public class OAuth2Authenticator {

    public OAuth2Authenticator() {
        initialize();
    }

    /**
     * Installs the OAuth2 SASL provider. This must be called exactly once before
     * calling other methods on this class.
     */
    public void initialize() {
        Security.addProvider(new OAuth2Provider());
    }

    /**
     * Connects and authenticates to an SMTP server with OAuth2. You must have
     * called {@code initialize}.
     *
     * @param host       Hostname of the smtp server, for example {@code
     *                   smtp.googlemail.com}.
     * @param port       Port of the smtp server, for example 587.
     * @param userEmail  Email address of the user to authenticate, for example
     *                   {@code oauth@gmail.com}.
     * @param oauthToken The user's OAuth token.
     * @param debug      Whether to enable debug logging on the connection.
     * @return An authenticated SMTPTransport that can be used for SMTP
     * operations.
     */
    public SMTPTransport connectToSmtp(String host, int port, String userEmail, String oauthToken, boolean debug) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.sasl.enable", "true");
        props.put("mail.smtp.sasl.mechanisms", "XOAUTH2");
        props.put(OAuth2SaslClientFactory.OAUTH_TOKEN_PROP, oauthToken);
        Session session = Session.getInstance(props);
        session.setDebug(debug);

        SMTPTransport transport = new SMTPTransport(session, null);
        // If the password is non-null, SMTP tries to do AUTH LOGIN.
        final String emptyPassword = "";
        transport.connect(host, port, userEmail, emptyPassword);
        return transport;
    }

    public String getAccessToken() throws IOException {
        String clientID = "Put your client ID here";
        String clientSecret = "Put your secret key here";
        String RefreshToken = "Put your refresh key here";
        TokenResponse response = new GoogleRefreshTokenRequest(new NetHttpTransport(), new GsonFactory(), RefreshToken, clientID, clientSecret).execute();
        return response.getAccessToken();
    }

    private static class OAuth2Provider extends Provider {
        @Serial
        private static final long serialVersionUID = 1L;

        public OAuth2Provider() {
            super("Google OAuth2 Provider", Double.toString(1.0), "Provides the XOAUTH2 SASL Mechanism");
            put("SaslClientFactory.XOAUTH2", "com.google.code.samples.oauth2.OAuth2SaslClientFactory");
        }
    }
}
