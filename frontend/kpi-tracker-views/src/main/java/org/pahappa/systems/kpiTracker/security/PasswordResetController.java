package org.pahappa.systems.kpiTracker.security;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.servlet.ServletContext;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.Getter;
import lombok.Setter;
import org.sers.webutils.model.security.PasswordToken;
import org.pahappa.systems.kpiTracker.core.services.MyPasswordResetService;
import org.springframework.web.context.support.WebApplicationContextUtils;

//@ManagedBean
//@ViewScoped
@Getter
@Setter
public class PasswordResetController implements Serializable {

//    @ManagedProperty("#{myPasswordResetService}")
    private MyPasswordResetService myPasswordResetService;

    private String email;
    private String username;

    private String newPassword;
    private String confirmPassword;

    private PasswordToken validatedToken;

    private static final ExecutorService emailExecutor = Executors.newSingleThreadExecutor();

    public void requestReset() {
        final String userEmail = this.email;
        final String userName = this.username;
        final String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();

        System.out.println("ACTION: Submitting email task to background thread for user: " + userName);

        Runnable emailTask = () -> {
            try {
                System.out.println("BACKGROUND: Task started for: " + userName);
                myPasswordResetService.requestPasswordChange(userEmail, userName, contextPath);
                System.out.println("BACKGROUND: Task finished successfully for: " + userName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        emailExecutor.submit(emailTask);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Request Submitted.", "If the username and email match an account, a reset link has been sent."));
    }

    public void validateTokenOnPageLoad() {

        if (!FacesContext.getCurrentInstance().isPostback()) {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String tokenId = params.get("token");

            if (tokenId != null && !tokenId.isEmpty()) {
                try {
                    this.validatedToken = myPasswordResetService.getTokenById(tokenId);
                } catch (Exception e) {
                    this.validatedToken = null;
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Invalid Link", e.getMessage()));
                }
            }
        }
    }

    public String performReset() {
        if (!newPassword.equals(confirmPassword)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error", "Passwords do not match."));
            return null;
        }

        try {
            myPasswordResetService.changePassword(this.validatedToken, this.newPassword);
            return "/ServiceLogin?faces-redirect=true&reset=success";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error", e.getMessage()));
            return null;
        }
    }

    public boolean isTokenValid() {
        return this.validatedToken != null;
    }
}
