package com.nagornyi.uc.currency.loader;

import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;
import com.nagornyi.uc.appinfo.AppInfoLoadException;
import com.nagornyi.uc.appinfo.AppInfoLoader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Nagornyi
 * Date: 29.06.14
 */
public class PrivateBankLoader implements AppInfoLoader {
    private static final Logger LOG = Logger.getLogger(PrivateBankLoader.class.getName());
    private static final String JSON_LOAD_URI = "https://api.privatbank.ua/p24api/pubinfo?exchange&json&coursid=11";

    @Override
    public void load(Map<String, Object> map) throws AppInfoLoadException {
        try {
            URL url = new URL(JSON_LOAD_URI);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            BufferedReader br =
                    new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
            StringBuilder result = new StringBuilder();
            String input;
            while ((input = br.readLine()) != null){
                result.append(input);
            }
            br.close();

            JSONArray currenciesList = new JSONArray(result.toString());
            for (int i = 0, size = currenciesList.length(); i < size; i++) {
                JSONObject currencyRecord = currenciesList.getJSONObject(i);
                if ("EUR".equals(currencyRecord.getString("ccy")) && "UAH".equals(currencyRecord.getString("base_ccy"))) {
                    String saleRateStr = currencyRecord.getString("sale");
                    double saleRate = Double.parseDouble(saleRateStr);
                    map.put("currencyRate", saleRate);
                    LOG.info("Successfully loaded privat-bank currency rate " + saleRateStr);
                    break;
                }
            }

        } catch (JSONException | IOException e) {
            throw new AppInfoLoadException(e);
        }
    }
}
