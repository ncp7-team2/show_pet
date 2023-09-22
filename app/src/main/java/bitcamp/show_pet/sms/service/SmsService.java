package bitcamp.show_pet.sms.service;

import bitcamp.show_pet.config.NcpSmsConfig;
import bitcamp.show_pet.sms.Dto.MessageDto;
import bitcamp.show_pet.sms.Dto.SmsRequestDto;
import bitcamp.show_pet.sms.Dto.SmsResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SmsService {
    private final NcpSmsConfig ncpSmsConfig;

    public SmsService(NcpSmsConfig ncpSmsConfig) {
        this.ncpSmsConfig = ncpSmsConfig;
    }

    public SmsResponseDto sendSms(MessageDto messageDto) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        Long time = System.currentTimeMillis();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time.toString());
        headers.set("x-ncp-iam-access-key", ncpSmsConfig.getAccessKey());
        headers.set("x-ncp-apigw-signature-v2", ncpSmsConfig.makeSignature(time));

        List<MessageDto> messages = new ArrayList<>();
        messages.add(messageDto);

        SmsRequestDto request = SmsRequestDto.builder()
                .type("SMS")
                .contentType("COMM")
                .countryCode("82")
                .from(ncpSmsConfig.getSenderPhone())
                .content(messageDto.getContent())
                .messages(messages)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(request);
        HttpEntity<String> httpBody = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        SmsResponseDto response = restTemplate.postForObject(new URI("https://sens.apigw.ntruss.com/sms/v2/services/"+ ncpSmsConfig.getServiceId() +"/messages"), httpBody, SmsResponseDto.class);

        return response;
    }
}
