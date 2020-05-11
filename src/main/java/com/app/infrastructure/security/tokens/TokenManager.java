package com.app.infrastructure.security.tokens;

import com.app.domain.repository.UserRepository;
import com.app.infrastructure.dto.RefreshTokenDto;
import com.app.infrastructure.security.dto.TokensDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TokenManager {

    @Value("${jwt.access-token.expiration-time-ms}")
    private Long accessTokenExpirationTimeInMs;

    @Value("${jwt.refresh-token.expiration-time-ms}")
    private Long refreshTokenExpirationTimeInMs;

    @Value("${jwt.token.prefix}")
    private String jwtTokenPrefix;

    @Value("${jwt.token.header}")
    private String jwtTokenHeader;

    @Value("${jwt.refresh-token.access-token-key}")
    private String refreshTokenAccessTokenKey;

    private final SecretKey secretKey;
    private final UserRepository userRepository;

    // ------------------------------------------------------------------------------
    // GENEROWANIE TOKENOW
    // ------------------------------------------------------------------------------
    public TokensDto generateTokens(Authentication authentication) {

        if (authentication == null) {
            throw new SecurityException("generate tokens - authentication object  is null");
        }

        var id = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() -> new SecurityException("generate tokens - cannot find user with username " + authentication.getName()))
                .getId();

        var createdDate = new Date();
        var accessTokenExpirationTimeMillis = System.currentTimeMillis() + accessTokenExpirationTimeInMs;
        var accessTokenExpirationTime = new Date(accessTokenExpirationTimeMillis);
        var refreshTokenExpirationTime = new Date(System.currentTimeMillis() + refreshTokenExpirationTimeInMs);

        var accessToken = Jwts
                .builder()
                .setSubject(String.valueOf(id))
                .setExpiration(accessTokenExpirationTime)
                .setIssuedAt(createdDate)
                .signWith(secretKey)
                .compact();


        var refreshToken = Jwts
                .builder()
                .setSubject(String.valueOf(id))
                .setExpiration(refreshTokenExpirationTime)
                .setIssuedAt(createdDate)
                .signWith(secretKey)
                .claim(refreshTokenAccessTokenKey, accessTokenExpirationTimeMillis)
                .compact();

        return TokensDto
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    public TokensDto generateTokens(RefreshTokenDto refreshTokenDto) {

        if (Objects.isNull(refreshTokenDto)) {
            throw new SecurityException("generate tokens - refresh tokens is null");
        }

        var accessTokenTimeMsFromRefreshToken = Long.parseLong(getClaims(refreshTokenDto.getToken()).get(refreshTokenAccessTokenKey).toString());
        if (accessTokenTimeMsFromRefreshToken < System.currentTimeMillis()) {
            throw new SecurityException("generate tokens - cannot generate refresh token");
        }

        var id = getId(refreshTokenDto.getToken());

        var createdDate = new Date();
        var accessTokenExpirationTimeMillis = System.currentTimeMillis() + accessTokenExpirationTimeInMs;
        var accessTokenExpirationTime = new Date(accessTokenExpirationTimeMillis);
        var refreshTokenExpirationTime = getExpiration(refreshTokenDto.getToken());


        var accessToken = Jwts
                .builder()
                .setSubject(String.valueOf(id))
                .setExpiration(accessTokenExpirationTime)
                .setIssuedAt(createdDate)
                .signWith(secretKey)
                .compact();


        var refreshToken = Jwts
                .builder()
                .setSubject(String.valueOf(id))
                .setExpiration(refreshTokenExpirationTime)
                .setIssuedAt(createdDate)
                .signWith(secretKey)
                .claim(refreshTokenAccessTokenKey, accessTokenExpirationTimeMillis)
                .compact();

        return TokensDto
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    // ------------------------------------------------------------------------------
    // PARSOWANIE TOKENOW
    // ------------------------------------------------------------------------------
    public UsernamePasswordAuthenticationToken parseToken(String token) {

        if (Objects.isNull(token)) {
            throw new SecurityException("token is null");
        }

        if (!token.startsWith(jwtTokenPrefix)) {
            throw new SecurityException("token is not valid");
        }

        String accessToken = token.replace(jwtTokenPrefix, "");

        if (!isTokenValid(accessToken)) {
            throw new SecurityException("token is not valid");
        }

        Long userId = getId(accessToken);
//        return userRepository
//                .findOne(userId)
//                .map(userFromDb -> new UsernamePasswordAuthenticationToken(
//                        userFromDb.getUsername(),
//                        null,
//                        userFromDb
//                                .getRoles()
//                                .stream()
//                                .map(role -> new SimpleGrantedAuthority(role.getName()))
//                                .collect(Collectors.toList())))
//                .orElseThrow(() -> new SecurityException("no user with id " + userId));
        return userRepository
                .findOne(userId)
                .map(userFromDb -> new UsernamePasswordAuthenticationToken(
                        userFromDb.getUsername(),
                        null,
                        List.of(new SimpleGrantedAuthority(userFromDb.getRole().getName()))
                )).orElseThrow(() -> new SecurityException("no user with id: " + userId));
    }

    private Claims getClaims(String token) {
        if (Objects.isNull(token)) {
            throw new SecurityException("token is null");
        }

        System.out.println("-------------------------------------------------------");
        System.out.println(token);
        System.out.println("-------------------------------------------------------");

        return Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Long getId(String token) {
        return Long.parseLong(getClaims(token).getSubject());
    }

    private Date getExpiration(String token) {
        return getClaims(token).getExpiration();
    }

    private boolean isTokenValid(String token) {
        Date expirationDate = getExpiration(token);
        return expirationDate.after(new Date());
    }

}
