package cham.TestProjectFromSmartSoft.controller;

import cham.TestProjectFromSmartSoft.parser.XMLParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Map;

/**
 * Контроллер отвечающий за безопастность в сервисе
 * Осуществляет проверку авторизации пользователя и, при необходимости,
 * перебрасывает на страницы авторизации и регистрации
 */
@Controller
public class SecurityController {

    // Пустой запрос на сайт
    // Если пользователь не авторизован - редиректим на страницу с авторизацией
    // Если пользователь авторизован - редиректим на страницу с калькулятором валют
    @GetMapping("/")
    public String main(Map<String, Object> model){
        return "redirect:/login";
    }

    // Запрос на логинезацию пользователя
    @GetMapping("/login")
    public String login(Map<String, Object> model){
        return "login";
    }

    // Запрос на страницу для регистрации пользователя
    @GetMapping("/registration")
    public String registration(Map<String, Object> model){
        return "registration";
    }
}
