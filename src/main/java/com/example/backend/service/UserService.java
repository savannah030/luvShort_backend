package com.example.backend.service;

import com.example.backend.config.dto.OAuthAttributesDto;
import com.example.backend.domain.user.User;
import com.example.backend.exception.BackendException;
import com.example.backend.exception.ReturnCode;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    // 인증 서버(카카오, 네이버...)에서 받은 정보로 User 만들기
    public void createUser(OAuthAttributesDto oAuthAttributesDto){
        final String email = oAuthAttributesDto.getEmail();
        log.info("email from oAuthAttributesDto : {}",email);

        // 해당 이메일이 이미 User 테이블에 있으면 Exception 처리
        if(userRepository.existsByEmail(email)){
            log.warn("email already exists");
            throw new BackendException(ReturnCode.USER_EXIST_USING_THIS_EMAIL);
        }
        // 해당 이메일이 User 테이블에 없을 때만 User 엔티티 만들기
        userRepository.save(oAuthAttributesDto.toEntity());
        log.info("user saved");
    }
}
