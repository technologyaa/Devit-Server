package technologyaa.Devit.domain.auth.jwt.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import technologyaa.Devit.domain.auth.jwt.exception.AuthErrorCode;
import technologyaa.Devit.domain.auth.jwt.exception.AuthException;
import technologyaa.Devit.domain.common.APIResponse;
import technologyaa.Devit.global.config.RedisConfig;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final RedisConfig redisConfig;
    private int authNume;

    @Value("${spring.mail.username}")
    private String serviceName;

    public void makeRandomNumber(){
        Random random = new Random();
        authNume = 100000 + random.nextInt(900000);
    }

    public void sendEmail(String setFrom, String toMail, String title, String content) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true, "utf-8");
            helper.setFrom(setFrom);
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content,true);
            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        ValueOperations<String, String> valueOperations = redisConfig.redisTemplate().opsForValue();
        valueOperations.set(toMail,Integer.toString(authNume), 5, TimeUnit.MINUTES);
    }

    public String joinEmail(String email) {
        makeRandomNumber();
        String customerMail = email;
        String title = "회원가입을 위한 인증코드입니다.";
        String content =
                "<div style='max-width:480px; margin:auto; font-family:\"Inter\", \"Segoe UI\", Arial, sans-serif; border:1px solid #e5e7eb; border-radius:12px; padding:32px; background:#ffffff; color:#111827;'>" +
                        "<h2 style='text-align:center; color:#2563eb; margin-bottom:20px;'>devit 이메일 인증</h2>" +
                        "<p style='font-size:15px; text-align:center; line-height:1.6; margin-bottom:28px;'>안녕하세요,<br>아래 인증 번호를 입력하시면 이메일 인증이 완료됩니다.</p>" +

                        "<div style='text-align:center; background:#f9fafb; border:1px solid #e5e7eb; border-radius:8px; padding:20px; margin-bottom:28px;'>" +
                        "<span style='font-size:32px; font-weight:700; color:#2563eb; letter-spacing:4px;'>"+ authNume +"</span>" +
                        "</div>" +

                        "<p style='font-size:13px; text-align:center; color:#6b7280; line-height:1.5;'>본 메일은 Devit 서비스에서 자동 발송되었습니다.<br>답장하실 필요는 없습니다.</p>" +
                        "</div>";

        sendEmail(serviceName, customerMail, title, content);
        return Integer.toString(authNume);
    }

    public APIResponse<String> checkEmail(String email, String authNume) {
        ValueOperations<String, String> valueOperations = redisConfig.redisTemplate().opsForValue();
        String code = valueOperations.get(email);
        if(Objects.equals(code,authNume)){
            return APIResponse.ok("이메일 인증에 성공하셨습니다.");

        }
        throw new AuthException(AuthErrorCode.EMAIL_VERIFICATION_FAILED);
    }
}

