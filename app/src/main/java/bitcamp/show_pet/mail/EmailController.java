package bitcamp.show_pet.mail;

import bitcamp.show_pet.member.model.vo.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

@Controller
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/email-welcome")
    public String sendWelcomeEmail(@RequestBody Member member) throws MessagingException {
        emailService.sendWelcomeEmail(member);
        return "redirect:/";
    }
}
