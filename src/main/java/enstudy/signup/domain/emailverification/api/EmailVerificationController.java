package enstudy.signup.domain.emailverification.api;

import enstudy.signup.domain.emailverification.dto.VerificationCodeRequest;
import enstudy.signup.domain.emailverification.dto.EmailVerificationRequest;
import enstudy.signup.domain.emailverification.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// RESTFUL 한 URI 를 만들려고 어쩔 수 없이 또는 동사를 쓰는 것이 최상의 선택일때는
// 도메인을 또는 자원을 구분하는 '/' 슬래쉬를 쓰는 것이 아니라 ':' 칼럼을 사용해서 동사를 쓰는 경우도 있다고 한다!

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/email-verification")
@Tag(name = "이메일 인증 API", description = "이메일 인증 관련")
@Slf4j
public class EmailVerificationController {
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/request")
    @Operation(summary = "이메일 인증 번호 전송")
    public ResponseEntity<String> sendVerificationCode(@Valid @RequestBody VerificationCodeRequest verificationCodeRequest) {

        log.info("POST /email-verification/request: {}", verificationCodeRequest.email());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(emailVerificationService.sendVerificationCode(verificationCodeRequest));
    }

    @PostMapping("/confirm")
    @Operation(summary = "이메일 인증")
    public ResponseEntity<String> verifyCode(@Valid @RequestBody EmailVerificationRequest emailVerificationRequest) {

        log.info("POST /email-verification/confirm: {} / {}", emailVerificationRequest.email(), emailVerificationRequest.verificationCode());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(emailVerificationService.verify(emailVerificationRequest));
    }
}
