package jm.controller;

import jm.dao.EventDao;
import jm.dao.ReportDailyEventDao;
import jm.filter.EventFilter;
import jm.filter.ReportDailyEventFilter;
import jm.model.Event;
import jm.model.ReportDailyEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by vk on 05.04.17.
 */

@ManagedBean
@ViewScoped
public class ReportDailyEventListBean extends BaseBean {

    @ManagedProperty(value = "#{loginData}")
    private LoginData loginData;


    @EJB private ReportDailyEventDao reportDailyEventDao;
    private ReportDailyEventFilter filter;
    private List<ReportDailyEvent> events;

    @PostConstruct
    private void prepare(){
        if (null == loginData || null == loginData.getPerson()){
            redirect(getRequest().getContextPath()+"/login/index.html");
            return;
        }

        filter = new ReportDailyEventFilter();
        filter.setPerson(loginData.getPerson().getName());
        events = reportDailyEventDao.report(filter);

    }

    public void search(){
        events = reportDailyEventDao.report(filter);
    }

    public ReportDailyEventFilter getFilter() {
        return filter;
    }

    public void setFilter(ReportDailyEventFilter filter) {
        this.filter = filter;
    }

    public String getTitle(){
        return "Report";
    }

    public List<ReportDailyEvent> getEvents() {
        return events;
    }

    public void setEvents(List<ReportDailyEvent> events) {
        this.events = events;
    }

    public LoginData getLoginData() {
        return loginData;
    }

    public void setLoginData(LoginData loginData) {
        this.loginData = loginData;
    }
}
