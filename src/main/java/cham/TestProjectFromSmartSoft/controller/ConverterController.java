package cham.TestProjectFromSmartSoft.controller;

import cham.TestProjectFromSmartSoft.data.Currency;
import cham.TestProjectFromSmartSoft.parser.XMLParser;
import cham.TestProjectFromSmartSoft.repo.CurrencyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Контроллер отвечающий за заботу внутреннего сервиса
 * Осуществляет обработку данных внутри сервиса
 * Сохраняет историю и осуществляет отображение данных по конвертации валют
 */

@Controller
public class ConverterController {

    @Autowired
    private CurrencyRepo currencyRepo;

    // Запрос для открытия страницы конвертера
    @GetMapping("/converter")
    public String converter(Map<String, Object> model){
        List<Currency> currencies = search();
        model.put("seed", "0");
        model.put("currency_left", null);
        model.put("currency_right", null);
        model.put("currencies_left", currencies);
        model.put("currencies_right", currencies);
        model.put("result", "0");
        return "converter";
    }

    @PostMapping("/converter")
    public String converter(@RequestParam Integer left_id, @RequestParam Integer right_id,
                            @RequestParam(name = "left_val", required = false, defaultValue = "0") String left_val,
                            Map<String, Object> model){
        List<Currency> currencies_right = search();
        List<Currency> left = currencyRepo.findById(left_id);
        List<Currency> right = currencyRepo.findById(right_id);
        List<Currency> currencies_left = currencies_right;
        currencies_right.remove(right);
        currencies_left.remove(left);
        model.put("currency_left", left);
        model.put("currency_right",  right);
        model.put("currencies_left", currencies_left);
        model.put("currencies_right", currencies_right);
        model.put("seed", String.format("%.2f", Double.parseDouble(left_val)));
        model.put("result", String.format("%.2f", calculate(currencyRepo.findById(left_id).get(0).getRate(),
                currencyRepo.findById(right_id).get(0).getRate(), Double.parseDouble(left_val))));
        return "converter";
    }

    // Запрос для открытия страницы истории
    @GetMapping("/history")
    public String  history(Map<String, Object> model) {
        List<Currency> currencies = search();
        model.put("currencies", currencies);
        return "history";
    }

    // Встроенный метод для вычисления результата
    private Double calculate(Double f_c, Double s_c, Double val){
        return (val * f_c) / s_c;
    }

    // Встроенный метод для поиска актуальных данных о валюте
    private List<Currency> search(){
        // Формирование фильтра по сегоднешней дате
        Date dateNow = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy-MM-dd");
        // Запрос данных из БД курсов валют
        List<Currency> currencies = currencyRepo.findByUpdateDate(formatForDateNow.format(dateNow));

        // Проверка получены ли актуальные данные
        // Если не получены - обновляем и повторяем запрос курсов
        if(currencies.isEmpty()){
            update();
            currencies = currencyRepo.findByUpdateDate(formatForDateNow.format(dateNow));
        }

        return currencies;
    }

    // Втроенный метод для подгрузки актуальных курсов с сайта ЦБ РФ
    private void update(){
        // Создаем буффер для курсов валют
        ArrayList<Currency> currencies = null;
        try {
            // Запускаем парсер валют
            currencies = XMLParser.parse();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Загружаем результат в БД
        for (Currency currency: currencies)
            currencyRepo.save(currency);
    }
}
