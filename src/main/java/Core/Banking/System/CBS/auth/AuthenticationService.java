package Core.Banking.System.CBS.auth;


import Core.Banking.System.CBS.config.JwtService;
import Core.Banking.System.CBS.entity.Token;
import Core.Banking.System.CBS.entity.TokenType;
import Core.Banking.System.CBS.entity.User;
import Core.Banking.System.CBS.repository.TokenRepository;
import Core.Banking.System.CBS.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public AuthenticationResponse register(RegisterRequest request) {
        var user= User.builder()
                .firstName(request.getFirstName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        var savedUser= repository.save(user);
        var jwtToken=jwtService.generateToken(user);
        var refreshToken=jwtService.generateRefreshToken(user);
        saveUserToken(user, jwtToken);

        return AuthenticationResponse.builder()
                .token(jwtToken)
//                .refreshToken(refreshToken)
                .build();
    }


    public AuthenticationResponse authenticate(AuthenticateRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user=repository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken=jwtService.generateToken(user);
        var refreshToken=jwtService.generateRefreshToken(user);

        revokeAllUserToken(user);
        saveUserToken(user, jwtToken);


        return AuthenticationResponse.builder()
                .token(jwtToken)
//                .refreshToken(refreshToken)
                .build();

    }

    private void revokeAllUserToken(User  user){
        var validUserToken=tokenRepository.findAllValidTokensByUser(user.getId());

        if (validUserToken.isEmpty())
            return;
        validUserToken.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserToken);
    }
    private void saveUserToken(User user, String jwtToken) {
        var token= Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader==null ||!authHeader.startsWith("Bearer ")){
            return;
        }
        refreshToken=authHeader.substring(7);
        userEmail=jwtService.extractUsername(refreshToken);

        if(userEmail !=null){
            var user=this.repository.findByEmail(userEmail).orElseThrow();

            if (jwtService.isTokenValid(refreshToken,user)){
                var accessToken=jwtService.generateToken(user);
                revokeAllUserToken(user);
                saveUserToken(user, accessToken);


                var authResponse=AuthenticationResponse.builder()
                        .token(accessToken)
//                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(),authResponse);
            }

        }
    }
}
