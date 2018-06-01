package com.site;

import com.vaadin.server.Page;
import com.vaadin.server.WebBrowser;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ClientDataLoader {

    private static final Logger ClientDataLog = Logger.getLogger(ClientDataLoader.class);

    public static String getTime(){
       try {
           DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm");
           DateTimeZone zone = DateTimeZone.forID("Asia/Krasnoyarsk");
           DateTime serverNow = DateTime.now(zone);
           DateTime browserCurrentDateTime = new DateTime(serverNow, zone);
           String time = fmt.print(browserCurrentDateTime);
           return time;
       }
       catch (Exception e) {
           ClientDataLog.error("FAILED TO UPDATE TIME");
           return  null;
       }
    }

    public static String getIP(){
        try {
            WebBrowser webBrowser = Page.getCurrent().getWebBrowser();

            String ip_addr = new String();
            if (webBrowser.getAddress().equals("0:0:0:0:0:0:0:1")) {
                ip_addr = ("Ваш IP адрес - 127.0.0.1");
            } else {
                ip_addr = ("Ваш IP адрес - " + webBrowser.getAddress());
            }
            return ip_addr;
        }
        catch (Exception e) {
            return null;
        }
    }
}
