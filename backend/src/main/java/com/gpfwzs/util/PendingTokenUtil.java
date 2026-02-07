package com.gpfwzs.util;

import com.gpfwzs.dto.HuaweiUserInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class PendingTokenUtil {

    private final SecretKey secretKey;
    private static final long PENDING_TOKEN_EXPIRATION = 5 * 60 * 1000; // 5 minutes

    public PendingTokenUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generatePendingToken(HuaweiUserInfo userInfo) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + PENDING_TOKEN_EXPIRATION);

        return Jwts.builder()
                .subject(userInfo.getOpenId())
                .claim("unionId", userInfo.getUnionId())
                .claim("displayName", userInfo.getDisplayName())
                .claim("avatar", userInfo.getAvatar())
                .claim("type", "pending")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public HuaweiUserInfo parsePendingToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String type = claims.get("type", String.class);
            if (!"pending".equals(type)) {
                throw new JwtException("Invalid token type");
            }

            return HuaweiUserInfo.builder()
                    .openId(claims.getSubject())
                    .unionId(claims.get("unionId", String.class))
                    .displayName(claims.get("displayName", String.class))
                    .avatar(claims.get("avatar", String.class))
                    .build();
        } catch (JwtException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid or expired pending token", e);
        }
    }

    public boolean validatePendingToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return "pending".equals(claims.get("type", String.class));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
