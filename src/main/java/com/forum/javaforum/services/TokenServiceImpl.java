package com.forum.javaforum.services;

import com.forum.javaforum.models.users.ConfirmationToken;
import com.forum.javaforum.repositories.contracts.TokenRepository;
import com.forum.javaforum.services.contracts.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;

    @Autowired
    public TokenServiceImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public Optional<ConfirmationToken> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    public void saveConfirmationToken(ConfirmationToken token) {
        tokenRepository.save(token);
    }

    @Override
    public void updateConfirmationToken(ConfirmationToken token) {
        tokenRepository.update(token);
    }
}
