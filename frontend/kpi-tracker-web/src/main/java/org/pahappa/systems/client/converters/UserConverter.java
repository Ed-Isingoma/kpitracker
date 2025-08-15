package org.pahappa.systems.client.converters;

import org.pahappa.systems.kpiTracker.core.services.EmployeeUserService;
import org.pahappa.systems.kpiTracker.models.security.EmployeeUser;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("userConverter") // Use forClass for type-safety
public class UserConverter implements Converter {

	private static EmployeeUserService userService;

	// Lazy-load the service instance to improve performance
	private static synchronized EmployeeUserService getUserServiceInstance() {
		if (userService == null) {
			// This will now work because @Primary resolves the ambiguity for UserService
			userService = ApplicationContextProvider.getBean(EmployeeUserService.class);
		}
		return userService;
	}

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		if (arg2 == null || arg2.isEmpty())
			return null;
		return getUserServiceInstance().getUserById(arg2);
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object object) {
		if (object == null) {
			return "";
		}
		if (object instanceof User) {
			return ((User) object).getId();
		}
		return "";
	}
}