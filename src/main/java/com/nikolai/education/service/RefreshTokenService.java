package com.nikolai.education.service;

import com.nikolai.education.exception.TokenRefreshException;
import com.nikolai.education.model.RefreshToken;
import com.nikolai.education.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${app.jwtRefreshExpirationMs}")
    private static Long jwtRefreshExpirationMs;

    private final UserRepo userRepository;
    private final CacheManager<RefreshToken> cacheManager;

    public Optional<RefreshToken> findByToken(String token) {
        //return refreshTokenRepository.findByToken(token);
        return cacheManager.getByKey(token);
    }

    public RefreshToken createRefreshToken(Authentication authentication) {

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findByEmail(authentication.getName()));
        refreshToken.setExpiryDate(Calendar.getInstance().getTimeInMillis() + 90000000);
        refreshToken.setToken(UUID.randomUUID().toString());
        //refreshToken = refreshTokenRepository.save(refreshToken);
        cacheManager.cachedObject(refreshToken.getToken(), refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Calendar.getInstance().getTimeInMillis()) < 0) {
            cacheManager.deleteFromCache(token.getToken());
            //refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }



}
