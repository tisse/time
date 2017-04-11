package jm.rest;

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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

/**
 * Created by vk on 10.04.17.
 */

@Path("/google")
public class GoogleResource {

    public static final String CALLBACK = "http://localhost:8080/time-web/rest/google/code";

    @Inject
    private Logger logger;

    @EJB
    private PersonDao personDao;

    @GET
    @Path("/code")
    public Response code(@Context HttpServletResponse response,
                         @Context HttpServletRequest request,
                         @QueryParam("code") String code) {
        OAuth20Service service = new ServiceBuilder().apiKey("770230208150-q06s7om6mc83j0mn3uuivmdbpr55h2tu.apps.googleusercontent.com").apiSecret("mbdCnFDZY4mJ93UokNYEuzJA").scope("https://www.googleapis.com/auth/userinfo.profile")
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
                    request.getSession().setAttribute("person", person);


                    logger.info(person.toString());

                    String scheme = request.getScheme();
                    String serverName = request.getServerName();
                    int portNumber = request.getServerPort();
                    String contextPath = request.getContextPath();

                    String str = scheme + "://" + serverName + ":" + portNumber + contextPath + "/index.html";
                    logger.info(str);

                    return Response.temporaryRedirect(URI.create(str)).build();
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
        return Response.status(Response.Status.BAD_REQUEST).build();
    }


}
