package com.gpfwzs.service;

import com.gpfwzs.dto.HuaweiUserInfo;
import com.gpfwzs.entity.User;
import com.gpfwzs.exception.InviteCodeException;
import com.gpfwzs.exception.OAuthException;
import com.gpfwzs.exception.WhitelistException;
import com.gpfwzs.security.JwtTokenProvider;
import com.gpfwzs.util.PendingTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String OAUTH_AUTHORIZE_URL = "https://oauth-login.cloud.huawei.com/oauth2/v3/authorize";

    private final WebClient webClient;
    private final WhitelistService whitelistService;
    private final InviteCodeService inviteCodeService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PendingTokenUtil pendingTokenUtil;

    @Value("${oauth.huawei.client-id}")
    private String clientId;

    @Value("${oauth.huawei.client-secret}")
    private String clientSecret;

    @Value("${oauth.huawei.redirect-uri}")
    private String redirectUri;

    @Value("${oauth.huawei.token-url}")
    private String tokenUrl;

    @Value("${oauth.huawei.user-info-url}")
    private String userInfoUrl;

    /**
     * OAuth 回调处理结果
     */
    public static class OAuthResult {
        private final boolean success;
        private final String token;
        private final String pendingToken;
        private final HuaweiUserInfo userInfo;

        private OAuthResult(boolean success, String token, String pendingToken, HuaweiUserInfo userInfo) {
            this.success = success;
            this.token = token;
            this.pendingToken = pendingToken;
            this.userInfo = userInfo;
        }

        public static OAuthResult success(String token) {
            return new OAuthResult(true, token, null, null);
        }

        public static OAuthResult needInviteCode(String pendingToken, HuaweiUserInfo userInfo) {
            return new OAuthResult(false, null, pendingToken, userInfo);
        }

        public boolean isSuccess() { return success; }
        public String getToken() { return token; }
        public String getPendingToken() { return pendingToken; }
        public HuaweiUserInfo getUserInfo() { return userInfo; }
    }

    /**
     * 构建华为OAuth授权URL
     */
    public String buildOAuthAuthorizeUrl() {
        String state = UUID.randomUUID().toString().replace("-", "");
        
        return UriComponentsBuilder.fromHttpUrl(OAUTH_AUTHORIZE_URL)
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", "openid profile")
                .queryParam("state", state)
                .build()
                .toUriString();
    }

    /**
     * 处理 OAuth 回调
     * @return OAuthResult 包含成功或需要邀请码的信息
     */
    public OAuthResult processOAuthCallback(String code) {
        // 1. Exchange code for access token
        String accessToken = getAccessToken(code);
        
        // 2. Get user info from Huawei
        HuaweiUserInfo userInfo = getHuaweiUserInfo(accessToken);
        
        // 3. Check whitelist
        if (whitelistService.isInWhitelist(userInfo.getOpenId())) {
            // User in whitelist, create/update user and return JWT
            User user = userService.createOrUpdateUser(userInfo);
            String token = jwtTokenProvider.generateToken(user);
            return OAuthResult.success(token);
        } else {
            // Not in whitelist, return pending token for invite code flow
            String pendingToken = pendingTokenUtil.generatePendingToken(userInfo);
            log.info("User not in whitelist, need invite code. OpenId: {}****", 
                    userInfo.getOpenId().substring(0, Math.min(4, userInfo.getOpenId().length())));
            return OAuthResult.needInviteCode(pendingToken, userInfo);
        }
    }

    /**
     * 处理邀请码验证后的登录
     */
    public String processInviteCodeLogin(String inviteCode, String pendingToken) {
        // 1. Parse pending token to get user info
        HuaweiUserInfo userInfo;
        try {
            userInfo = pendingTokenUtil.parsePendingToken(pendingToken);
        } catch (IllegalArgumentException e) {
            throw new OAuthException("无效或过期的认证信息，请重新登录");
        }

        // 2. Validate and use invite code
        inviteCodeService.validateAndUseInviteCode(inviteCode, userInfo.getOpenId());

        // 3. Add user to whitelist
        whitelistService.addToWhitelist(userInfo.getOpenId(), "通过邀请码 [" + inviteCode + "] 加入");

        // 4. Create or update user
        User user = userService.createOrUpdateUser(userInfo);

        // 5. Generate and return JWT
        return jwtTokenProvider.generateToken(user);
    }

    private String getAccessToken(String code) {
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "authorization_code");
            formData.add("code", code);
            formData.add("client_id", clientId);
            formData.add("client_secret", clientSecret);
            formData.add("redirect_uri", redirectUri);

            Map<String, Object> response = webClient.post()
                    .uri(tokenUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || !response.containsKey("access_token")) {
                throw new OAuthException("Failed to get access token from Huawei");
            }

            return (String) response.get("access_token");
        } catch (Exception e) {
            log.error("Failed to get access token", e);
            throw new OAuthException("Failed to get access token: " + e.getMessage(), e);
        }
    }

    private HuaweiUserInfo getHuaweiUserInfo(String accessToken) {
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("nsp_svc", "GOpen.User.getInfo");
            formData.add("access_token", accessToken);
            formData.add("nsp_ts", String.valueOf(System.currentTimeMillis() / 1000));
            formData.add("nsp_fmt", "JSON");

            Map<String, Object> response = webClient.post()
                    .uri(userInfoUrl + "?nsp_svc=GOpen.User.getInfo")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            log.debug("Huawei userinfo response: {}", response);

            if (response == null) {
                throw new OAuthException("Failed to get user info from Huawei");
            }

            if (response.containsKey("error")) {
                String error = String.valueOf(response.get("error"));
                String errorDesc = String.valueOf(response.get("error_description"));
                throw new OAuthException("Huawei API error: " + error + " - " + errorDesc);
            }

            String openId = (String) response.get("openID");
            if (openId == null) {
                log.warn("Huawei response fields: {}", response.keySet());
                throw new OAuthException("OpenID not found in user info");
            }

            HuaweiUserInfo userInfo = HuaweiUserInfo.builder()
                    .openId(openId)
                    .unionId((String) response.get("unionID"))
                    .displayName((String) response.get("displayName"))
                    .avatar((String) response.get("headPictureURL"))
                    .build();

            log.info("Got user info from Huawei. OpenId: {}****", 
                    openId.substring(0, Math.min(4, openId.length())));
            return userInfo;
        } catch (OAuthException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to get user info", e);
            throw new OAuthException("Failed to get user info: " + e.getMessage(), e);
        }
    }
}
