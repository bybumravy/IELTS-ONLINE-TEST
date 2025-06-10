
package web.ielts.Auth;

import java.security.Key;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.security.Keys;

public class JwtToken {
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // random key
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 2; // 2 giờ


    public static String generateToken(String email, String role) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + EXPIRATION_TIME);
    
        ZonedDateTime nowVn = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        ZonedDateTime expireVn = nowVn.plusSeconds(EXPIRATION_TIME / 1000);
    
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String issuedAtLocal = nowVn.format(formatter);
        String expirationLocal = expireVn.format(formatter);
    
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)                    // truyền role vào claim
                .claim("issuedAtLocal", issuedAtLocal)
                .claim("expiresAtLocal", expirationLocal)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }
    public static String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public static String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    private static Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            throw new RuntimeException("Invalid JWT signature");
        } catch (Exception e) {
            throw new RuntimeException("Invalid token");
        }
    }


//    public static String extractUsername(String token) {
//        try {
//            Claims claims = Jwts.parserBuilder() // ✅ đúng: tạo builder
//                .setSigningKey(key)              // ✅ thiết lập khóa ký
//                .build()                         // ✅ build ra JwtParser
//                .parseClaimsJws(token)           // ✅ parse token
//                .getBody();                      // ✅ lấy payload
//
//            return claims.getSubject(); // ✅ thường là email/username
//
//        } catch (SignatureException e) {
//            throw new RuntimeException("Invalid JWT signature");
//        } catch (Exception e) {
//            throw new RuntimeException("Invalid token");
//        }
//    }
}

