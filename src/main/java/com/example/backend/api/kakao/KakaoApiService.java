package com.example.backend.api.kakao;

import com.example.backend.config.auth.dto.KakaoAccessTokenInfo;
import com.example.backend.config.auth.dto.KakaoUserInfoResponseDto;
import com.example.backend.exception.BackendException;
import com.example.backend.exception.ReturnCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoApiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;


    // 카카오 '사용자 정보 가져오기' API에서 accessToken으로 정보 받아오기
    // cf) 카카오 '사용자 정보 가져오기' API https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info
    /**
     * // 요청
     * GET/POST /v2/user/me HTTP/1.1
     * Host: kapi.kakao.com
     * Authorization: Bearer ${ACCESS_TOKEN}
     * Content-type: application/x-www-form-urlencoded;charset=utf-8
     *
     * // 응답
     * Response: 성공, 사용자가 닉네임만 동의한 경우
     * HTTP/1.1 200 OK
     * {
     *     "id":123456789,
     *     "kakao_account": {
     *         "profile_nickname_needs_agreement": false,
     *         "profile": {
     *             "nickname": "홍길동"
     *         }
     *     },
     *     "properties":{
     *         "nickname":"홍길동카톡",
     *         "custom_field1":"23",
     *         "custom_field2":"여",
     *         ...
     *     }
     * }
     */
    public ResponseEntity<?> getUserByAccessToken(String accessToken){

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 만들기
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        String url = "https://kapi.kakao.com/v2/user/me";
        try {
            // 응답 받기
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            log.info("response: {}",response.getBody());
            return response;
        } catch (RestClientException ex) {
            ex.printStackTrace();
            throw new BackendException(ReturnCode.FAIL_TO_GET_KAKAO_ACCOUNT);
        }
    }

    // 카카오 '토큰 정보 보기' API에서 accessToken으로 '회원번호' 받아오기
    // cf) 카카오 '토큰 정보 보기' API https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#get-token-info
    /**
     * // 요청
     * GET /v1/user/access_token_info HTTP/1.1
     * Host: kapi.kakao.com
     * Authorization: Bearer ${ACCESS_TOKEN} // 헤더에 추가
     *
     * // 응답
     * HTTP/1.1 200 OK
     * {
     *     "id":123456789,
     *     "expires_in": 7199,
     *     "app_id":1234
     * }
     */
    public Long getTokenInfoByAccessToken(String accessToken){

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // 이건 안됨
        //headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //headers.add("Authorization", "Bearer "+accessToken);

        // 요청 만들기
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        String url = "https://kapi.kakao.com/v1/user/access_token_info"; // kapi.kakao.com는 카카오서버의 CORS가 닫혀있어서 우리 서버에서 응답받지 못함 https://devtalk.kakao.com/t/topic/116261
        try {
            // 응답 받기
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, request);
            log.info("response: {}",response);
            log.info("response.getBody(): {}",response.getBody());
            // DTO에 저장
            //KakaoAccessTokenInfo accessTokenInfo = objectMapper.readValue(response.getBody(), KakaoAccessTokenInfo.class);
            //log.info(" KakaoAccessTokenInfo.id: {}", accessTokenInfo.getId());
            //log.info(" KakaoAccessTokenInfo.expiresIn: {}", accessTokenInfo.getExpiresIn());
            //log.info(" KakaoAccessTokenInfo.appId: {}", accessTokenInfo.getAppId());
            // 그 중 회원번호만 리턴
            //return accessTokenInfo.getId();
            return 1L;
        } catch (RestClientException ex) {
            ex.printStackTrace();
            throw new BackendException(ReturnCode.FAIL_TO_GET_KAKAO_ACCESS_TOKEN_INFO);
        }
    }

}
