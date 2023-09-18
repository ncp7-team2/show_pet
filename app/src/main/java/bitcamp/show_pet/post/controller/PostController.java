package bitcamp.show_pet.post.controller;

import bitcamp.show_pet.NcpObjectStorageService;
import bitcamp.show_pet.post.model.vo.AttachedFile;
import bitcamp.show_pet.post.model.vo.Post;
import bitcamp.show_pet.post.service.PostService;
import bitcamp.show_pet.user.model.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public void form() { }

    @PostMapping("add")
    public String add(Post post, MultipartFile[] files, HttpSession session) throws Exception {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "/user/form";
        }
        post.setUser(loginUser);

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
        return "redirect:/post/list";
    }

    @GetMapping("list")
    public void list() throws Exception {

    }
}
