package bitcamp.show_pet.user.controller;

import bitcamp.show_pet.user.model.vo.User;
import bitcamp.show_pet.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("form")
    public void form(@CookieValue(required = false) String email, Model model) {
        model.addAttribute("email", email);
    }

    @PostMapping("login")
    public String login(
            String email,
            String password,
            String saveEmail,
            HttpSession session,
            Model model,
            HttpServletResponse response) throws Exception {

        if (saveEmail != null) {
            Cookie cookie = new Cookie("email", email);
            response.addCookie(cookie);
        } else {
            Cookie cookie = new Cookie("email", "id");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }

        User loginUser = userService.get(email, password);
        if (loginUser == null) {
            model.addAttribute("refresh", "1;url=form");
            throw new Exception("회원 정보가 일치하지 않습니다.");
        }

        session.setAttribute("loginUser", loginUser);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) throws Exception {

        session.invalidate();
        return "redirect:/";
    }
}

