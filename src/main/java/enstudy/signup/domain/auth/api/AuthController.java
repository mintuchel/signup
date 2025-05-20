package enstudy.signup.domain.auth.api;

import enstudy.signup.domain.auth.dto.request.*;
import enstudy.signup.domain.auth.service.AuthService;
import enstudy.signup.domain.auth.dto.response.UserInfoResponse;
import enstudy.signup.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// @RestController가 @ResponseBody를 포함하므로 리턴되는 객체가 자동으로 JSON으로 변환되며,
// 응답의 Content-Type이 기본적으로 application/json으로 설정됨

// Controller 단에서 ResponseEntity 생성하는게 좋음
// 1. SOLID 원칙 준수: 단일 책임 원칙(SRP)에 따라, 서비스는 비즈니스 로직만, 컨트롤러는 HTTP 요청/응답 처리를 담당하는 게 깔끔.
// 2. 테스트 용이성: 서비스 단이 HTTP와 독립적이어서 단위 테스트가 쉬움.

// DTO에 @NotBlank를 적용했다면, 컨트롤러에서 @Valid 또는 @Validated를 사용해 검증을 트리거해야함

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "회원가입/로그인 API", description = "박대원 김시원 신혜연 화이팅")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    // jwt 안쓰는 상황에서는 이 api 만드는게 제일 애매함
    @GetMapping("/me")
    @Operation(summary = "특정 유저 정보 조회")
    public ResponseEntity<UserInfoResponse> getUserInfo(@RequestParam String email) {

        log.info("GET /auth/me: {}", email);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getUserByEmail(email));
    }

    @PostMapping("/signup")
    @Operation(summary = "회원가입")
    public ResponseEntity<Integer> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {

        log.info("POST /auth/signup: {}", signUpRequest.email());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.signUp(signUpRequest));
    }

    @PostMapping("/email-check")
    @Operation(summary = "이메일 중복 확인")
    public ResponseEntity<String> checkIfEmailAvailable(@Valid @RequestBody CheckEmailRequest checkEmailRequest) {

        log.info("POST /email-check: {}", checkEmailRequest.email());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.checkIfEmailAvailable(checkEmailRequest));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest) {

        log.info("POST /auth/login: {}", loginRequest.email());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.login(loginRequest).getUsername());
    }

    // RESTful 방식에서는 왜 noContent로 보내는 것이 적합하다고 하는 것일까
    // 200 + body에 메시지 담아서 보내줘도 되는데
    // 그냥 HttpStatus로 명확하게 얘기하는게 목표라서 그런 것일까??
    @PatchMapping("/password")
    @Operation(summary = "비밀번호 변경")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {

        log.info("POST /auth/password: {}", changePasswordRequest.email());

        authService.changePassword(changePasswordRequest);

        // 204 반환
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}