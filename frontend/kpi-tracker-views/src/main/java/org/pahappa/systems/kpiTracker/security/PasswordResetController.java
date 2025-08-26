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
import org.sers.webutils.server.core.service.PasswordResetService;
import org.springframework.web.context.support.WebApplicationContextUtils;

//@ManagedBean
//@ViewScoped
@Getter
@Setter
public class PasswordResetController implements Serializable {

//    @ManagedProperty("#{passwordResetService}")
    private PasswordResetService passwordResetService;

    private String email;
    private String username;

    private String newPassword;
    private String confirmPassword;

    private PasswordToken validatedToken;

    private static final ExecutorService emailExecutor = Executors.newSingleThreadExecutor();

    @PostConstruct
    public void init() {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance()
                .getExternalContext().getContext();

        this.passwordResetService = WebApplicationContextUtils
                .getRequiredWebApplicationContext(servletContext)
                .getBean("passwordResetService", PasswordResetService.class);
    }

    public void requestReset() {
        // Capture the current values to use them in the background thread
        final String userEmail = this.email;
        final String userName = this.username;
        final String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();

        System.out.println("ACTION: Submitting email task to background thread for user: " + userName);

        // This is the background task
        Runnable emailTask = () -> {
            try {
                System.out.println("BACKGROUND: Task started for: " + userName);

                // This is the long-running call that is currently freezing your application
                passwordResetService.requestPasswordChange(userEmail, userName, contextPath);

                System.out.println("BACKGROUND: Task finished successfully for: " + userName);

            } catch (Exception e) {
                // THIS WILL NOW CATCH THE REAL ERROR AND PRINT IT TO THE LOG
                System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.err.println("!!!   ERROR IN BACKGROUND EMAIL TASK - THE REAL CAUSE    !!!");
                System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                e.printStackTrace();
            }
        };

        // Submit the task to run in the background. The main thread will not wait.
        emailExecutor.submit(emailTask);

        // Because the main thread is not blocked, this message appears instantly on the screen.
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Request Submitted.", "If the username and email match an account, a reset link has been sent."));
    }

//    public void requestReset() {
//        try {
//            String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
//            passwordResetService.requestPasswordChange(this.email, this.username, contextPath);
//
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
//                    "Request Submitted.", "If the username and email match an account, a reset link has been sent."));
//        } catch (Exception e) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                    "Error", e.getMessage()));
//        }
//    }

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
