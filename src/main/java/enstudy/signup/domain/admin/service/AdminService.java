package enstudy.signup.domain.admin.service;

import enstudy.signup.domain.auth.dto.response.UserInfoResponse;
import enstudy.signup.domain.emailverification.entity.EmailVerification;
import enstudy.signup.domain.emailverification.repository.EmailVerificationRepository;
import enstudy.signup.domain.user.entity.User;
import enstudy.signup.domain.user.repository.UserRepository;
import enstudy.signup.global.exception.errorcode.EmailErrorCode;
import enstudy.signup.global.exception.errorcode.UserErrorCode;
import enstudy.signup.global.exception.exception.EmailException;
import enstudy.signup.global.exception.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;

    @Transactional(readOnly = true)
    public List<UserInfoResponse> getAllUsers() {

        log.info("[관리자 모든 유저 요청]");

        List<User> users = userRepository.findAll();

        // stream 이랑 map 도 공부해야해
        // 언제까지 미룰꺼야 이거
        // 공부할거 개많다 진짜,,
        // :: 이거도 공부해야함
        return users.stream()
                .map(UserInfoResponse::from) // User -> UserInfoResponse 변환
                .toList();
    }

    @Transactional
    public void deleteUserByEmail(String email) {

        log.info("[관리자 유저 삭제 요청] email={}", email);

        // 여기서는 existsByEmail 보다 findByEmail 을 사용하는 것이 좋음
        // 만약 존재한다면 바로 user 객체를 userRepository.delete(user); 이렇게 사용하면 되기 때문

        User user = userRepository.findByEmail(email)
                // 해당 이메일 유저가 존재하지 않는다면
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        userRepository.delete(user);

        log.info("[관리자 유저 삭제 성공] email={}", email);
    }

    @Transactional
    public void deleteEmail(String email) {
        log.info("[관리자 이메일 인증 내역 삭제 요청] email={}", email);

        // 이메일 인증 테이블에 이메일이 존재하지 않는다면
        // 애초에 인증코드가 전송된 적이 없다는 뜻
        EmailVerification targetEmailVerification = emailVerificationRepository.findById(email)
                .orElseThrow(() -> new EmailException(EmailErrorCode.CODE_NOT_SENT));

        emailVerificationRepository.delete(targetEmailVerification);

        log.info("[관리자 이메일 인증 내역 삭제 성공] email={}", email);
    }
}
