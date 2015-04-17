package com.nagornyi.uc.currency;

import com.nagornyi.uc.appinfo.AppInfoManager;
import com.nagornyi.uc.currency.loader.BankUaComLoader;
import com.nagornyi.uc.currency.loader.PrivateBankLoader;
import com.nagornyi.uc.currency.loader.RateExchangeAppspotLoader;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Nagornyi
 * Date: 29.06.14
 */
public class UpdateCurrencyServlet extends HttpServlet {
    private static Logger log = Logger.getLogger(UpdateCurrencyServlet.class.getName());
    private static boolean initialized = false;
    private String currencyLoader;

    @Override
    public void init(ServletConfig config) throws ServletException {
        log.info("Initializing UpdateCurrencyServlet");
        if (!initialized) {
            AppInfoManager.getInstance().registerLoader("bankUaCom", new BankUaComLoader());
            AppInfoManager.getInstance().registerLoader("privateBank", new PrivateBankLoader());
            AppInfoManager.getInstance().registerLoader("rateExchange", new RateExchangeAppspotLoader());

            currencyLoader = config.getInitParameter("currencyLoader");
            load(currencyLoader);
            initialized = true;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String loader = currencyLoader != null? currencyLoader : getServletConfig().getInitParameter("currencyLoader");
        load(loader);
    }

    private void load(String currencyLoader) {
        if (currencyLoader != null) {
            AppInfoManager.getInstance().loadInfo(currencyLoader);
        } else {
            log.warning("Currency rates loading unavailable. Currency loader is not defined");
        }
    }
}
