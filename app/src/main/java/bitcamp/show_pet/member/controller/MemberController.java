package bitcamp.show_pet.member.controller;

import bitcamp.show_pet.config.KakaoConfig;
import bitcamp.show_pet.config.NcpConfig;
import bitcamp.show_pet.member.model.vo.Member;
import bitcamp.show_pet.member.model.vo.Role;
import bitcamp.show_pet.member.service.MemberService;
import bitcamp.show_pet.member.controller.KakaoAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;

@Controller
@RequestMapping("/member")
public class MemberController {

    @Autowired
    KakaoConfig kakaoConfig;

    @Autowired
    NcpConfig ncpConfig;

    @Autowired
    MemberService memberService;

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

        Member loginUser = memberService.get(email, password);
        if (loginUser == null) {
            model.addAttribute("refresh", "1;url=form");
            throw new Exception("회원 정보가 일치하지 않습니다.");
        }

        session.setAttribute("loginUser", loginUser);

        if (loginUser.getRole() == Role.ADMIN) {
            System.out.println(loginUser.getRole());
            return "redirect:/admin/form";
        }
        System.out.println("gd");
            return "redirect:/";

    }

    @GetMapping("/logout")
    public String logout(HttpSession session) throws Exception {

        session.invalidate();
        return "redirect:/";
    }

    KakaoAPI kakaoApi = new KakaoAPI();

    @RequestMapping(value = "kakaologin")
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
        mav.setViewName("/member/form");
        return mav;
    }

    @RequestMapping(value = "kakaologout")
    public ModelAndView logout(HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();

        kakaoApi.kakaoLogout((String) session.getAttribute("accessToken"));
        session.removeAttribute("accessToken");
        session.removeAttribute("kakaoUser");
        session.invalidate();
        mav.setViewName("/member/form");

        return mav;
    }


    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return kakaoConfig.getRestApi();
    }

    @GetMapping("/ntest")
    @ResponseBody
    public String ntest() {
        return ncpConfig.toString();
    }
}

