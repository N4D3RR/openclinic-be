package naderdeghaili.capstoneproject.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import naderdeghaili.capstoneproject.entities.User;
import naderdeghaili.capstoneproject.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JWTTools {
    @Value("${jwt.secret}")
    private String key;

    public String createToken(User user) {
        return Jwts.builder()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(14)))
                .subject(String.valueOf(user.getId()))
                .signWith(Keys.hmacShaKeyFor(key.getBytes()))
                .compact();
    }

    public void verifyToken(String token) {
        try {
            Jwts.parser().verifyWith(Keys.hmacShaKeyFor(key.getBytes())).build().parse(token);
        } catch (Exception ex) {
            throw new UnauthorizedException("Invalid or expired token");
        }
    }

    public UUID getIdFromToken(String token) {
        return UUID.fromString(Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(key.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject());
    }
}
