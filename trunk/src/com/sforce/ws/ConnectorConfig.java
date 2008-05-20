/*
 * Copyright (c) 2005, salesforce.com, inc.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided 
 * that the following conditions are met:
 * 
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the 
 *    following disclaimer.
 *  
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and 
 *    the following disclaimer in the documentation and/or other materials provided with the distribution. 
 *    
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or 
 *    promote products derived from this software without specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED 
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A 
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR 
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED 
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.sforce.ws;

import java.io.*;
import java.util.*;
import java.net.*;

import com.sforce.ws.util.Verbose;

/**
 * This class contains a set of configuration properties
 *
 * @author http://cheenath.com
 * @version 1.0
 * @since 1.0  Dec 19, 2005
 */
public class ConnectorConfig {
    private int readTimeout;
    private int connectionTimeout;
    private boolean traceMessage;
    private boolean compression = true;
    private boolean prettyPrintXml;
    private boolean manualLogin;
    private boolean useChunkedPost;
    private String username;
    private String password;
    private String sessionId;
    private String authEndpoint;
    private String serviceEndpoint;
    private String traceFile;
    private PrintStream traceStream;
    private String proxyUsername;
    private String proxyPassword;
    private HashMap<String, String> headers;
    private Proxy proxy = Proxy.NO_PROXY;
    private ArrayList<MessageHandler> handlers = new ArrayList<MessageHandler>();
    private int maxRequestSize;
    private int maxResponseSize;
    private boolean validateSchema = true;

    public static final ConnectorConfig DEFAULT = new ConnectorConfig();


    public void setNtlmDomain(String domain) {
        if (System.getProperty("http.auth.ntlm.domain") == null) {
            System.setProperty("http.auth.ntlm.domain", domain);
        } else {
            Verbose.log("http.auth.ntlm.domain already set");
        }
    }

    public boolean isValidateSchema() {
        return validateSchema;
    }

    public void setValidateSchema(boolean validateSchema) {
        this.validateSchema = validateSchema;
    }

    public void setProxy(String host, int port) {
        SocketAddress addr = new InetSocketAddress(host, port);
        proxy = new Proxy(Proxy.Type.HTTP, addr);
    }

    public Proxy getProxy() {
        return proxy;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public String getRequestHeader(String key) {
        String ret = null;
        if (this.headers != null) {
            ret = this.headers.get(key);
        }
        return ret;
    }

    public void setRequestHeader(String key, String value) {
        if (this.headers == null) {
            this.headers = new HashMap<String, String>();
        }
        this.headers.put(key, value);
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public boolean isPrettyPrintXml() {
        return prettyPrintXml;
    }

    public void setPrettyPrintXml(boolean prettyPrintXml) {
        this.prettyPrintXml = prettyPrintXml;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getServiceEndpoint() {
        return serviceEndpoint;
    }

    public void setServiceEndpoint(String serviceEndpoint) {
        if (serviceEndpoint == null || serviceEndpoint.equals("")) {
            throw new IllegalArgumentException("illegal service endpoint " + serviceEndpoint);
        }
        this.serviceEndpoint = serviceEndpoint;
    }

    public boolean isCompression() {
        return compression;
    }

    public void setCompression(boolean compress) {
        this.compression = compress;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public boolean isTraceMessage() {
        return traceMessage;
    }

    public void setTraceMessage(boolean traceMessage) {
        this.traceMessage = traceMessage;
    }

    public String getTraceFile() {
        return traceFile;
    }

    public void setTraceFile(String traceFile) throws FileNotFoundException {
        this.traceFile = traceFile;
        File file = new File(traceFile);

        if (file.exists()) {
            Verbose.log("Log file already exists, appending to " + file);
        }

        traceStream = new PrintStream(new FileOutputStream(file, true), true);
    }

    public PrintStream getTraceStream() {
        return traceStream == null ? System.out : traceStream;
    }

    public String getAuthEndpoint() {
        return authEndpoint;
    }

    public void setAuthEndpoint(String authEndpoint) {
        if (authEndpoint == null || authEndpoint.equals("")) {
            throw new IllegalArgumentException("Illegal auth endpoint " + authEndpoint);
        }
        this.authEndpoint = authEndpoint;
    }

    public void setManualLogin(boolean manualLogin) {
        this.manualLogin = manualLogin;
    }

    public boolean isManualLogin() {
        return manualLogin;
    }

	public void setUseChunkedPost(boolean chunk) {
		this.useChunkedPost = chunk;
	}
	
	public boolean useChunkedPost() {
		return this.useChunkedPost;
	}
	
    public void verifyPartnerEndpoint() throws ConnectionException {
        verifyEndpoint("/services/Soap/u/");
    }

    public void verifyEnterpriseEndpoint() throws ConnectionException {
        verifyEndpoint("/services/Soap/c/");
    }

    public Iterator<MessageHandler> getMessagerHandlers() {
        return handlers.iterator();
    }

    public boolean hasMessageHandlers() {
        return handlers.size() != 0;
    }

    public void addMessageHandler(MessageHandler handler) {
        handlers.add(handler);
    }

    public void clearMessageHandlers() {
        handlers.clear();
    }

    public int getMaxRequestSize() {
        return maxRequestSize;
    }

    public void setMaxRequestSize(int maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
    }

    public int getMaxResponseSize() {
        return maxResponseSize;
    }

    public void setMaxResponseSize(int maxResponseSize) {
        this.maxResponseSize = maxResponseSize;
    }

    private void verifyEndpoint(String contains) throws ConnectionException {
        if (authEndpoint != null && !authEndpoint.contains(contains)) {
            throw new ConnectionException("Check authEndpoint. It must contain '" + contains + "'. " +
                                          "authEndpoint specified " + authEndpoint);
        }
        if (serviceEndpoint != null && !serviceEndpoint.contains(contains)) {
            throw new ConnectionException("Check serviceEndpoint. It must contain '" + contains + "'. " +
                                          "serviceEndpoint specified " + serviceEndpoint);
        }
    }
}