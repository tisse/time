package jm.controller;

import jm.model.TimeResponse;
import jm.service.OauthGoogleBusiness;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import java.io.IOException;

/**
 * Created by vk on 10.04.17.
 */

@ManagedBean
public class LoginBean extends BaseBean {

    @EJB
    private OauthGoogleBusiness oauthGoogleBusiness;

    @Override
    public String getTitle() {
        return "Login";
    }

    public void google(){

        TimeResponse<String> response = oauthGoogleBusiness.prepareUrl();

        if (response.getResponseCode().equals(TimeResponse.ResponseCode.SUCCESS)){
            try {
                String authorizationUrl = response.getResponseBody();
                getResponse().sendRedirect(authorizationUrl);
                getFacesContext().responseComplete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

}
