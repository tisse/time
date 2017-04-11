package jm.controller;

import jm.dao.EventDao;
import jm.filter.EventFilter;
import jm.model.Event;
import jm.model.Person;
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
public class DataListBean extends BaseBean {

    @EJB private EventDao eventDao;
    private EventFilter filter;

    @ManagedProperty(value = "#{loginData}")
    private LoginData loginData;


    private Person person;


    @PostConstruct
    private void prepare() {

        if (null == loginData || null == loginData.getPerson()){
            redirect(getRequest().getContextPath()+"/login/index.html");
            return;
        }

        person = loginData.getPerson();

        filter = new EventFilter();

        if (null != person) {
            filter.setPerson(person.getName());
        }

        this.setModel(new LazyDataModel<Event>() {
            @Override
            public List<Event> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

                filter.setCount(Optional.ofNullable(Long.valueOf(pageSize)));
                filter.setStart(Optional.ofNullable(Long.valueOf(first)));

                List<Event> result = eventDao.list(filter);
                int count = eventDao.count(filter).intValue();
                getModel().setRowCount(count);
                return result;
            }
        });

    }

    public void search() {
    }

    private LazyDataModel<Event> model;

    public LazyDataModel<Event> getModel() {
        return model;
    }

    public void setModel(LazyDataModel<Event> model) {
        this.model = model;
    }

    public EventFilter getFilter() {
        return filter;
    }

    public void setFilter(EventFilter filter) {
        this.filter = filter;
    }

    public String getTitle() {
        return "Data";
    }

    public LoginData getLoginData() {
        return loginData;
    }

    public void setLoginData(LoginData loginData) {
        this.loginData = loginData;
    }
}
