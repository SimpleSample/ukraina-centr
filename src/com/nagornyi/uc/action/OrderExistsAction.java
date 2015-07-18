package com.nagornyi.uc.action;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.entity.Order;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

/**
 * @author Nagornyi
 * Date: 26.06.14
 */
public class OrderExistsAction implements Action {
    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        String orderId = req.getParam("orderId");
        if (orderId != null) {
            try {
                //if decoded - ok
                Key key = KeyFactory.stringToKey(orderId);
                JSONObject response = new JSONObject();

                Order order = DAOFacade.findById(Order.class, key);
                if (Order.Status.SUCCESS.equals(order.getStatus())) {
                    response.put("title", "Квитки придбано");
                    response.put("message", "Ви успішно придбали квитки, підтвердження надіслано на вашу пошту");
                } else if (Order.Status.PROCESSING.equals(order.getStatus())) {
                    response.put("title", "Квитки замовлено");
                    response.put("message", "Ви успішно замовили квитки, після перерахування коштів підтвердження буде надіслано на вашу пошту");
                } else if (Order.Status.FAILURE.equals(order.getStatus())) {
                    response.put("title", "Помилка");
                    response.put("message", "Невдале замовлення");
                }
                response.put("orderId", orderId);
                resp.setDataObject(response);
            } catch (Exception e) {
                //nothing to do, just a check
            }
        }
    }
}
