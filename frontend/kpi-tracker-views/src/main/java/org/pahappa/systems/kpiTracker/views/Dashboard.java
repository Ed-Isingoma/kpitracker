package org.pahappa.systems.kpiTracker.views;

import com.googlecode.genericdao.search.Search;
;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.sers.webutils.client.controllers.WebAppExceptionHandler;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.security.User;
import org.sers.webutils.model.utils.SortField;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;


@ManagedBean(name = "dashboard")
@ViewScoped
@ViewPath(path = HyperLinks.DASHBOARD)
public class Dashboard extends WebAppExceptionHandler implements Serializable {

    private static final long serialVersionUID = 1L;
    @Setter
    @Getter
    private User loggedinUser;

    Search search = new Search();
    private String searchTerm;
    private SortField selectedSortField;

    @Setter
    @SuppressWarnings("unused")
    private String viewPath;

    @PostConstruct
    public void init() {
        loggedinUser = SharedAppData.getLoggedInUser();
    }

    /**
     * @return the viewPath
     */
    public String getViewPath() {
        return Dashboard.class.getAnnotation(ViewPath.class).path();
    }


}
