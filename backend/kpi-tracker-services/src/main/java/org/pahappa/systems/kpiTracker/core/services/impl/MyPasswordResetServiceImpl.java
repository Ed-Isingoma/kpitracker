package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.apache.commons.lang.StringUtils;
import org.pahappa.systems.kpiTracker.core.dao.impl.MailSettingDaoImpl;
import org.pahappa.systems.kpiTracker.core.services.MyPasswordResetService;
import org.pahappa.systems.kpiTracker.models.MailSetting;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.PasswordToken;
import org.sers.webutils.model.security.TokenStatus;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.dao.TokenDao;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.service.impl.PasswordResetServiceImpl;
import org.sers.webutils.server.core.utils.DateUtils;
import org.sers.webutils.server.core.utils.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service("myPasswordResetService")
@Transactional
public class MyPasswordResetServiceImpl extends PasswordResetServiceImpl implements MyPasswordResetService {

    @Autowired
    MailSettingDaoImpl mailSettingDao;
    @Autowired
    TokenDao tokenDao;
    @Autowired
    UserService userService;

    @Override
    public void requestPasswordChange(String email, String username, String contextPath) throws ValidationFailedException {
        if (StringUtils.isBlank(email)) {
            throw new ValidationFailedException("Specify an Email address");
        } else if (StringUtils.isBlank(username)) {
            throw new ValidationFailedException("Specify a username");
        } else {
            User userWithUsername = this.userService.getUserByUsername(username);
            if (userWithUsername != null && userWithUsername.getEmailAddress() != null && userWithUsername.getEmailAddress().equals(email)) {
                Search search = new Search();
                search.addFilterEqual("tokenStatus", TokenStatus.Active);
                search.addFilterEqual("user", userWithUsername);

                for(Object token : this.tokenDao.search(search)) {
                    PasswordToken tokennn = (PasswordToken)token;
                    if (tokennn.getExpiryDate().after(new Date())) {
                        throw new ValidationFailedException("You already requested for a password change and a reset link was sent to your email address.");
                    }

                    this.tokenDao.delete(tokennn);
                }

                PasswordToken passwordToken = new PasswordToken();
                passwordToken.setTokenStatus(TokenStatus.Active);
                passwordToken.setUser(userWithUsername);
                passwordToken.setExpiryDate(DateUtils.getDateAfterHours(1));
                String tokenID = ((PasswordToken)this.tokenDao.save(passwordToken)).getId();
                boolean sent = sendPasswordChangeVerificationMail(userWithUsername, tokenID, contextPath, 1);
            } else {
                throw new ValidationFailedException("Invalid Username or Email");
            }
        }
    }

    public boolean sendPasswordChangeVerificationMail(User user, String tokenId, String contextPath, int hoursAlive) {
        if (user == null || StringUtils.isBlank(user.getEmailAddress())) {
            return false;
        }

        try {
            String resetLink = contextPath + "/pages/settings/resetPassword.xhtml?token=" + tokenId;
            String subject = "Password Reset Request";
            String message = "Hello " + user.getFullName() + ",\n\n"
                    + "We received a request to reset your password for your account ("
                    + user.getUsername() + ").\n\n"
                    + "To reset your password, click the link below or paste it into your browser:\n"
                    + resetLink + "\n\n"
                    + "This link will remain active for " + hoursAlive + " hour(s).\n\n"
                    + "If you did not request a password reset, please ignore this email.\n\n"
                    + "Regards,\n"
                    + "Support Team";

            MailSetting settings = getActiveSettings();

            return MailService.sendMail(
                    subject,
                    message,
                    user.getEmailAddress(),
                    settings.getSenderAddress(),
                    settings.getSenderPassword(),
                    settings.getSenderSmtpHost(),
                    String.valueOf(settings.getSenderSmtpPort())
            );
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public MailSetting getActiveSettings() {
        Search search = new Search(MailSetting.class);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        return (MailSetting) mailSettingDao.searchUnique(search);
    }
}
