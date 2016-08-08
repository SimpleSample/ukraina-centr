package com.nagornyi.uc.templates;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.tofu.SoyTofu;

import java.io.File;

/**
 * @author Nagornyi
 * Date: 23.06.14
 */
public class TemplatesManager {
    public static TemplatesManager INSTANCE = new TemplatesManager();
    private SoyTofu tofu;

    private TemplatesManager() {
    }

    public void init() {

        // Bundle the Soy files for your project into a SoyFileSet.
        SoyFileSet sfs = new SoyFileSet.Builder()
                .add(new File("WEB-INF/templates/greetingsHeader.soy"))
                .add(new File("WEB-INF/templates/emailFooter.soy"))
                .add(new File("WEB-INF/templates/emailEmptyLine.soy"))
                .add(new File("WEB-INF/templates/ticketsList.soy"))
                .add(new File("WEB-INF/templates/renewPass.soy"))
                .add(new File("WEB-INF/templates/reserveFail.soy"))
                .add(new File("WEB-INF/templates/liqpayPurchaseFailed.soy"))
                .add(new File("WEB-INF/templates/reservationTimedOut.soy"))
                .add(new File("WEB-INF/templates/reserve.soy")).build();

        // Compile the template into a SoyTofu object.
        // SoyTofu's newRenderer method returns an object that can render any template in the file set.
        tofu = sfs.compileToTofu();

        // Render the template
//        System.out.println(tofu.newRenderer("templates.reserve").setData(data).render());
    }

    public String renderTemplate(String name, SoyMapData params) {
        if (tofu == null) init();

        return tofu.newRenderer(name).setData(params).render();
    }
}
