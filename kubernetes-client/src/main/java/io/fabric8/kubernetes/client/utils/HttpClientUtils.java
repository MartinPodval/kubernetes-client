/**
 * Copyright (C) 2015 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.kubernetes.client.utils;

import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Challenge;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.internal.SSLUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.fabric8.kubernetes.client.utils.Utils.isNotNullOrEmpty;

public class HttpClientUtils {

    public static OkHttpClient createHttpClient(final Config config) {
        try {
            OkHttpClient httpClient = new OkHttpClient();

            // Follow any redirects
            httpClient.setFollowRedirects(true);
            httpClient.setFollowSslRedirects(true);

            if (config.isTrustCerts()) {
                httpClient.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                });
            }

            TrustManager[] trustManagers = SSLUtils.trustManagers(config);
            KeyManager[] keyManagers = SSLUtils.keyManagers(config);

            if (keyManagers != null || trustManagers != null || config.isTrustCerts()) {
                try {
                    SSLContext sslContext = SSLUtils.sslContext(keyManagers, trustManagers, config.isTrustCerts());
                    httpClient.setSslSocketFactory(sslContext.getSocketFactory());
                } catch (GeneralSecurityException e) {
                    throw new AssertionError(); // The system has no TLS. Just give up.
                }
            }

            if (isNotNullOrEmpty(config.getUsername()) && isNotNullOrEmpty(config.getPassword())) {
                httpClient.setAuthenticator(new Authenticator() {

                    @Override
                    public Request authenticate(Proxy proxy, Response response) throws IOException {
                        List<Challenge> challenges = response.challenges();
                        Request request = response.request();
                        HttpUrl url = request.httpUrl();
                        for (int i = 0, size = challenges.size(); i < size; i++) {
                            Challenge challenge = challenges.get(i);
                            if (!"Basic".equalsIgnoreCase(challenge.getScheme())) continue;

                            String credential = Credentials.basic(config.getUsername(), config.getPassword());
                            return request.newBuilder()
                                    .header("Authorization", credential)
                                    .build();
                        }
                        return null;
                    }

                    @Override
                    public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
                        return null;
                    }
                });
            } else if (config.getOauthToken() != null) {
                httpClient.interceptors().add(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request authReq = chain.request().newBuilder().addHeader("Authorization", "Bearer " + config.getOauthToken()).build();
                        return chain.proceed(authReq);
                    }
                });
            }

            if (config.getConnectionTimeout() > 0) {
                httpClient.setConnectTimeout(config.getConnectionTimeout(), TimeUnit.MILLISECONDS);
            }

            if (config.getRequestTimeout() > 0) {
                httpClient.setReadTimeout(config.getRequestTimeout(), TimeUnit.MILLISECONDS);
            }

            if (config.getProxy() != null) {
                try {
                    URL u = new URL(config.getProxy());
                    httpClient.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(u.getHost(), u.getPort())));
                } catch (MalformedURLException e) {
                    throw new KubernetesClientException("Invalid proxy server configuration", e);
                }
            }

            return httpClient;
        } catch (Exception e) {
            throw KubernetesClientException.launderThrowable(e);
        }
    }
}
