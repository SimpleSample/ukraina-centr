package com.nagornyi.uc.oauth2.contacts;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.appengine.datastore.AppEngineDataStoreFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.util.store.DataStore;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.gdata.client.Query;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.Content;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.Name;
import com.google.gdata.data.extensions.PhoneNumber;
import com.google.gdata.util.ServiceException;
import com.nagornyi.uc.oauth2.AuthUtil;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Created by artemnagorny on 17.08.15.
 */
public class ContactsAPI {

    private static final String NONAME = "No Name";
    private static final String FULL_NAME_KEY = "fullName";
    private static final String EMAIL_KEY = "email";
    private static final String PHONE_NUMBERS_KEY = "phoneNumbers";
    private static final String NOTE_KEY = "note";

    private static final Logger log = Logger.getLogger(ContactsAPI.class.getName());

    private static class Holder {
        private static final ContactsAPI instance = new ContactsAPI();
    }

    public static ContactsAPI getInstance() {
        return Holder.instance;
    }

    private ContactsService createService(String userId) throws IOException {
        DataStore<StoredCredential> dataStore = StoredCredential.getDefaultDataStore(AppEngineDataStoreFactory.getDefaultInstance());
        StoredCredential storedCredential = dataStore.get(userId);
        GoogleCredential credential = AuthUtil.convertFrom(storedCredential);

        ContactsService myService = new ContactsService("ukraina-centr");
        myService.setOAuth2Credentials(credential);
        return myService;
    }

    public JSONObject getAllContacts(String userMail) throws IOException, ServiceException, JSONException {
        ContactsService service = createService(userMail);

        //http://stackoverflow.com/questions/26286369/google-contacts-api-failing-to-refresh-access-token/26466985#26466985
        service.getRequestFactory().setHeader("User-Agent", "ukraina-centr");
        URL feedUrl = new URL("https://www.google.com/m8/feeds/contacts/"+userMail+"/full");
        Query myQuery = new Query(feedUrl);
        myQuery.setMaxResults(2000);
        ContactFeed resultFeed = service.getFeed(myQuery,
                ContactFeed.class);

        log.info("Fetched all contacts for " + resultFeed.getTitle().getPlainText());
        JSONObject result = new JSONObject();
        for (ContactEntry entry : resultFeed.getEntries()) {
            JSONObject contact = new JSONObject();
            String fullNameToDisplay = null;
            if (entry.hasName() && entry.getName().hasFullName()) {
                Name name = entry.getName();
                fullNameToDisplay = name.getFullName().getValue();
                    if (name.getFullName().hasYomi()) {
                        fullNameToDisplay += " (" + name.getFullName().getYomi() + ")";
                    }
            } else {
                fullNameToDisplay = NONAME;
            }
            contact.put(FULL_NAME_KEY, fullNameToDisplay);

            if (entry.hasEmailAddresses()) {
                JSONArray emails = new JSONArray();
                for (Email email : entry.getEmailAddresses()) {
                    emails.put(email.getAddress());
                }
                contact.put(EMAIL_KEY, emails);
            }

            if (entry.hasPhoneNumbers()) {
                JSONArray phones = new JSONArray();
                for (PhoneNumber phone : entry.getPhoneNumbers()) {
                    phones.put(phone.getPhoneNumber());
                }
                contact.put(PHONE_NUMBERS_KEY, phones);
            }

            Content content = entry.getContent();
            if (content != null && content instanceof TextContent) {
                contact.put(NOTE_KEY, ((TextContent)content).getContent().getPlainText());
            }
            result.put(entry.getId(), contact);
        }

        return result;
    }
}
