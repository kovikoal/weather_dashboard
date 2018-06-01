package com.site;

import javax.servlet.annotation.WebServlet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.*;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import java.io.File;
import java.text.DecimalFormat;
import org.apache.log4j.Logger;




@Push
public class MyUI extends UI {

    private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=24d664857584da3f3ddedb2a5b775dc6";
    private static final String CURRENCYLAYER_API = "http://www.apilayer.net/api/live?access_key=23dc0f02bc49dd96a8cc709cfd2f0389&currencies=RUB,EUR";
    private static final Logger Mainlog = Logger.getLogger(MyUI.class);
    private TextField weatherURL = new TextField();
    private TextField currencyURL = new TextField();
    private String city;
    private String icon_id;
    private Image coin = new Image();
    private Image icon = new Image();
    private Image background = new Image();
    private Label resultW = new Label();
    private Label resultC = new Label();
    private Label ip_addr = new Label();
    private Label time = new Label();
    private Label CurrencyDate = new Label();
    private Label counter = new Label();

    @Override
    protected void init(VaadinRequest vaadinRequest) {


        final VerticalLayout layout = new VerticalLayout();
            layout.setSizeFull();
            layout.setMargin(false);
        final VerticalLayout Weatherlayout = new VerticalLayout();
            Weatherlayout.setSpacing(true);
            Weatherlayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        final VerticalLayout CurrencyLayout = new VerticalLayout();
            CurrencyLayout.setSpacing(true);
            CurrencyLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        final VerticalLayout DataLayout = new VerticalLayout();
            DataLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        final HorizontalLayout hor = new HorizontalLayout();
        final VerticalLayout center = new VerticalLayout();
            center.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        final AbsoluteLayout backgroundImage = new AbsoluteLayout();
        final NativeSelect<String> select = new NativeSelect<>("Выберите город");
        select.setItems("Москва", "Уфа", "Новосибирск", "Хабаровск");
        select.setWidth("200px");
        Button buttonW = new Button("Обновить погоду");
        Button buttonC = new Button("Обновить курсы валют");

        if(database.mongoData() != null) {
            counter.setValue("Количество посещений: " + database.mongoData());
        }else counter.setValue("Ошибка подключения к базе данных");

        currencyURL.setValue(CURRENCYLAYER_API);
        currencyURL.setWidth("300px");
        weatherURL.setValue(OPEN_WEATHER_MAP_API);
        weatherURL.setWidth("300px");

        RequestWeather("Novosibirsk");
        RequestCurrency();

        if(ClientDataLoader.getIP() == null) {
            ip_addr.setValue("Не удалось определить IP адрес");
            Mainlog.error("FAILED TO GET IP");
        } else {
            ip_addr.setValue(ClientDataLoader.getIP());
            Mainlog.info("New Connection");
        }

        time.setValue(ClientDataLoader.getTime());


        buttonW.addClickListener(e -> {
            if(select.getValue() == null){
                city = "Novosibirsk";
            }
            else city = chooseCity(select.getValue());
            RequestWeather(city);
            Mainlog.info("Weather update request");
        });

        buttonC.addClickListener(e -> {
            RequestCurrency();
            Mainlog.info("Currency upgrade request");
        });

        Weatherlayout.addComponents(icon, resultW, select, buttonW, weatherURL);
        CurrencyLayout.addComponents(coin, resultC, CurrencyDate ,buttonC, currencyURL);
        DataLayout.addComponents(ip_addr, time, counter);
        hor.addComponents(Weatherlayout, DataLayout, CurrencyLayout);
        center.addComponent(hor);
        backgroundImage.addComponents(background, center);
        layout.addComponent(backgroundImage);
        setContent(layout);



    }

    private void RequestCurrency() {

        double USDRUB;
        double USDEUR;
        double RUBEUR;
        String id;
        String pattern = "##.##";
        String currencyOutput = "Курс USD/RUB - %s%nКурс EUR/RUB - %s%nКурс USD/EUR - %s";

        JsonObject currency = CurrensyDataLoader.getData(currencyURL.getValue());
        if(currency == null) {
            resultC.setValue(String.format("Сервис валют не отвечает,%nпроверьте корректность URL"));
            id = "fail";
        }
        else {
            id = "dollar";
            JsonObject json = currency.getAsJsonObject("quotes");
            USDRUB = json.get("USDRUB").getAsDouble();
            USDEUR = json.get("USDEUR").getAsDouble();
            RUBEUR = USDRUB/USDEUR;
            DecimalFormat Formatter = new DecimalFormat(pattern);
            String UR = Formatter.format(USDRUB);
            String ER = Formatter.format(RUBEUR);
            String UE = Formatter.format(USDEUR);
            resultC.setContentMode(ContentMode.PREFORMATTED);
            resultC.setValue(String.format(currencyOutput, UR, ER, UE));
        }
        CurrencyDate.setValue("Данные обновлены " + ClientDataLoader.getTime());
        setCoin(id);
    }

    private void setCoin(String id) {
        String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        coin.setSource(new FileResource(new File(basepath + "/WEB-INF/classes/images/" + id + ".png")));
        coin.setWidth("150px");
        coin.setHeight("150px");
    }

    private void RequestWeather(String city) {

        int humidity;
        double temp;
        double wind;
        String pattern = "##.#";
        String weatherOutput = "Влажность - %s%nТемпература - %s С %nСкорость ветра - %s м/с";

        JsonObject weather = WeatherDataLoader.getData(city, weatherURL.getValue());
        if(weather == null) {
            resultW.setValue(String.format("Сервис погоды не отвечает,%nпроверьте корректность URL"));
            Mainlog.error("FAILED TO UPDATE WEATHER");
            icon_id = "fail";
            setIcon(icon_id);
        }
        else {
            JsonObject json1 = weather.getAsJsonObject("main");
            JsonObject json2 = weather.getAsJsonObject("wind");
            JsonArray  json3 = weather.getAsJsonArray("weather");
            for (JsonElement j : json3) {
                JsonObject out = j.getAsJsonObject();
                icon_id = out.get("icon").getAsString();
            }
            wind = json2.get("speed").getAsDouble();
            humidity = json1.get("humidity").getAsInt();
            temp = json1.get("temp").getAsDouble() - 270;
            DecimalFormat Formatter = new DecimalFormat(pattern);
            String temperature = Formatter.format(temp);
            resultW.setContentMode(ContentMode.PREFORMATTED);
            resultW.setValue(String.format(weatherOutput, humidity, temperature, wind));
            setIcon(icon_id);
            setBackground(icon_id);
        }

    }

    private void setBackground(String id) {
        String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        String backgroundPath = null;
        switch(id){
            case ("01d") :  backgroundPath = "Day.jpg";
                break;
            case ("02d") :  backgroundPath = "Day.jpg";
                break;
            case ("03d") :  backgroundPath = "Day.jpg";
                break;
            case ("04d") :  backgroundPath = "Day.jpg";
                break;
            case ("09d") :  backgroundPath = "Day.jpg";
                break;
            case ("10d") :  backgroundPath = "Day.jpg";
                break;
            case ("11d") :  backgroundPath = "Day.jpg";
                break;
            case ("13d") :  backgroundPath = "Day.jpg";
                break;
            case ("50d") :  backgroundPath = "Day.jpg";
                break;
            case ("01n") :  backgroundPath = "Night.jpg";
                break;
            case ("02n") :  backgroundPath = "Night.jpg";
                break;
            case ("03n") :  backgroundPath = "Night.jpg";
                break;
            case ("04n") :  backgroundPath = "Night.jpg";
                break;
            case ("09n") :  backgroundPath = "Night.jpg";
                break;
            case ("10n") :  backgroundPath = "Night.jpg";
                break;
            case ("11n") :  backgroundPath = "Night.jpg";
                break;
            case ("13n") :  backgroundPath = "Night.jpg";
                break;
            case ("50n") :  backgroundPath = "Night.jpg";
                break;
        }
        FileResource b = new FileResource(new File(basepath + "/WEB-INF/classes/images/" + backgroundPath));
        background.setSource(b);
    }

    private void setIcon(String id) {
        String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        icon.setSource(new FileResource(new File(basepath + "/WEB-INF/classes/images/" + id + ".png")));
        icon.setWidth("200px");
        icon.setHeight("200px");
    }


    private String chooseCity(String value) {
        switch(value){
            case "Москва" : city = "Moscow";
                break;
            case "Новосибирск" : city = "Novosibirsk";
                break;
            case "Уфа" : city = "Ufa";
                break;
            case "Хабаровск" : city = "Khabarovsk";
                break;

        }
        return city;
    }


    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
