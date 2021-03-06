package com.nagornyi.uc.common.mail;


import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;
import com.nagornyi.env.EnvVariablesStorage;
import org.apache.commons.collections4.CollectionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copy pasted from
 * https://github.com/sendgrid/sendgrid-google-java
 */
public class Sendgrid {

    private static final Logger log = Logger.getLogger(Sendgrid.class.getName());

    private static final String ENV_PARAMETER_GROUP = "Sendgrid";

    private String from,
            from_name,
            reply_to,
            subject,
            html;
    private String serverResponse = "";
    private List<String> to_list  = new ArrayList<>();
    private List<String> to_name_list  = new ArrayList<>();
    private List<String> bcc_list = new ArrayList<>();
    private JSONObject header_list = new JSONObject();

    protected String domain = "https://sendgrid.com/",
            endpoint = "api/mail.send.json",
            username,
            password;

    public Sendgrid() {
        this.username = EnvVariablesStorage.getValue(ENV_PARAMETER_GROUP, "username");
        this.password = EnvVariablesStorage.getValue(ENV_PARAMETER_GROUP, "password");

        try {
            this.setCategory("google_sendgrid_java_lib");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * getTos - Return the list of recipients
     *
     * @return  List of recipients
     */
    public List<String> getTos() {
        return this.to_list;
    }

    /**
     * setTo - Initialize a single email for the recipient 'to' field
     * Destroy previous recipient 'to' data.
     *
     * @param    email   A list of email addresses
     * @return           The SendGrid object.
     */
    public Sendgrid setTo(String email) {
        this.to_list = new ArrayList<>();
        this.addTo(email);

        return this;
    }

    /**
     * addTo - Append an email address to the existing list of addresses
     * Preserve previous recipient 'to' data.
     *
     * @param    email   Recipient email address
     * @param    name    Recipient name
     * @return           The SendGrid object.
     */
    public Sendgrid addTo(String email, String name) {
        if (name != null) {
            this._addToName(name);
        } else {
            this._addToName("");
        }
        this.to_list.add(email);

        return this;
    }

    /**
     * addTo - Make the second parameter("name") of "addTo" method optional
     *
     * @param   email   A single email address
     * @return          The SendGrid object.
     */
    public Sendgrid addTo(String email) {
        return addTo(email, "");
    }

    /**
     * getTos - Return the list of names for recipients
     *
     * @return  List of names
     */
    public List<String> getToNames() {
        return this.to_name_list;
    }

    /**
     * getFrom - Get the from email address
     *
     * @return  The from email address
     */
    public String getFrom() {
        return this.from;
    }

    /**
     * setFrom - Set the from email
     *
     * @param    email   An email address
     * @return           The SendGrid object.
     */
    public Sendgrid setFrom(String email) {
        this.from = email;

        return this;
    }

    /**
     * getFromName - Get the from name
     *
     * @return  The from name
     */
    public String getFromName() {
        return this.from_name;
    }

    /**
     * setFromName - Set the from name
     *
     * @param    name    The name
     * @return           The SendGrid object.
     */
    public Sendgrid setFromName(String name) {
        this.from_name = name;

        return this;
    }

    /**
     * getReplyTo - Get reply to address
     *
     * @return the reply to address
     */
    public String getReplyTo() {
        return this.reply_to;
    }

    /**
     * setReplyTo - set the reply-to address
     *
     * @param  email   the email to reply to
     * @return         the SendGrid object.
     */
    public Sendgrid setReplyTo(String email) {
        this.reply_to = email;

        return this;
    }

    /**
     * getBccs - return the list of Blind Carbon Copy recipients
     *
     * @return ArrayList - the list of Blind Carbon Copy recipients
     */
    public List<String> getBccs() {
        return this.bcc_list;
    }

    /**
     * setBcc - Initialize the list of Carbon Copy recipients
     * destroy previous recipient Blind Carbon Copy data
     *
     * @param  email   an email address
     * @return         the SendGrid object.
     * @throws JSONException
     */
    public Sendgrid setBcc(String email) throws JSONException {
        this.bcc_list = new ArrayList<>();
        this.bcc_list.add(email);

        return this;
    }

    /**
     * getSubject - Get the email subject
     *
     * @return  The email subject
     */
    public String getSubject() {
        return this.subject;
    }

    /**
     * setSubject - Set the email subject
     *
     * @param    subject   The email subject
     * @return             The SendGrid object
     */
    public Sendgrid setSubject(String subject) {
        this.subject = subject;

        return this;
    }

    /**
     * getHtml - Get the HTML part of the email
     *
     * @return   The HTML part of the email.
     */
    public String getHtml() {
        return this.html;
    }

    /**
     * setHTML - Set the HTML part of the email
     *
     * @param   html   The HTML part of the email
     * @return         The SendGrid object.
     */
    public Sendgrid setHtml(String html) {
        this.html = html;

        return this;
    }

    /**
     * setCategories - Set the list of category headers
     * destroys previous category header data
     *
     * @param  category_list   the list of category values
     * @return                 the SendGrid object.
     * @throws JSONException
     */
    public Sendgrid setCategories(String[] category_list) throws JSONException {
        JSONArray categories_json = new JSONArray(category_list);
        this.header_list.put("category", categories_json);
        this.addCategory("google_sendgrid_java_lib");

        return this;
    }

    /**
     * setCategory - Clears the category list and adds the given category
     *
     * @param  category   the new category to append
     * @return            the SendGrid object.
     * @throws JSONException
     */
    public Sendgrid setCategory(String category) throws JSONException {
        JSONArray json_category = new JSONArray(new String[]{category});
        this.header_list.put("category", json_category);
        this.addCategory("google_sendgrid_java_lib");

        return this;
    }

    /**
     * addCategory - Append a category to the list of categories
     *
     * @param  category   the new category to append
     * @return            the SendGrid object.
     * @throws JSONException
     */
    public Sendgrid addCategory(String category) throws JSONException {
        if (this.header_list.has("category")) {
            ((JSONArray) this.header_list.get("category")).put(category);
        } else {
            this.setCategory(category);
        }

        return this;
    }

    /**
     * setSubstitutions - Substitute a value for list of values, where each value corresponds
     * to the list emails in a one to one relationship. (IE, value[0] = email[0],
     * value[1] = email[1])
     *
     * @param  key_value_pairs   key/value pairs where the value is an array of values
     * @return                   the SendGrid object.
     */
    public Sendgrid setSubstitutions(JSONObject key_value_pairs) {
        try {
            this.header_list.put("sub", key_value_pairs);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return this;
    }

    /**
     * addSubstitution - Substitute a value for list of values, where each value corresponds
     * to the list emails in a one to one relationship. (IE, value[0] = email[0],
     * value[1] = email[1])
     *
     * @param  from_value    the value to be replaced
     * @param  to_values   an array of values to replace the from_value
     * @return             the SendGrid object.
     * @throws JSONException
     * @throws IOException
     */
    public Sendgrid addSubstitution(String from_value, String[] to_values) throws JSONException {
        if (!this.header_list.has("sub")) {
            this.header_list.put("sub", new JSONObject());
        }
        JSONArray json_values = new JSONArray(to_values);
        ((JSONObject) this.header_list.get("sub")).put(from_value, json_values);

        return this;
    }

    /**
     * setSection - Set a list of section values
     *
     * @param  key_value_pairs   key/value pairs
     * @return                   the SendGrid object.
     * @throws JSONException
     */
    public Sendgrid setSections(JSONObject key_value_pairs) throws JSONException {
        header_list.put("section", key_value_pairs);

        return this;
    }

    /**
     * addSection - append a section value to the list of section values
     *
     * @param  from_value  the value to be replaced
     * @param  to_value    the value to replace
     * @return             the SendGrid object.
     * @throws JSONException
     */
    public Sendgrid addSection(String from_value, String to_value) throws JSONException {
        if (!header_list.has("section")) {
            header_list.put("section", new JSONObject() );
        }
        ((JSONObject) header_list.get("section")).put(from_value, to_value);

        return this;
    }

    /**
     * setUniqueArguments - Set a list of unique arguments, to be used for tracking purposes
     *
     * @param key_value_pairs - list of unique arguments
     */
    public Sendgrid setUniqueArguments(JSONObject key_value_pairs) {
        try {
            header_list.put("unique_args", key_value_pairs);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return this;
    }

    /**
     * setFilterSettings - Set filter/app settings
     *
     * @param filter_settings - JSONObject of fiter settings
     */
    public Sendgrid setFilterSettings(JSONObject filter_settings) {
        try {
            header_list.put("filters", filter_settings);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return this;
    }

    /**
     * addFilterSetting - Append a filter setting to the list of filter settings
     *
     * @param  filter_name       filter name
     * @param  parameter_name    parameter name
     * @param  parameter_value   setting value
     * @throws JSONException
     */
    public Sendgrid addFilterSetting(String filter_name, String parameter_name, String parameter_value) throws JSONException {
        if (!header_list.has("filters")) {
            header_list.put("filters", new JSONObject());
        }
        if (!((JSONObject) header_list.get("filters")).has(filter_name)) {
            ((JSONObject) header_list.get("filters")).put(filter_name, new JSONObject());
        }
        if (!((JSONObject) ((JSONObject) header_list.get("filters")).get(filter_name)).has("settings")) {
            ((JSONObject) ((JSONObject) header_list.get("filters")).get(filter_name)).put("settings", new JSONObject());
        }
        ((JSONObject) ((JSONObject) ((JSONObject) header_list.get("filters")).get(filter_name)).get("settings"))
                .put(parameter_name, parameter_value);

        return this;
    }

    /**
     * getHeaders - return the list of headers
     *
     * @return JSONObject with headers
     */
    public JSONObject getHeaders() {
        return this.header_list;
    }

    /**
     * setHeaders - Sets the list headers
     * destroys previous header data
     *
     * @param  key_value_pairs   the list of header data
     * @return                   the SendGrid object.
     */
    public Sendgrid setHeaders(JSONObject key_value_pairs) {
        this.header_list = key_value_pairs;

        return this;
    }

    /**
     * getServerResponse - Get the server response message
     *
     * @return  The server response message
     */
    public String getServerResponse() {
        return this.serverResponse;
    }

    /**
     * arrayToUrlPart - Converts an ArrayList to a url friendly string
     *
     * @param  array   the array to convert
     * @param  token   the name of parameter
     * @return         a url part that can be concatenated to a url request
     */
    protected String arrayToUrlPart(List<String> array, String token) {
        String string = "";
        for(int i = 0;i < array.size();i++)
        {
            try {
                string += "&" + token + "[]=" + URLEncoder.encode(array.get(i), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return string;
    }

    /**
     * prepareMessageData - Takes the mail message and returns a url friendly querystring
     *
     * @return the data query string to be posted
     * @throws JSONException
     */
    private Map<String, String> prepareMessageData() throws JSONException {
        Map<String,String> params = new HashMap<>();
        params.put("api_user", username);
        params.put("api_key", password);
        params.put("subject", getSubject());
        if(this.getHtml() != null) {
            params.put("html", getHtml());
        }
        if(this.getFromName() != null) {
            params.put("fromname", getFromName());
        }
        params.put("from", getFrom());

        if (this.getReplyTo() != null) {
            params.put("replyto", getReplyTo());
        }

        params.put("to", this.getTos().toString());
        if (CollectionUtils.isNotEmpty(getToNames())) {
            params.put("toname", getToNames().toString());
        }

        return params;
    }

    /**
     * Invoked when a warning is returned from the server that
     * isn't critical
     */
    public interface WarningListener {
        void warning(String serverResponse, Throwable t);
    }

    /**
     * send - Send an email
     *
     * @throws JSONException
     */
    public void send() throws JSONException {
        send(new WarningListener() {
            public void warning(String w, Throwable t) {
                log.log(Level.SEVERE, w, t);

                serverResponse = w;
            }
        });
    }

    /**
     * send - Send an email
     *
     * @param w callback that will receive warnings
     * @throws JSONException
     */
    public void send(WarningListener w) throws JSONException {
        Map<String, String> data = prepareMessageData();

        StringBuffer requestParams = new StringBuffer();
        Iterator<String> paramIterator = data.keySet().iterator();
        while (paramIterator.hasNext()) {
            String key = paramIterator.next();
            String value = data.get(key);

            if ("to".equals(key) && CollectionUtils.isNotEmpty(this.getTos())) {
                requestParams.append(arrayToUrlPart(this.getTos(), "to")+"&");
            } else {
                if (key.equals("toname") && CollectionUtils.isNotEmpty(getToNames())) {
                    requestParams.append(arrayToUrlPart(this.getToNames(), "toname").substring(1)+"&");
                } else {
                    try {
                        requestParams.append(URLEncoder.encode(key, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        w.warning("Unsupported Encoding Exception", e);
                    }
                    requestParams.append("=");
                    try {
                        requestParams.append(URLEncoder.encode(value, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        w.warning("Unsupported Encoding Exception", e);
                    }
                    requestParams.append("&");
                }
            }
        }
        String request = domain + endpoint;

        if (CollectionUtils.isNotEmpty(getBccs())) {
            request += "?" +arrayToUrlPart(this.getBccs(), "bcc").substring(1);
        }
        try {
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(requestParams.toString());
            // Get the response
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line, response = "";

            while ((line = reader.readLine()) != null) {
                // Process line...
                response += line;
            }
            reader.close();
            writer.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // OK
                serverResponse = "success";
            } else {
                // Server returned HTTP error code.
                JSONObject apiResponse = new JSONObject(response);
                JSONArray errorsObj = (JSONArray) apiResponse.get("errors");
                for (int i = 0; i < errorsObj.length(); i++) {
                    if (i != 0) {
                        serverResponse += ", ";
                    }
                    serverResponse += errorsObj.get(i);
                }
                w.warning(serverResponse, null);
            }
        } catch (MalformedURLException e) {
            w.warning("Malformed URL Exception", e);
        } catch (IOException e) {
            w.warning("IO Exception", e);
        }
    }

    /**
     * _addToName - Append an recipient name to the existing list of names
     *
     * @param    name    Recipient name
     * @return           The SendGrid object.
     */
    private Sendgrid _addToName(String name) {
        this.to_name_list.add(name);

        return this;
    }
}
