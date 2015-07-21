package com.nagornyi.uc.action;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.gdata.util.ServiceException;
import com.nagornyi.uc.oauth2.contacts.ContactsAPI;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

import java.io.IOException;

public class GetContactsAction implements Action {

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        try {
            JSONObject contacts = ContactsAPI.getInstance().getAllContacts("info@ukraina-centr.com");
            resp.setResponseParam("contacts", contacts);
        } catch (ServiceException | IOException e) {
            throw new RuntimeException("Could not load contacts", e);
        }
    }
}
