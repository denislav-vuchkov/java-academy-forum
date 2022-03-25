package com.forum.javaforum.services.contracts;

import com.forum.javaforum.models.users.ConfirmationToken;

import java.util.Optional;

public interface TokenService {

    Optional<ConfirmationToken> findByToken(String token);

    void saveConfirmationToken(ConfirmationToken token);

    void updateConfirmationToken(ConfirmationToken token);
}
