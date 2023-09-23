package bitcamp.show_pet.member.controller;

import bitcamp.show_pet.NcpObjectStorageService;
import bitcamp.show_pet.config.KakaoConfig;
import bitcamp.show_pet.config.NcpConfig;
import bitcamp.show_pet.mail.EmailService;
import bitcamp.show_pet.member.model.vo.Member;
import bitcamp.show_pet.member.model.vo.Role;
import bitcamp.show_pet.member.service.MemberService;
import bitcamp.show_pet.post.model.vo.Post;
import bitcamp.show_pet.post.service.PostService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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

  private final EmailService emailService;

  public MemberController(MemberService memberService, EmailService emailService) {
    this.memberService = memberService;
    this.emailService = emailService;
  }

  @Autowired
  KakaoConfig kakaoConfig;

  @Autowired
  NcpConfig ncpConfig;

  @Autowired
  MemberService memberService;

  @Autowired
  NcpObjectStorageService ncpObjectStorageService;

  @Autowired
  PostService postService;

  @GetMapping("form")
  public void form(@CookieValue(required = false) String email, Model model) {
    model.addAttribute("email", email);
  }

  // 로그인
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

  // 로그아웃
  @GetMapping("/logout")
  public String logout(HttpSession session) throws Exception {

    session.invalidate();
    return "redirect:/";
  }

  // 카카오 로그인
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

  // 카카오 로그아웃
  @RequestMapping(value = "kakaologout")
  public ModelAndView logout(HttpSession session, HttpServletResponse response,
      HttpServletRequest request) {
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


  // 회원가입
  @PostMapping("add")
  public String add(Member member) throws Exception {
    memberService.add(member);

    // 회원가입 이메일 전송
    emailService.sendWelcomeEmail(member);

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

  @GetMapping("profile/{memberId}")
  public String viewProfile(@PathVariable int memberId, Model model, HttpSession session)
      throws Exception {
    Member loginUser = (Member) session.getAttribute("loginUser");
    if (loginUser == null) {
      return "redirect:/member/form";
    }
    List<Post> likedPosts;
    List<Post> bookmarkedPosts;
    List<Member> followersList;
    List<Member> followingsList;

    if (loginUser.getId() == memberId) {
      likedPosts = postService.getLikedPosts(loginUser.getId(), session);
      bookmarkedPosts = postService.getBookmarkedPosts(loginUser.getId(), session);
      followersList = memberService.getFollowers(loginUser.getId());
      followingsList = memberService.getFollowings(loginUser.getId());
    } else {
      likedPosts = postService.getLikedPosts(memberId, session);
      bookmarkedPosts = postService.getBookmarkedPosts(memberId, session);
      followersList = memberService.getFollowers(memberId);
      followingsList = memberService.getFollowings(memberId);
    }

    model.addAttribute("followersList", followersList);
    model.addAttribute("followerCount", followersList.size());
    model.addAttribute("followingsList", followingsList);
    model.addAttribute("followingsCount", followingsList.size());

        model.addAttribute("member", memberService.get(memberId));
        model.addAttribute("myPosts", postService.getMyPosts(memberId));
        model.addAttribute("likedPosts", postService.getLikedPosts(memberId, session));
        model.addAttribute("bookMarkedPosts", postService.getBookmarkedPosts(memberId, session));

        return "member/profile";
    }

  @GetMapping("detail/{id}")
  public String detail(@PathVariable int id, Model model) throws Exception {
    model.addAttribute("member", memberService.get(id));
    return "member/detail";
  }


  @PostMapping("update")
  public String update(
      Member member,
      HttpSession session,
      MultipartFile photofile) throws Exception {

    if (photofile.getSize() > 0) {
      String uploadFileUrl = ncpObjectStorageService.uploadFile(
          "bitcamp-nc7-bucket-16", "member/", photofile);
      member.setPhoto(uploadFileUrl);
    }

    if (memberService.update(member) == 0) {
      throw new Exception("회원이 없습니다.");
    } else {
      session.setAttribute("loginUser", member);
      return "redirect:../post/list";
    }

  }

  @PostMapping("/{memberId}/follow")
  @ResponseBody
  public Map<String, Object> memberFollow(@PathVariable int memberId, HttpSession session)
      throws Exception {
    System.out.println("컨트롤러 멤버팔로우 호출됨!");
    Map<String, Object> response = new HashMap<>();
    Member loginUser = (Member) session.getAttribute("loginUser");
    if (loginUser == null) {
      response.put("status", "notLoggedIn");
      return response;
    }
    int currentMemberId = loginUser.getId();
    boolean newIsFollowed = memberService.memberFollow(currentMemberId, memberId);
    response.put("newIsFollowed", newIsFollowed);
    return response;
  }

  // 팔로우 상태 확인
  @PostMapping("/getFollowStatus")
  @ResponseBody
  public Map<Integer, Boolean> getFollowStatus(@RequestBody List<Integer> memberIds,
      HttpSession session)
      throws Exception {
    System.out.println("컨트롤러 팔로우상태확인 호출됨!");
    Member loginUser = (Member) session.getAttribute("loginUser");
    Map<Integer, Boolean> response = new HashMap<>();
    if (loginUser != null) {
      int currentMemberId = loginUser.getId();
      for (int memberId : memberIds) {
        boolean isFollowing = memberService.isFollowed(currentMemberId, memberId);
        response.put(memberId, isFollowing);
      }
    }
    return response;
  }

  @GetMapping("/followers")
  public String followers(HttpSession session, Model model) throws Exception {
    Member loginUser = (Member) session.getAttribute("loginUser");
    if (loginUser == null) {
      return "redirect:/member/form";
    }
    List<Member> followersList = memberService.getFollowers(loginUser.getId());
    model.addAttribute("followersList", followersList);
    model.addAttribute("followerCount", followersList.size());
    return "member/followers";
  }

  @GetMapping("/followings")
  public String followings(HttpSession session, Model model) throws Exception {
    Member loginUser = (Member) session.getAttribute("loginUser");
    if (loginUser == null) {
      return "redirect:/member/form";
    }
    List<Member> followingsList = memberService.getFollowings(loginUser.getId());
    model.addAttribute("followingsList", followingsList);
    model.addAttribute("followingsCount", followingsList.size()); // 팔로잉 숫자 추가
    return "member/followings";
  }
}

