package enstudy.signup.domain.admin.api;

import enstudy.signup.domain.auth.dto.response.UserInfoResponse;
import enstudy.signup.domain.admin.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@Tag(name = "관리자 API",
        description = "테스트 시 유용하게 사용하되, 다른 사람의 자원은 절대 삭제하지 마세요.\n\n" +
                "관리자 권한 남용 시 타 동기들에게 피해가 갈 수 있습니다.")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/users")
    @Operation(summary = "모든 유저 조회")
    public ResponseEntity<List<UserInfoResponse>> getAllUsers() {

        log.info("GET /admin/users");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.getAllUsers());
    }

    @DeleteMapping("/users/{email}")
    @Operation(summary = "유저 삭제")
    public ResponseEntity<Void> deleteUser(@PathVariable String email) {

        log.info("DELETE /admin/users: {}", email);

        adminService.deleteUserByEmail(email);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @DeleteMapping("/email-verification/{email}")
    @Operation(summary = "이메일 인증 기록 삭제")
    public ResponseEntity<Void> deleteEmail(@PathVariable String email) {

        log.info("DELETE /admin/email-verification: {}", email);

        adminService.deleteEmail(email);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
