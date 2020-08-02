package cham.TestProjectFromSmartSoft.controller;

import cham.TestProjectFromSmartSoft.data.Currency;
import cham.TestProjectFromSmartSoft.data.History;
import cham.TestProjectFromSmartSoft.parser.XMLParser;
import cham.TestProjectFromSmartSoft.repo.CurrencyRepo;
import cham.TestProjectFromSmartSoft.repo.HistoryRepo;
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
import java.util.*;

/**
 * Контроллер отвечающий за заботу внутреннего сервиса
 * Осуществляет обработку данных внутри сервиса
 * Сохраняет историю и осуществляет отображение данных по конвертации валют
 */

@Controller
public class ConverterController {

    @Autowired
    private CurrencyRepo currencyRepo;
    @Autowired
    private HistoryRepo historyRepo;

    // Запрос для открытия страницы конвертера первый раз
    @GetMapping("/converter")
    public String converter(Map<String, Object> model){
        // Получение списка валют
        List<Currency> currencies = search();

        // Заполнение места для выбранной валюты для конвертации, по дефолту null
        model.put("currency_left", null);
        model.put("currency_right", null);

        // Заполнение списка валют
        model.put("currencies_left", currencies);
        model.put("currencies_right", currencies);

        // Заполнение дефолтными значениями строк числа для конвертации и результата конвертации
        model.put("seed", "0");
        model.put("result", "0");
        return "converter";
    }

    // Запрос для открытия страницы конвертера после начала конвертации
    @PostMapping("/converter")
    public String converter(@RequestParam Integer left_id, @RequestParam Integer right_id,
                            @RequestParam(name = "left_val", required = false, defaultValue = "0") String left_val,
                            Map<String, Object> model){
        // Полученеи списка всех доступных валют для конвертации
        List<Currency> currencies_right = search();
        List<Currency> currencies_left = currencies_right;

        // Получение валют, учавствующих в конвертации
        List<Currency> left = currencyRepo.findById(left_id);
        List<Currency> right = currencyRepo.findById(right_id);

        // Удаление валют учавствующих в конвертации из списка доступныз для выбора валют
        currencies_right.remove(right);
        currencies_left.remove(left);

        // Запосление полей выбранных валют
        model.put("currency_left", left);
        model.put("currency_right",  right);

        // Заполнение списка валют
        model.put("currencies_left", currencies_left);
        model.put("currencies_right", currencies_right);

        // Заполнение значениями строк числа для конвертации и результата конвертации
        // с форматированием до двух знаков после запятой
        String seed = String.format(Locale.ROOT, "%.2f", Double.parseDouble(left_val));
        String result = String.format(Locale.ROOT, "%.2f",
                calculate(currencyRepo.findById(left_id).get(0).getRate(),
                currencyRepo.findById(right_id).get(0).getRate(), Double.parseDouble(left_val)));
        model.put("seed", seed);
        model.put("result", result);

        // Заполнение истории конвертаций
        historyRepo.save(new History(left.get(0).getName(), right.get(0).getName(),
                seed, result, dateNow()));
        return "converter";
    }

    // Запрос для открытия страницы c историей и стандартными параметрами
    @GetMapping("/history")
    public String  history(Map<String, Object> model) {
        // Получение списка истории по дате сейчас
        List<Currency> currencies = search();

        // Получение списков валют для фильтров
        List<Currency> currency = Collections.singletonList(new Currency("Все", 0.0, "0"));
        List<History> history = historyRepo.findByCurrencyLeftContainingAndCurrencyRightContainingAndDateConvert
                ("", "", dateNow());

        // Заполнение дефолтных значений фильтра валют
        model.put("currency_left", currency);
        model.put("currency_right", currency);

        // Заполнение значений списка валют
        model.put("currencies_left", currencies);
        model.put("currencies_right", currencies);

        // Заполнение дефолтного значения фильтра по дате
        model.put("date", dateNow());

        // Выгрузка отфильтрованной истории
        model.put("history", history);
        return "history";
    }

    @PostMapping("/history")
    public String history(@RequestParam String left_cur, @RequestParam String right_cur,
                          @RequestParam String date, Map<String, Object> model){

        // Заполнение текущих значений фильтров по валютам
        List<Currency> left = Collections.singletonList(new Currency(left_cur, 0.0, "0"));
        List<Currency> right = Collections.singletonList(new Currency(right_cur, 0.0, "0"));
        model.put("currency_left", left);
        model.put("currency_right", right);

        // Проверка на фильтр по всем валютам
        if(left_cur.equals("Все"))
            left_cur = "";
        if(right_cur.equals("Все"))
            right_cur = "";

        // Получение списка всех валют
        List<Currency> currencies_left = search();
        currencies_left.add(new Currency("Все", 0.0, "0"));
        List<Currency> currencies_right = currencies_left;

        // Удаление элементов, стоящих в выборе фильтра
        currencies_left.remove(left);
        currencies_right.remove(right);

        // Заполнение списка фильтров
        model.put("currencies_left", currencies_left);
        model.put("currencies_right", currencies_right);

        // Заполнение фильтра по дате
        model.put("date", date);

        // Выгрузка отфильтрованной истории
        model.put("history",
                historyRepo.findByCurrencyLeftContainingAndCurrencyRightContainingAndDateConvert(left_cur,
                        right_cur, date));

        return "history";
    }

    // Встроенный метод для получения даты сейчас
    private String dateNow(){
        Date dateNow = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy-MM-dd");
        return formatForDateNow.format(dateNow);
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

        currencyRepo.save(new Currency("RUB(Российский рубль)", 1.0, dateNow()));
    }
}
