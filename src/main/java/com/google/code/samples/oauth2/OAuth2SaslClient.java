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
 * Last modified data: 30/07/2022
 * Current version: 1.0
 */

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;
import java.io.IOException;


/**
 * An OAuth2 implementation of SaslClient.
 */
public class OAuth2SaslClient implements SaslClient {

    private final String oauthToken;
    private final CallbackHandler callbackHandler;

    private boolean isComplete = false;

    /**
     * Creates a new instance of the com.google.code.samples.oauth2.OAuth2SaslClient. This will ordinarily only
     * be called from com.google.code.samples.oauth2.OAuth2SaslClientFactory.
     */
    public OAuth2SaslClient(String oauthToken, CallbackHandler callbackHandler) {
        this.oauthToken = oauthToken;
        this.callbackHandler = callbackHandler;
    }

    public String getMechanismName() {
        return "XOAUTH2";
    }

    public boolean hasInitialResponse() {
        return true;
    }

    public byte[] evaluateChallenge(byte[] challenge) throws SaslException {
        if (isComplete) {
            // Empty final response from server, just ignore it.
            return new byte[]{};
        }

        NameCallback nameCallback = new NameCallback("Enter name");
        Callback[] callbacks = new Callback[]{nameCallback};
        try {
            callbackHandler.handle(callbacks);
        } catch (UnsupportedCallbackException e) {
            throw new SaslException("Unsupported callback: " + e);
        } catch (IOException e) {
            throw new SaslException("Failed to execute callback: " + e);
        }
        String email = nameCallback.getName();

        byte[] response = String.format("user=%s\1auth=Bearer %s\1\1", email, oauthToken).getBytes();
        isComplete = true;
        return response;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public byte[] unwrap(byte[] incoming, int offset, int len) {
        throw new IllegalStateException();
    }

    public byte[] wrap(byte[] outgoing, int offset, int len) {
        throw new IllegalStateException();
    }

    public Object getNegotiatedProperty(String propName) {
        if (!isComplete()) {
            throw new IllegalStateException();
        }
        return null;
    }

    @Override
    public void dispose() throws SaslException {

    }
}