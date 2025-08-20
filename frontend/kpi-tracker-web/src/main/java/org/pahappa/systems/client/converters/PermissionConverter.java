package org.pahappa.systems.client.converters;

import org.sers.webutils.model.security.Permission;
import org.sers.webutils.server.core.service.PermissionService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("permissionConverter")
public class PermissionConverter implements Converter {

	private PermissionService permissionService;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}

		try {
			if (permissionService == null) {
				permissionService = ApplicationContextProvider.getBean(PermissionService.class);
			}

			return permissionService.getObjectById(value);
		} catch (Exception e) {
			throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Cannot convert permission"), e);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return "";
		}

		if (value instanceof Permission) {
			Permission permission = (Permission) value;
			return permission.getId();
		} else {
			throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Object is not a Permission"));
		}
	}
}