package jm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import jm.dao.PersonDao;
import jm.filter.PersonFilter;
import jm.model.GoogleResponse;
import jm.model.Person;
import jm.model.TimeResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

/**
 * Created by vk on 11.04.17.
 */

@Stateless
public class OauthGoogleBusiness {

    public static final String API_KEY = "API_KEY";
    public static final String API_SECRET = "API_SECRET";
    public static final String SCOPE = "https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/admin.directory.group.member.readonly";
    @Inject
    private Logger logger;

    @EJB
    private PersonDao personDao;


    private static final String CALLBACK = "http://localhost:8080/time-web/login/oauth.html";

    public TimeResponse<String> prepareUrl(){
        OAuth20Service service = new ServiceBuilder().apiKey(API_KEY).apiSecret(API_SECRET)
                .scope(SCOPE)
                .callback(CALLBACK).build(GoogleApi20.instance());
        String authorizationUrl = service.getAuthorizationUrl();


        TimeResponse<String> response = new TimeResponse<>();
        response.setResponseCode(TimeResponse.ResponseCode.SUCCESS);
        response.setResponseBody(authorizationUrl);
        return response;

    }

    public TimeResponse<Person> auth(String code){
        OAuth20Service service = new ServiceBuilder().apiKey(API_KEY).apiSecret(API_SECRET)
                .scope(SCOPE)
                .callback(CALLBACK).build(GoogleApi20.instance());
        try {


            OAuth2AccessToken accessToken = service.getAccessToken(code);
            OAuthRequest oAuthRequest = new OAuthRequest(Verb.GET, "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + code);
            service.signRequest(accessToken, oAuthRequest);


            HttpGet httpGet = new HttpGet("https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + code);
            httpGet.addHeader("Authorization", "Bearer " + accessToken.getAccessToken());


            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse execute = httpClient.execute(httpGet);

            HttpEntity httpEntity = execute.getEntity();

            String string = EntityUtils.toString(httpEntity);

            ObjectMapper objectMapper = new ObjectMapper();
            GoogleResponse googleResponse = objectMapper.readValue(string, GoogleResponse.class);

            logger.info(googleResponse.toString());

            if (null != googleResponse) {
                PersonFilter personFilter = new PersonFilter();
                personFilter.setExtName(googleResponse.getName());

                logger.info(personFilter.toString());

                List<Person> personList = personDao.list(personFilter);

                logger.info(String.valueOf(personList.size()));


                if (CollectionUtils.isNotEmpty(personList)) {
                    Person person = personList.get(0);

                    TimeResponse<Person> response = new TimeResponse<>();
                    response.setResponseCode(TimeResponse.ResponseCode.SUCCESS);
                    response.setResponseBody(person);
                    return response;
                }

            }

            logger.info(string);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        TimeResponse<Person> response = new TimeResponse<>();
        response.setResponseCode(TimeResponse.ResponseCode.FAIL);
        return response;


    }

}
