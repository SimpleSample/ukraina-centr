package com.nagornyi.uc;

import com.nagornyi.env.EnvVariablesStorage;
import com.nagornyi.uc.cache.CacheManager;
import com.nagornyi.uc.templates.TemplatesManager;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * @author Nagornyi
 * Date: 30.06.14
 */
public class InitServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        CacheManager.initCache();
        TemplatesManager.INSTANCE.init();
        EnvVariablesStorage.loadEnvVariables();
    }
}
