package com.nagornyi.uc.action;

import com.google.appengine.repackaged.org.json.JSONException;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.entity.Feedback;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

import java.util.Date;

public class PostFeedback implements Action {

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        String feedbackText = req.getParam("feedback");
        Feedback feedback = new Feedback();
        feedback.setFeedback(feedbackText);
        feedback.setDate(new Date().getTime());
        feedback.setUsername(getUsername(req));
        DAOFacade.save(feedback);
    }

    private String getUsername(ActionRequest req) {
        String result = "Anonymous";
        if (req.isAuthorized()) {
            if (req.getUser().isPartner()) {
                result = req.getUser().getPartnerName();
            } else {
                result = req.getUser().getUsername();
            }
        }
        return result;
    }
}
