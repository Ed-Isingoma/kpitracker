package org.pahappa.systems.client.converters;

import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.server.core.dao.BaseDao;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.util.Map;

@FacesConverter("entityConverter")
public class EntityConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent component, String value) {
        if (value != null) {
            return this.getAttributesFrom(component).get(value);
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext ctx, UIComponent component, Object value) {
        if (value != null && !"".equals(value)) {
            BaseEntity entity = (BaseEntity) value;
            this.addAttribute(component, entity);
            if (entity.getId() != null) {
                return entity.getId();
            }
        }
        return (String) value;
    }

    protected void addAttribute(UIComponent component, BaseEntity o) {
        this.getAttributesFrom(component).put(o.getId(), o);
    }

    protected Map<String, Object> getAttributesFrom(UIComponent component) {
        return component.getAttributes();
    }
}