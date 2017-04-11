package jm.controller;

import jm.model.Person;
import jm.model.TimeResponse;
import jm.service.OauthGoogleBusiness;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Created by vk on 10.04.17.
 */

@ManagedBean
public class OauthBean extends BaseBean {

    @Inject
    private Logger logger;

    @ManagedProperty(value = "#{loginData}")
    private LoginData loginData;

    @EJB
    private OauthGoogleBusiness oauthGoogleBusiness;

    @PostConstruct
    private void prepare() {

        String code = getRequest().getParameter("code");

        logger.info(code);

        TimeResponse<Person> response = oauthGoogleBusiness.auth(code);
        if (response.getResponseCode().equals(TimeResponse.ResponseCode.SUCCESS)) {

            Person person = response.getResponseBody();

            loginData.setPerson(person);

            logger.info(person.toString());
            String contextPath = getRequest().getContextPath();
            redirect(contextPath+"/index.html");
        }
    }

    @Override
    public String getTitle() {
        return null;
    }

    public LoginData getLoginData() {
        return loginData;
    }

    public void setLoginData(LoginData loginData) {
        this.loginData = loginData;
    }
}
