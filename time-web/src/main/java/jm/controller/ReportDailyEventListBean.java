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

    @EJB private ReportDailyEventDao reportDailyEventDao;
    private ReportDailyEventFilter filter;
    private List<ReportDailyEvent> events;

    @PostConstruct
    private void prepare(){
        filter = new ReportDailyEventFilter();
        events = reportDailyEventDao.report();

    }

    public void search(){
        events = reportDailyEventDao.report();
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
}
