package jm.controller;

import jm.model.Person;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

/**
 * Created by vk on 10.04.17.
 */

@ManagedBean
@ViewScoped
public class IndexBean extends BaseBean {

    @ManagedProperty(value = "#{loginData}")
    private LoginData loginData;


    @Override
    public String getTitle() {
        return "Main";
    }

    @PostConstruct
    private void prepare() {
    }

    public LoginData getLoginData() {
        return loginData;
    }

    public void setLoginData(LoginData loginData) {
        this.loginData = loginData;
    }
}
