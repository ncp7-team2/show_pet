package bitcamp.show_pet.post.controller;

import bitcamp.show_pet.NcpObjectStorageService;
import bitcamp.show_pet.post.model.vo.AttachedFile;
import bitcamp.show_pet.post.model.vo.Post;
import bitcamp.show_pet.post.service.PostService;
import bitcamp.show_pet.member.model.vo.Member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdk.jshell.spi.ExecutionControlProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

@Controller
@RequestMapping("/post")
public class PostController {

    @Autowired
    PostService postService;

    @Autowired
    NcpObjectStorageService ncpObjectStorageService;

    @GetMapping("form")
    public void form() {
    }

    @PostMapping("add")
    public String add(Post post, MultipartFile[] files, HttpSession session) throws Exception {

        Member loginUser = (Member) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "/member/form";
        }
        post.setMember(loginUser);

        ArrayList<AttachedFile> attachedFiles = new ArrayList<>();
        for (MultipartFile part : files) {
            if (part.getSize() > 0) {
                String uploadFileUrl = ncpObjectStorageService.uploadFile(
                        "bitcamp-nc7-bucket-16", "post/", part);
                AttachedFile attachedFile = new AttachedFile();
                attachedFile.setFilePath(uploadFileUrl);
                attachedFiles.add(attachedFile);
            }
        }
        post.setAttachedFiles(attachedFiles);

        postService.add(post);
        return "redirect:/post/list?category=" + post.getCategory();
    }

    @GetMapping("delete")
    public String delete(int id, int category, HttpSession session) throws Exception {
        Member loginUser = (Member) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/member/form";
        }

        Post p = postService.get(id);

        if (p == null || p.getMember().getId() != loginUser.getId()) {
            throw new Exception("해당 번호의 게시글이 없거나 삭제 권한이 없습니다.");
        } else {
            postService.delete(p.getId());
            return "redirect:/post/list?category=" + category;
        }
    }

    @GetMapping("list")
    public void list(
            @RequestParam(name = "search", required = false)
            String searchKeyword,
            Model model, HttpSession session) throws Exception {
        model.addAttribute("list", postService.list(session));
        model.addAttribute("searchKeyword", searchKeyword);

    }

    @GetMapping("listEtc")
    public void listEtc(
            @RequestParam(name = "search", required = false)
            String searchKeyword,
            Model model) throws Exception {
        model.addAttribute("listEtc", postService.listEtc());
        model.addAttribute("searchKeyword", searchKeyword);
    }

    @GetMapping("listDog")
    public void listDog(
            @RequestParam(name = "search", required = false)
            String searchKeyword,
            Model model) throws Exception {
        model.addAttribute("listDog", postService.listDog());
        model.addAttribute("searchKeyword", searchKeyword);
    }

    @GetMapping("listCat")
    public void listCat(
            @RequestParam(name = "search", required = false)
            String searchKeyword,
            Model model) throws Exception {
        model.addAttribute("listCat", postService.listCat());
        model.addAttribute("searchKeyword", searchKeyword);
    }

    @GetMapping("listBird")
    public void listBird(
            @RequestParam(name = "search", required = false)
            String searchKeyword,
            Model model) throws Exception {
        model.addAttribute("listBird", postService.listBird());
        model.addAttribute("searchKeyword", searchKeyword);
    }

    @GetMapping("detail/{category}/{id}")
    public String detail(@PathVariable int id, Model model, HttpSession session) throws Exception {
        System.out.println("detail 호출! PostController");
        Post post = postService.setSessionStatus(id, session);
        Member loginUser = (Member) session.getAttribute("loginUser");
        boolean isLoggedIn = (loginUser != null);
        model.addAttribute("isLoggedIn", isLoggedIn);
        if (post != null) {
            model.addAttribute("post", post);
        }
        return "post/detail";
    }

    @PostMapping("update")
    public String update(Post post, MultipartFile[] files, HttpSession session) throws Exception {
        Member loginUser = (Member) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/auth/form";
        }

        Post p = postService.get(post.getId());
        if (p == null || p.getMember().getId() != loginUser.getId()) {
            throw new Exception("게시글이 존재하지 않거나 변경 권한이 없습니다.");
        }

        ArrayList<AttachedFile> attachedFiles = new ArrayList<>();
        for (MultipartFile part : files) {
            if (part.getSize() > 0) {
                String uploadFileUrl = ncpObjectStorageService.uploadFile(
                        "bitcamp-nc7-bucket-16", "post/", part);
                AttachedFile attachedFile = new AttachedFile();
                attachedFile.setFilePath(uploadFileUrl);
                attachedFiles.add(attachedFile);
            }
        }
        post.setAttachedFiles(attachedFiles);

        postService.update(post);
        return "redirect:/post/list?category=" + p.getCategory();

    }

    @GetMapping("fileDelete/{attachedFile}") // 예) .../fileDelete/attachedFile;no=30
    public String fileDelete(
            @MatrixVariable("id") int id,
            HttpSession session) throws Exception {

        Member loginUser = (Member) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/auth/form";
        }

        Post post = null;

        AttachedFile attachedFile = postService.getAttachedFile(id);
        post = postService.get(attachedFile.getPostId());
        if (post.getMember().getId() != loginUser.getId()) {
            throw new Exception("게시글 변경 권한이 없습니다!");
        }

        if (postService.deleteAttachedFile(id) == 0) {
            throw new Exception("해당 번호의 첨부파일이 없다.");
        } else {
            return "redirect:/post/detail/" + post.getCategory() + "/" + post.getId();
        }
    }

    @PostMapping("/{postId}/like")
    @ResponseBody
    public Map<String, Object> postLike(@PathVariable int postId, HttpSession session)
            throws Exception {
        Map<String, Object> response = new HashMap<>();
        Member loginUser = (Member) session.getAttribute("loginUser");
        if (loginUser == null) {
            response.put("status", "notLoggedIn");
            return response;
        }
        int memberId = loginUser.getId();
        boolean isLiked = postService.postLike(postId, memberId);
        int newLikeCount = postService.getLikeCount(postId);
        response.put("newIsLiked", isLiked);
        response.put("newLikeCount", newLikeCount);
        return response;
    }

    @GetMapping("/liked")
    public String getLikedPosts(Model model, HttpSession session) throws Exception {
        Member loginUser = (Member) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/member/form";
        }
        int memberId = loginUser.getId();
        List<Post> posts = postService.getLikedPosts(memberId, session);
        model.addAttribute("likedPosts", posts);
        return "/post/likeList";
    }

    @PostMapping("/getLikeStatus")
    @ResponseBody
    public Map<Integer, Map<String, Object>> getLikeStatus(@RequestBody List<Integer> postIds, HttpSession session)
            throws Exception {
        System.out.println("좋아요 상태 정보 업데이트!");
        Member loginUser = (Member) session.getAttribute("loginUser");
        Map<Integer, Map<String, Object>> response = new HashMap<>();

        if (loginUser != null) {
            int memberId = loginUser.getId();

            for (int postId : postIds) {
                boolean isLiked = postService.isLiked(postId, memberId);
                int likeCount = postService.getLikeCount(postId);

                Map<String, Object> postStatus = new HashMap<>();
                postStatus.put("isLiked", isLiked);
                postStatus.put("likeCount", likeCount);

                response.put(postId, postStatus);
            }
        }
        return response;
    }

    @PostMapping("/{postId}/bookmark")
    @ResponseBody
    public Map<String, Object> postBookmark(@PathVariable int postId, HttpSession session)
            throws Exception {
        Map<String, Object> response = new HashMap<>();
        Member loginUser = (Member) session.getAttribute("loginUser");
        if (loginUser == null) {
            response.put("status", "notLoggedIn");
            return response;
        }

        int memberId = loginUser.getId();
        boolean newIsBookmarked = postService.postBookmark(postId, memberId);
        response.put("newIsBookmarked", newIsBookmarked);
        return response;
    }

    @GetMapping("/bookmarked")
    public String getBookmarkedPosts(Model model, HttpSession session) throws Exception {
        Member loginUser = (Member) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/member/form";
        }
        int memberId = loginUser.getId();
        List<Post> posts = postService.getBookmarkedPosts(memberId, session);
        model.addAttribute("bookmarkedPosts", posts);
        return "/post/bookmarkList";
    }

    @PostMapping("/getBookmarkStatus")
    @ResponseBody
    public Map<Integer, Boolean> getBookmarkStatus(@RequestBody List<Integer> postIds, HttpSession session) {
        System.out.println("북마크 상태 정보 업데이트!");
        Member loginUser = (Member) session.getAttribute("loginUser");
        Map<Integer, Boolean> response = new HashMap<>();
        if (loginUser != null) {
            int memberId = loginUser.getId();
            for (int postId : postIds) {
                boolean isBookmarked = postService.isBookmarked(postId, memberId);
                response.put(postId, isBookmarked);
            }
        }
        return response;
    }

}
