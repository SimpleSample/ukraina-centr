package com.nagornyi.uc.oauth2;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.appengine.datastore.AppEngineDataStoreFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.nagornyi.env.EnvVariablesStorage;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

public class AuthUtil {
    private static final Logger log = Logger.getLogger(AdminOAuth2CallbackServlet.class.getName());
    private static final String ENV_PARAMETER_GROUP = "Oauth";

    private static NetHttpTransport transport = new NetHttpTransport();
    private static JacksonFactory jacksonFactory = JacksonFactory.getDefaultInstance();
    private static AppEngineDataStoreFactory dataStoreFactory = AppEngineDataStoreFactory.getDefaultInstance();

    public static GoogleCredential convertFrom(StoredCredential storedCredential) {
        String clientId = EnvVariablesStorage.getValue(ENV_PARAMETER_GROUP, "clientId");
        String clientSecret = EnvVariablesStorage.getValue(ENV_PARAMETER_GROUP, "clientSecret");
        GoogleCredential credential = new GoogleCredential.Builder()
                .setJsonFactory(jacksonFactory)
                .setTransport(transport)
                .setClientSecrets(clientId, clientSecret).build();
        log.info(storedCredential.toString());

        return credential
                .setAccessToken(storedCredential.getAccessToken())
                .setRefreshToken(storedCredential.getRefreshToken())
                .setExpirationTimeMilliseconds(storedCredential.getExpirationTimeMilliseconds());
    }

    public static AuthorizationCodeFlow initFlow(Set<String> feeds) throws IOException {
        String clientId = EnvVariablesStorage.getValue(ENV_PARAMETER_GROUP, "clientId");
        String clientSecret = EnvVariablesStorage.getValue(ENV_PARAMETER_GROUP, "clientSecret");
        return new GoogleAuthorizationCodeFlow.Builder(
                        transport, jacksonFactory,
                        clientId, clientSecret, feeds)
                .setDataStoreFactory(dataStoreFactory)
                .setAccessType("offline")
                .build();
    }
}
