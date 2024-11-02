package com.visionbagel.utils;

import com.visionbagel.entitys.User;
import com.visionbagel.entitys.UserToken;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.jwt.Claims;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;

@ApplicationScoped
public class TokenTools {
    public void generate(User user, List<String> roles) {
        String date = LocalDate.now()
            .plusYears(1)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Instant now = Instant.now();
        String tokenString = Jwt.issuer("https://visionbagel.com")
            .upn(user.username)
            .groups(new HashSet<>(roles))
            .expiresIn(Duration.ofDays(30))
            // .expiresAt(now.plus(30, ChronoUnit.DAYS))
            .claim(Claims.birthdate.name(), date)
            .sign();

        if(user.token == null) {
            UserToken userToken = new UserToken();
            userToken.token = tokenString;
            userToken.user = user;
            user.token = userToken;
            user.persistAndFlush();
        } else {
            user.token.token = tokenString;
            user.token.persistAndFlush();
        }
    }
}
