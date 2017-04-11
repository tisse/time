package jm.controller;

import jm.model.Event;
import jm.service.TimeParser;
import org.primefaces.event.FileUploadEvent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.List;

/**
 * Created by vk on 21.03.17.
 */

@ManagedBean
@ViewScoped
public class UploadBean extends BaseBean {

    @ManagedProperty(value = "#{loginData}")
    private LoginData loginData;


    @EJB private TimeParser parser;
    private List<Event> events;

    @PostConstruct
    private void prepare(){
        if (null == loginData || null == loginData.getPerson()){
            redirect(getRequest().getContextPath()+"/login/index.html");
            return;
        }
    }

    public void handleFileUpload(FileUploadEvent event) {

        if (null != event.getFile()) {

            try {
                events = parser.parse(event.getFile().getInputstream());
                FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
                FacesContext.getCurrentInstance().addMessage(null, message);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public String getTitle(){
        return "Load";
    }


    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public LoginData getLoginData() {
        return loginData;
    }

    public void setLoginData(LoginData loginData) {
        this.loginData = loginData;
    }
}
