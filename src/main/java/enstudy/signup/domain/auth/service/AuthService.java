package enstudy.signup.domain.auth.service;

import enstudy.signup.domain.auth.dto.request.ChangePasswordRequest;
import enstudy.signup.domain.auth.dto.request.CheckEmailRequest;
import enstudy.signup.domain.auth.dto.request.LoginRequest;
import enstudy.signup.domain.auth.dto.request.SignUpRequest;
import enstudy.signup.domain.user.entity.User;
import enstudy.signup.domain.user.repository.UserRepository;
import enstudy.signup.global.exception.errorcode.UserErrorCode;
import enstudy.signup.global.exception.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public int signUp(SignUpRequest signUpRequest){

        log.info("[ SIGN UP - TRANSACTION START ]      email = {} / name = {}", signUpRequest.email(), signUpRequest.username());

        // 이미 해당 이메일 유저가 존재한다면 예외 던지기
        if(userRepository.existsByEmail(signUpRequest.email())){
            throw new UserException(UserErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .email(signUpRequest.email())
                .username(signUpRequest.username())
                // 비밀번호는 인코딩하여 저장
                .password(passwordEncoder.encode(signUpRequest.password()))
                .streetAddress(signUpRequest.streetAddress())
                .detailAddress(signUpRequest.detailAddress())
                .build();

        userRepository.save(user);
        log.info("[ SIGN UP - TRANSACTION COMMITED ]   email = {} / name = {}", signUpRequest.email(), signUpRequest.username());

        return user.getId();
    }

    @Transactional(readOnly = true)
    public String checkIfEmailAvailable(CheckEmailRequest checkEmailRequest){

        log.info("[ CHECK EMAIL - TRANSACTION START ]      email = {}", checkEmailRequest.email());

        // 만약 이메일이 중복되었으면 예외 던지기
        if(userRepository.existsByEmail(checkEmailRequest.email())) {
            throw new UserException(UserErrorCode.DUPLICATE_EMAIL);
        }

        log.info("[ CHECK EMAIL - TRANSACTION COMMITED ]   email = {}", checkEmailRequest.email());

        return "사용 가능한 이메일입니다";
    }

    @Transactional(readOnly = true)
    public User login(LoginRequest loginRequest) {

        log.info("[ LOGIN - TRANSACTION START ]      email = {} / password = {}", loginRequest.email(), loginRequest.password());

        User user = userRepository.findByEmail(loginRequest.email())
                // 해당 이메일 유저가 존재하지 않는다면
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 만약 비밀번호가 일치하지 않는다면
        // 같은 평문이어도 실행마다 다른 해시 값을 생성 -> equals로 비교하면 안됨
        if(!passwordEncoder.matches(loginRequest.password(), user.getPassword()))
            throw new UserException(UserErrorCode.INVALID_PASSWORD);

        log.info("[ LOGIN - TRANSACTION COMMITED ]   email = {} / password = {}", loginRequest.email(), loginRequest.password());

        // 로그인 성공하면 닉네임 반환
        return user;
    }

    @Transactional
    public void changePassword(ChangePasswordRequest changePasswordRequest) {

        log.info("[ CHANGE PASSWORD - TRANSACTION START ]      email = {}", changePasswordRequest.email());

        // 새로운 password 인코딩해주기
        String newPassword = passwordEncoder.encode(changePasswordRequest.password());

        if(!userRepository.existsByEmail(changePasswordRequest.email())){
            throw new UserException(UserErrorCode.USER_NOT_FOUND);
        }

        int rows = userRepository.updatePasswordByEmail(changePasswordRequest.email(), newPassword);

        // 업데이트 된 행이 없다면
        if(rows == 0)
            throw new UserException(UserErrorCode.PASSWORD_UPDATE_FAILURE);

        log.info("[ CHANGE PASSWORD - TRANSACTION COMMITED ]   email = {}", changePasswordRequest.email());
    }
}
