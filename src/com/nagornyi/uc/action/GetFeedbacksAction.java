package com.nagornyi.uc.action;

import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.IFeedbackDAO;
import com.nagornyi.uc.dao.PaginationBatch;
import com.nagornyi.uc.entity.Feedback;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

public class GetFeedbacksAction implements Action {

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        String cursor = req.getParam("cursor");
        Integer count = Integer.parseInt((String)req.getParam("count"));
        IFeedbackDAO dao = DAOFacade.getDAO(Feedback.class);

        PaginationBatch<Feedback> feedbacksBatch = dao.getNextBatch(cursor, count);

        JSONObject result = new JSONObject();
        result.put("cursor", feedbacksBatch.getStartCursor());

        JSONArray feedbacks = new JSONArray();
        for (Feedback feedback: feedbacksBatch.getEntitiesBatch()) {
            JSONObject feedbackJson = new JSONObject();
            feedbackJson.put("date", feedback.getDate());
            feedbackJson.put("feedbackText", feedback.getFeedback());
            feedbackJson.put("username", feedback.getUsername());
            feedbacks.put(feedbackJson);
        }
        result.put("feedbacks", feedbacks);
        resp.setDataObject(result);
    }
}
