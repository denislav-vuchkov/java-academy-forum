package com.forum.javaforum.utilities;

import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.users.User;

public class PublicationValidators {

    public static void validatePublicationAccess(User user) {
        validateUserIsNotDeleted(user);
    }

    public static void validatePublicationCreation(User user) {
        validateUserIsNotDeleted(user);
        validUserIsNotBlocked(user);
    }

    public static void validatePublicationModification(User user, int authorID) {
        validateUserIsNotDeleted(user);
        validUserIsNotBlocked(user);
        validateUserIsAuthorOrAdmin(user, authorID);
    }

    public static void validateUserIsNotDeleted(User user) {
        if (user.isDeleted())
            throw new UnauthorizedOperationException("User deleted. Please contact a forum administrator.");
    }

    public static void validUserIsNotBlocked(User user) {
        if (user.isBlocked())
            throw new UnauthorizedOperationException("Blocked users have only reading rights.");
    }

    public static void validateUserIsAuthorOrAdmin(User user, int authorID) {
        if (!(user.getId() == authorID || user.isAdmin()))
            throw new UnauthorizedOperationException("Users can only modify publications they authored.");
    }

}
