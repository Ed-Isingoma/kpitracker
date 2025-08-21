package org.pahappa.systems.kpiTracker.security;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import java.io.Serializable;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import org.sers.webutils.model.security.PasswordToken;
import org.sers.webutils.server.core.service.PasswordResetService;
import org.springframework.stereotype.Controller;

@ManagedBean
@ViewScoped
@Getter
@Setter
@Controller
public class PasswordResetController implements Serializable {

    @ManagedProperty("#{passwordResetService}")
    private PasswordResetService passwordResetService;

    private String email;
    private String username;

    private String newPassword;
    private String confirmPassword;

    private PasswordToken validatedToken;

    public void requestReset() {
        try {
            String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
            passwordResetService.requestPasswordChange(this.email, this.username, contextPath);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Request Submitted.", "If the username and email match an account, a reset link has been sent."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error", e.getMessage()));
        }
    }

    public void validateTokenOnPageLoad(PhaseEvent event) {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String tokenId = params.get("token");

            if (tokenId != null && !tokenId.isEmpty()) {
                try {
                    this.validatedToken = passwordResetService.getTokenById(tokenId);
                } catch (Exception e) {
                    this.validatedToken = null; // Mark as invalid
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
            passwordResetService.changePassword(this.validatedToken, this.newPassword);
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
