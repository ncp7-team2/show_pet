package bitcamp.show_pet.member.controller;

import bitcamp.show_pet.NcpObjectStorageService;
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
import org.springframework.web.multipart.MultipartFile;
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

    @Autowired
    NcpObjectStorageService ncpObjectStorageService;

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

    @GetMapping("join")
    public void join() {

    }

    @PostMapping("add")
    public String add(Member member) throws Exception {
        memberService.add(member);
        return "redirect:form";
    }

    @GetMapping("delete")
    public String delete(int id, Model model) throws Exception {
        if (memberService.delete(id) == 0) {
            model.addAttribute("refresh", "2;url=../post/list");
            throw new Exception("해당 회원이 없습니다.");
        }
        return "redirect:../post/list";
    }


    @GetMapping("list")
    public void list(Model model) throws Exception {
        model.addAttribute("list", memberService.list());
    }

    @GetMapping("myPage")
    public void myPage(HttpSession session, Model model) throws Exception {
        Member loginUser = (Member) session.getAttribute("loginUser");
        if (loginUser != null) {
            model.addAttribute("member", memberService.get(loginUser.getId()));
        }
    }

    @GetMapping("detail/{id}")
    public void detail(@PathVariable int id, Model model) throws Exception {
        Member member = memberService.get(id);
        if (member != null) {
            model.addAttribute("member", member);
        } else {
            throw new Exception("회원이 없습니다.");
        }
    }

    @GetMapping("profile/{id}")
    public void profile(@PathVariable int id, Model model) throws Exception {
        Member member = memberService.get(id);
        if (member != null) {
            model.addAttribute("member", member);
        } else {
            throw new Exception("회원이 없습니다.");
        }
    }

    @PostMapping("update")
    public String update(
            Member member,
            MultipartFile photofile) throws Exception {

        if (photofile.getSize() > 0) {
            String uploadFileUrl = ncpObjectStorageService.uploadFile(
                    "bitcamp-nc7-bucket-16", "member/", photofile);
            member.setPhoto(uploadFileUrl);
        }

        if (memberService.update(member) == 0) {
            throw new Exception("회원이 없습니다.");
        } else {
            return "redirect:../post/list";
        }
    }
}

