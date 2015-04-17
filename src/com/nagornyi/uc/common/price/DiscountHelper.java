package com.nagornyi.uc.common.price;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.entity.DiscountCategory;


/**
 * @author Nagornyi
 * Date: 05.06.14
 */
public class DiscountHelper {

    public static DiscountCategory[] getDiscountCategories() {
        return DiscountCategory.values();
    }

    public static JSONArray getDiscountCategoriesAsJSON() throws JSONException {
        DiscountCategory[] categories = getDiscountCategories();
        JSONArray array = new JSONArray();
        for (DiscountCategory category: categories) {
            JSONObject object = new JSONObject();
            object.put("id", category.name());
            object.put("text", category.getDiscount().getName());
            object.put("type", category.getDiscount().getType().name());
            object.put("value", category.getDiscount().getValue());
            array.put(object);
        }
        return array;
    }
}
