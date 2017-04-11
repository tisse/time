package jm.controller;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by vk on 05.04.17.
 */
public abstract class BaseBean {

    public abstract String getTitle();

    protected HttpServletRequest getRequest() {
        return (HttpServletRequest) this.getFacesContext().getExternalContext().getRequest();
    }

    protected HttpSession getSession() {
        return this.getRequest().getSession();
    }

    protected HttpServletResponse getResponse() {
        return ((HttpServletResponse) this.getFacesContext().getExternalContext().getResponse());
    }

    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    protected void redirect(String url) {
        try {
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            context.redirect(url);
            getFacesContext().responseComplete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
