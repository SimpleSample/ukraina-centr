package com.nagornyi.uc.currency.loader;

import com.nagornyi.uc.appinfo.AppInfoLoadException;
import com.nagornyi.uc.appinfo.AppInfoLoader;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * @author Nagornyi
 * Date: 29.06.14
 */
public class PrivateBankLoader implements AppInfoLoader {

    private static String LOAD_URI = "https://api.privatbank.ua/p24api/pubinfo?exchange&coursid=3";
    private XPath xpath = XPathFactory.newInstance().newXPath();

    @Override
    public void load(Map<String, Object> map) throws AppInfoLoadException {
        try {
            URL url = new URL(LOAD_URI);
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(url.openStream());

            XPathExpression rateExpr = this.xpath.compile("/exchangerates/row/exchangerate[@ccy='EUR']/@buy");
            NodeList nodes = (NodeList) rateExpr.evaluate(doc, XPathConstants.NODESET);
            double rate = 0.0;
            if (nodes.getLength() > 0) {
                rate = new Double(nodes.item(0).getNodeValue());
            }

            if (rate != 0.0) {
                map.put("currencyRate", rate);
            } else {
                throw new AppInfoLoadException();
            }

        } catch (MalformedURLException e) {
            throw new AppInfoLoadException(e);
        } catch (ParserConfigurationException e) {
            throw new AppInfoLoadException(e);
        } catch (IOException e) {
            throw new AppInfoLoadException(e);
        } catch (SAXException e) {
            throw new AppInfoLoadException(e);
        } catch (XPathExpressionException e) {
            throw new AppInfoLoadException(e);
        }
    }
}
