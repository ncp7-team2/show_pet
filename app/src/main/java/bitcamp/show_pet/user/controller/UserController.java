package bitcamp.show_pet.user.controller;

import bitcamp.show_pet.user.model.vo.Role;
import bitcamp.show_pet.user.model.vo.User;
import bitcamp.show_pet.user.service.UserService;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.servlet.ModelAndView;

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

        if (loginUser.getRole() == Role.ADMIN) {
            System.out.println(loginUser.getRole());
            return "redirect:/admin/form";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) throws Exception {

        session.invalidate();
        return "redirect:/";
    }

    KakaoAPI kakaoApi = new KakaoAPI();

    @RequestMapping(value = "login")
    public ModelAndView login(String code, HttpSession session) {
        ModelAndView mav = new ModelAndView();
        // 1번 인증코드 요청 전달
        String accessToken = kakaoApi.getAccessToken(code);
        // 2번 인증코드로 토큰 전달
        HashMap<String, Object> userInfo = kakaoApi.getUserInfo(accessToken);

        System.out.println("login info : " + userInfo.toString());

        if (userInfo.get("email") != null) {
            session.setAttribute("kakaoUser", userInfo.get("email"));
            session.setAttribute("accessToken", accessToken);
        }
        mav.addObject("kakaoUser", userInfo.get("email"));
        mav.addObject("userNickname", userInfo.get("nickname"));
        mav.setViewName("/user/form");
        return mav;
    }

    @RequestMapping(value = "logout")
    public ModelAndView logout(HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();

        kakaoApi.kakaoLogout((String) session.getAttribute("accessToken"));
        session.removeAttribute("accessToken");
        session.removeAttribute("kakaoUser");
        session.invalidate();
        mav.setViewName("/user/form");

        return mav;
    }
}

