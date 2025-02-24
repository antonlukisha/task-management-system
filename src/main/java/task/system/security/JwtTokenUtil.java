package task.system.security;

import io.jsonwebtoken.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import task.system.dto.JWTRspDTO;
import task.system.exception.implementations.JwtTokenException;

import java.util.Date;

@Component
@Tag(name = "JwtTokenUtil", description = "Util for generation and validation check of access/refresh tokens")
public class JwtTokenUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Value("${jwt.secret.key}")
    private static String SECRET_KEY;

    @Value("${jwt.access.token.expiration}")
    private static Long ACCESS_TOKEN_EXPIRATION;

    @Value("${jwt.refresh.token.expiration}")
    private static Long REFRESH_TOKEN_EXPIRATION;

    public String generateAccessToken(UserDetails userDetail) {
        return Jwts.builder()
                .setSubject(userDetail.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetail) {
        return Jwts.builder()
                .setSubject(userDetail.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public String generateAccessTokenByKeyWord(String keyWord) {
        return Jwts.builder()
                .setSubject(keyWord)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public String generateRefreshTokenByKeyWord(String keyWord) {
        return Jwts.builder()
                .setSubject(keyWord)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public Claims extractClaims(String token) {
        try {
            logger.info("Extracting claims from token: {}...", token);
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception exception) {
            logger.error("Token is invalid or corrupted: {}", exception.getMessage());
            throw JwtTokenException.of(HttpStatus.FORBIDDEN, "Token is invalid or corrupted");
        }
    }

    public String extractKeyWord(String token) {
        return extractClaims(token).getSubject();
    }

    private Date getExpirationDateFromToken(String token) {
        logger.info("Fetching expiration date of token: {}...", token);
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    private boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    public boolean validateJwtToken(String token) {
        logger.info("Checking validation of token: {}...", token);
        try {
            Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException exception) {
            logger.error("JWT token is expired: {}", exception.getMessage());
        } catch (UnsupportedJwtException exception) {
            logger.error("JWT token is unsupported: {}", exception.getMessage());
        } catch (IllegalArgumentException exception) {
            logger.error("JWT claims string is empty: {}", exception.getMessage());
        } catch (Exception exception) {
            logger.error("Invalid JWT token: {}", exception.getMessage());
        }

        return false;
    }

    public JWTRspDTO refreshAccessToken(String refreshToken) {
        if (isTokenExpired(refreshToken)) {
            logger.error("Refresh token has expired");
            throw JwtTokenException.of(HttpStatus.FORBIDDEN, "Refresh token has expired");
        }
        String keyWord = extractKeyWord(refreshToken);
        logger.info("Updating tokens and creating response by key word: {}...", keyWord);
        return JWTRspDTO.builder()
                .accessToken(generateAccessTokenByKeyWord(keyWord))
                .refreshToken(generateRefreshTokenByKeyWord(keyWord))
                .email(keyWord)
                .build();
    }
}
