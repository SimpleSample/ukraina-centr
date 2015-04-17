package com.nagornyi.uc.context;

import com.nagornyi.uc.common.DateFormatter;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.IUserDAO;
import com.nagornyi.uc.entity.User;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Enumeration;
import java.util.Locale;

/**
 * Additional info:
 *      Locale Language:
 *          http://www.iana.org/assignments/language-subtag-registry/language-subtag-registry
 *      Locale Region:
 *          https://www.iso.org/obp/ui/#search
 *
 * @author Nagornyi
 * Date: 29.06.14
 */
public class RequestContext {

    private static ThreadLocal<Locale> locale = new ThreadLocal<Locale>() {
        @Override
        protected Locale initialValue() {
            return DateFormatter.UK_LOCALE;
        }
    };

    private static ThreadLocal<SimpleDateFormat> formatter = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("d MMMM, yyyy kk:mm", locale.get());
        }
    };

    private static ThreadLocal<Currency> currency = new ThreadLocal<Currency>() {
        @Override
        protected Currency initialValue() {
            return Currency.getInstance(locale.get());
        }
    };

    private static ThreadLocal<User> user = new ThreadLocal<User>();

    public static void init(HttpServletRequest request) {
        if (request.getSession(false) == null) {
            setLocaleFromRequest(request);
        } else {
            if (request.getSession().getAttribute("email") == null) {
                setLocaleFromRequest(request);
            } else {
                String email = (String)request.getSession().getAttribute("email");
                User currentUser = ((IUserDAO)DAOFacade.getDAO(User.class)).getUserByEmail(email);
                setUser(currentUser);
                setLocale(currentUser.getUserLocale());
            }
        }
        if (getLocale().getLanguage().equals("it")) {
            setCurrency(Currency.getInstance("EUR"));
        }
        setFormatter(new SimpleDateFormat("d MMMM, yyyy kk:mm", getLocale()));
    }

    private static void setLocaleFromRequest(HttpServletRequest request) {
        Enumeration<Locale> locales = request.getLocales();
        while(locales.hasMoreElements()) {
            Locale l = locales.nextElement();
            if (containsLocale(l)) {
                setLocale(l);
                request.getSession().setAttribute("loc", l.getLanguage().toUpperCase());
                break;
            }
        }
    }

    private static boolean containsLocale(Locale locale) {
        String lang = locale.getLanguage();
        return lang.equals("uk") || lang.equals("en") || lang.equals("it") || lang.equals("ru");
    }

    public static Locale getLocale() {
        return locale.get();
    }

    public static void setLocale(Locale locale) {
        RequestContext.locale.set(locale);
    }

    public static SimpleDateFormat getFormatter() {
        return formatter.get();
    }

    public static void setFormatter(SimpleDateFormat formatter) {
        RequestContext.formatter.set(formatter);
    }

    public static Currency getCurrency() {
        return currency.get();
    }

    public static void setCurrency(Currency currency) {
        RequestContext.currency.set(currency);
    }

    public static User getUser() {
        return user.get();
    }

    public static void setUser(User user) {
        RequestContext.user.set(user);
    }

    public static synchronized void destroy() {
        user.remove();
        locale.remove();
        currency.remove();
        formatter.remove();
    }
}
