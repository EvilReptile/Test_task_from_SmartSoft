package cham.TestProjectFromSmartSoft.parser;

import cham.TestProjectFromSmartSoft.data.Currency;
import cham.TestProjectFromSmartSoft.repo.CurrencyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class XMLParser {
    //Ссылка на объект трансляции
    private static String URL = "http://www.cbr.ru/scripts/XML_daily.asp";

    //Метод парсинга дажнных с сайта
    public static ArrayList<Currency> parse() throws ParserConfigurationException, SAXException, IOException {

        // Создание массива результирующих данных
        ArrayList<Currency> currencies = new ArrayList<>();

        // Создание переменной нынешней даты
        Date dateNow = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy-MM-dd");
        String date = formatForDateNow.format(dateNow);

        // Создается построитель документа
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        // Создается дерево DOM документа из файла
        Document document = documentBuilder.parse(URL);

        //Получаем дочерние элементы главного элемента (Получаем Valuet элементы)
        NodeList valutes = document.getDocumentElement().getChildNodes();

        //Пребираем все полученные элементы
        for(int i = 0; i < valutes.getLength(); i++) {
            Node valute = valutes.item(i);

            //Проверяем, что это является элементом
            if(valute.getNodeType() != Node.TEXT_NODE) {

                //Получаем дочерние элементы Valute
                NodeList params = valute.getChildNodes();

                //Преобразовываем значение элемента Value в транслируемый для Float
                char[] rateArray = params.item(4).getChildNodes().item(0).getTextContent().toCharArray();
                String rate = "";
                for(char sumbol : rateArray)
                    if(sumbol != ',')
                        rate += sumbol;
                    else
                        rate += '.';

                //Добавляем валюту в таблицу
                currencies.add(new Currency(params.item(1).getChildNodes().item(0).getTextContent() + "(" + params.item(3).getChildNodes().item(0).getTextContent() + ") ", Double.parseDouble(rate), date));
            }
        }
        return currencies;
    }

}
