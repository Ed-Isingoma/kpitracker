package org.pahappa.systems.kpiTracker.views.users;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.primefaces.PrimeFaces;
import org.sers.webutils.client.views.presenters.DialogForm;
import org.sers.webutils.model.Gender;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.RoleService;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean(name = "userFormDialog")
@SessionScoped
@Getter @Setter
public class UserFormDialog extends DialogForm<User> {

    private static final long serialVersionUID = 1L;
    private transient UserService userService;
    private transient RoleService roleService;

    private List<Gender> listOfGenders;
    private List<Role> allRoles;
    private Set<Role> selectedRoles = new HashSet<>();

    // RENAMED PROPERTY
    private boolean editMode = false;

    public UserFormDialog() {
        super(HyperLinks.USER_FORM_DIALOG, 700, 450);
    }

    @PostConstruct
    public void init() {
        this.userService = ApplicationContextProvider.getBean(UserService.class);
        this.roleService = ApplicationContextProvider.getBean(RoleService.class);
        this.listOfGenders = Arrays.asList(Gender.values());
        this.allRoles = this.roleService.getRoles();
    }

    @Override
    public User getModel() {
        if (super.getModel() == null) {
            super.setModel(new User());
        }
        return super.getModel();
    }

    @Override
    public void persist() throws ValidationFailedException {
        this.model.setRoles(this.selectedRoles);
        this.userService.saveUser(this.model);
    }

    @Override
    public void save() {
        try {
            this.persist();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Action Successful", "User saved successfully."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Action Failed", e.getMessage()));
            e.printStackTrace();
        }
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.setModel(new User());
        this.selectedRoles = new HashSet<>();
        this.editMode = false; // Use renamed property
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        this.editMode = true; // Use renamed property
        if (this.model.getRoles() != null) {
            this.selectedRoles = new HashSet<>(this.model.getRoles());
        } else {
            this.selectedRoles = new HashSet<>();
        }
    }

    @Override
    public void pageLoadInit() {}

    @Override
    public void beanInit() {}
}