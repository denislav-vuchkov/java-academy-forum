package com.forum.javaforum.repositories.contracts;

import com.forum.javaforum.models.users.ConfirmationToken;

import java.util.Optional;

public interface TokenRepository {

    Optional<ConfirmationToken> findByToken(String token);

    void save(ConfirmationToken token);

    void update(ConfirmationToken token);
}
