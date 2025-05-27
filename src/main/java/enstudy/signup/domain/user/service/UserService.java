package enstudy.signup.domain.user.service;

import enstudy.signup.domain.auth.dto.response.UserInfoResponse;
import enstudy.signup.domain.user.entity.User;
import enstudy.signup.domain.user.repository.UserRepository;
import enstudy.signup.global.exception.errorcode.UserErrorCode;
import enstudy.signup.global.exception.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserInfoResponse getUserByEmail(String email) {

        log.info("[ GET USER BY EMAIL - TRANSACTION START ]      email = {}", email);

        User user = userRepository.findByEmail(email)
                // 해당 이메일(유저)이 존재하지 않는다면
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        log.info("[ GET USER BY EMAIL - TRANSACTION COMMITED ]   email = {}", email);
        return UserInfoResponse.from(user);
    }
}
