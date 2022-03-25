package com.forum.javaforum.services;

import com.forum.javaforum.services.contracts.EmailService;
import com.forum.javaforum.exceptions.DuplicateEntityException;
import com.forum.javaforum.exceptions.InvalidParameter;
import com.forum.javaforum.exceptions.UnauthorizedOperationException;
import com.forum.javaforum.models.users.ConfirmationToken;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.repositories.contracts.UserRepository;
import com.forum.javaforum.services.contracts.PhoneService;
import com.forum.javaforum.services.contracts.TokenService;
import com.forum.javaforum.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.forum.javaforum.utilities.AuthenticationHelper.AUTHORIZATION_HEADER_NAME;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PhoneService phoneService;
    private final TokenService tokenService;
    private final EmailService emailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PhoneService phoneService,
                           TokenService tokenService,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.phoneService = phoneService;
        this.tokenService = tokenService;
        this.emailService = emailService;
    }

    @Override
    public List<User> getAll(Optional<String> username, Optional<String> firstName,
                             Optional<String> email, Optional<String> sortBy, Optional<String> sortOrder) {
        return userRepository.getAll(username, firstName, email, sortBy, sortOrder);
    }

    @Override
    public User getById(int id) {
        validateID(id);
        return userRepository.getByID(id);
    }

    public User getByUsername(String username) {
        return userRepository.getByUsername(username);
    }

    //Method used by REST controller
    @Override
    public User create(HttpHeaders headers, User user) {
        validateIfAuthenticated(headers);

        return create(user);
    }

    //Method used by User MVC controller
    @Override
    public User create(User user) {
        userRepository.validateUserParamIsUnique("username", user.getUsername());
        userRepository.validateUserParamIsUnique("email", user.getEmail());

        userRepository.save(user);

        generateConfirmationToken(user);

        return userRepository.getByUsername(user.getUsername());
    }

    @Override
    public String generateConfirmationToken(User user) {
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user);

        tokenService.saveConfirmationToken(confirmationToken);

        sendEmailWithToken(user, token);

        return token;
    }

    private void sendEmailWithToken(User user, String token) {
        String link = "http://localhost:8080/auth/confirm-token?token=" + token;
        emailService.send(user.getEmail(),
                buildEmail(user.getFirstName(), link));
    }

    @Override
    public User update(User requester, int id, User user) {
        validateID(id);
        validateIfOwner(requester, user);
        validateEmailIsUnique(userRepository.getByID(id), user);

        User originalUser = getById(id);

        if (user.isAdmin()) {
            phoneService.update(user, originalUser);
        } else if (user.getPhoneNumber().getNumber() != null) {
            throw new UnauthorizedOperationException("Only administrators are allowed to have a phone number.");
        }

        return userRepository.update(id, user);
    }

    @Override
    public User delete(User requester, int id) {
        validateID(id);
        validateIfOwnerOrAdmin(requester, id);
        validateIfAlreadyDeleted(id);
        User targetUser = userRepository.getByID(id);

        return userRepository.delete(targetUser);
    }

    @Override
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = tokenService
                .findByToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found."));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Email already confirmed.");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiredAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token has expired.");
        }

        confirmationToken.setConfirmedAt(LocalDateTime.now());

        tokenService.updateConfirmationToken(confirmationToken);
        enableUser(confirmationToken.getUser().getId());

        return "confirmed";
    }

    @Override
    public void enableUser(int id) {
        User user = getById(id);

        user.setEnabled(true);
        userRepository.update(id, user);
    }

    @Override
    public Long getNumberOfUsers() {
        return userRepository.getNumberOfUsers();
    }

    @Override
    public Long getNumberOfInactiveUsers() {
        return userRepository.getNumberOfInactiveUsers();
    }

    @Override
    public Long getNumberOfAdmins() {
        return userRepository.getNumberOfAdmins();
    }

    private void validateIfAlreadyDeleted(int id) {
        User targetUser = userRepository.getByID(id);

        if (targetUser.isDeleted()) {
            throw new DuplicateEntityException(String.format("User with id %d is already deleted. " +
                    "Cannot perform this operation again.", id));
        }
    }

    private void validateEmailIsUnique(User originalUser, User updatedUser) {
        if (!originalUser.getEmail().equals(updatedUser.getEmail())) {
            userRepository.validateUserParamIsUnique("email", updatedUser.getEmail());
        }
    }

    private void validateIfOwnerOrAdmin(User requester, int id) {
        if (!(requester.isAdmin() || requester.getId() == id)) {
            throw new UnauthorizedOperationException("You do not have sufficient rights to perform this operation. " +
                    "Only admins or the owner of the account can delete it.");
        }
    }


    private void validateIfOwner(User requester, User user) {
        if (!requester.getUsername().equals(user.getUsername())) {
            throw new UnauthorizedOperationException("You are not authorised to make changes to this user." +
                    "Only the owner of the account can make changes.");
        }
    }

    private void validateIfAuthenticated(HttpHeaders headers) {
        if (headers.containsKey(AUTHORIZATION_HEADER_NAME)) {
            throw new UnauthorizedOperationException("The requested resource is available only" +
                    " to users that are not logged in the system.");
        }
    }

    private void validateID(int id) {
        if (id <= 0) {
            throw new InvalidParameter("Please provide a positive integer as ID.");
        }
    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }

}
