package cham.TestProjectFromSmartSoft.parser;

import cham.TestProjectFromSmartSoft.data.Currency;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Класс реализации парсинга данных с сайта
 * Работает по принципу загрузки XML документа с дальнейшим
 * разбиением на подъелементы и парсинга нужных
 */
public class XMLParser {

    //Метод парсинга дажнных с сайта
    public static ArrayList<Currency> parse() throws ParserConfigurationException, SAXException, IOException {
        //Ссылка на объект трансляции
        String URL = getURL();

        // Если конфиг файл пустой - заполняем дефолтным значением
        if(URL.isEmpty())
            URL = "http://www.cbr.ru/scripts/XML_daily.asp";

        // Создание буффера валют
        ArrayList<Currency> currencies = new ArrayList<>();
        // Получение сегоднешней даты
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

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

    // Встроенный метод для получения адреса поиска
    private static String getURL(){
        // Буффер для возвращаемого значения
        String output = "";

        // Получение конфигурации
        try(FileReader reader = new FileReader("config.txt")) {
            // Считываем файл посимвольно
            int c;
            while((c=reader.read())!=-1){
                output += (char)c;
            }
        }catch(IOException e){
            System.out.println("WARNING: " + e.getMessage());
            return "";
        }

        // Если конфиг файл пуст - возвращаем пустое значение
        if(output.equals(""))
            return "";
        // Если в конфиге есть параметры - возвращаем адрес для поиска
        else
            return output.split("=")[1];
    }

}
