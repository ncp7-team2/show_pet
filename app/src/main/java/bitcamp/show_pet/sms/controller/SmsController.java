package bitcamp.show_pet.sms.controller;

import bitcamp.show_pet.sms.Dto.MessageDto;
import bitcamp.show_pet.sms.Dto.SmsResponseDto;
import bitcamp.show_pet.sms.service.SmsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestClientException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Controller
@RequiredArgsConstructor
public class SmsController {
    private final SmsService smsService;

    @GetMapping("/send")
    public String getSmsPage() {
        return "/sms/sendSms";
    }


    @PostMapping("/sms/send")
    public String sendSms(MessageDto messageDto, Model model) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        SmsResponseDto response = smsService.sendSms(messageDto);
        model.addAttribute("response", response);
        return "/sms/result";
    }


}