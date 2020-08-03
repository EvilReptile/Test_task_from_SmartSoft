package cham.TestProjectFromSmartSoft.controller;

import cham.TestProjectFromSmartSoft.data.Role;
import cham.TestProjectFromSmartSoft.data.Users;
import cham.TestProjectFromSmartSoft.repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.Map;

/**
 * Контроллер отвечающий за безопастность в сервисе
 * Осуществляет проверку авторизации пользователя и, при необходимости,
 * перебрасывает на страницы авторизации и регистрации
 */
@Controller
public class MainController {

    @Autowired
    private UsersRepo usersRepo;

    // Пустой запрос на сайт
    // Если пользователь не авторизован - редиректим на страницу с авторизацией
    // Если пользователь авторизован - редиректим на страницу с калькулятором валют
    @GetMapping("/")
    public String main(Map<String, Object> model){
        return "redirect:/converter";
    }

    // Запрос на пустую страницу для регистрации пользователя
    @GetMapping("/registration")
    public String registration(Map<String, Object> model){
        model.put("error", "");
        return "registration";
    }

    // Запрос на страницу для регистрации пользователей
    @PostMapping("/registration")
    public String registration(@RequestParam String username, @RequestParam String password, Map<String, Object> model){
        // Проверяем наличие пользователей с заданным username-ом
        Users users = usersRepo.findByUsername(username);

        // В случае, если такой username уже есть - отправляем сообщение об ошибке и форму регистрации
        if(users != null){
            model.put("error", "Такой пользователь уже существует");
            return "registration";
        }

        // Если username свободен - сохраняем запись в базу данных
        usersRepo.save(new Users(username, password, true, Collections.singleton(Role.USER)));

        return "redirect:/login";
    }
}
