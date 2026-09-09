package com.bank.config;


import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;



@Service
public class JwtService {


    private static final String SECRET_KEY =
            "BankApplicationSecretKeyForJwtTokenGeneration123456789";



    private SecretKey getKey(){

        return Keys.hmacShaKeyFor(
            SECRET_KEY.getBytes()
        );

    }





    public String generateToken(String username){


        return Jwts.builder()

                .subject(username)

                .issuedAt(
                    new Date()
                )

                .expiration(
                    new Date(
                        System.currentTimeMillis()
                        +
                        1000 * 60 * 60 * 24
                    )
                )

                .signWith(
                    getKey()
                )

                .compact();


    }







    public String extractUsername(String token){


        Claims claims =
            Jwts.parser()

            .verifyWith(getKey())

            .build()

            .parseSignedClaims(token)

            .getPayload();



        return claims.getSubject();

    }






    public boolean isValid(String token){


        try {


            extractUsername(token);

            return true;


        }
        catch(Exception e){

            return false;

        }


    }


}