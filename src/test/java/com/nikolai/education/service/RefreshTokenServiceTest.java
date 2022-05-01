package com.nikolai.education.service;

import com.nikolai.education.exception.TokenRefreshException;
import com.nikolai.education.model.RefreshToken;
import com.nikolai.education.model.User;
import com.nikolai.education.repository.UserRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Calendar;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class RefreshTokenServiceTest {

    @InjectMocks
    private RefreshTokenService refreshTokenService;
    @Mock
    private UserRepo userRepo;
    @Mock
    private CacheManagerService<RefreshToken> cacheManagerService;

    private RefreshToken testRefreshToken;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("Nikolai", "Nagornykh", "stormytt@mail.ru",
                "112233", "+375444321965");
        testRefreshToken = new RefreshToken(1L, testUser, "asdas-dsf-cxz-nb", 10L);
    }

    @Test
    void findByToken() {
        when(cacheManagerService.getByKey(anyString())).thenReturn(java.util.Optional.ofNullable(testRefreshToken));
        Optional<RefreshToken> result = refreshTokenService.findByToken(anyString());
        Assertions.assertEquals(testRefreshToken.getId(), result.get().getId());
        Assertions.assertEquals(testRefreshToken.getToken(), result.get().getToken());
    }

    @Test
    void createRefreshToken() {
        when(userRepo.findByEmail(anyString())).thenReturn(testUser);
        when(cacheManagerService.cachedObject(anyString(), any(RefreshToken.class))).thenReturn(testRefreshToken);

        RefreshToken result = refreshTokenService.createRefreshToken(testUser.getEmail());

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getExpiryDate());
        Assertions.assertNotNull(result.getToken());
        Assertions.assertEquals(testUser, result.getUser());
    }

    @Test
    void verifyExpiration() {
        testRefreshToken.setExpiryDate(Calendar.getInstance().getTimeInMillis() + 900000);
        refreshTokenService.verifyExpiration(testRefreshToken);
    }

    @Test
    void verifyExpirationException() {
        testRefreshToken.setExpiryDate(Calendar.getInstance().getTimeInMillis() - 900000);
        assertThatThrownBy(() -> refreshTokenService.verifyExpiration(testRefreshToken)).isInstanceOf(TokenRefreshException.class);
    }
}