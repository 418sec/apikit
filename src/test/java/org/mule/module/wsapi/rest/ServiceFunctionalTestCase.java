
package org.mule.module.wsapi.rest;

import static com.jayway.restassured.RestAssured.given;

import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.tck.junit4.rule.DynamicPort;
import org.mule.util.IOUtils;

import com.jayway.restassured.RestAssured;

import java.io.IOException;
import java.io.InputStream;

import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;

public class ServiceFunctionalTestCase extends FunctionalTestCase
{
    @Rule
    public DynamicPort serverPort = new DynamicPort("serverPort");

    @Override
    protected void doSetUp() throws Exception
    {
        RestAssured.port = serverPort.getNumber();
        super.doSetUp();
    }

    @Override
    protected String getConfigResources()
    {
        return "org/mule/module/wsapi/rest/service-config.xml, org/mule/module/wsapi/test-flows-config.xml";
    }

    // Base URI

    @Test
    public void baseUriPutNotSupported() throws Exception
    {
        given().expect()
            .response()
            .statusCode(405)
            .header("Allow", Matchers.equalToIgnoringCase("GET"))
            .when()
            .put("/api");
    }

    @Test
    public void baseUriPostNotSupported() throws Exception
    {
        given().expect()
            .response()
            .statusCode(405)
            .header("Allow", Matchers.equalToIgnoringCase("GET"))
            .when()
            .post("/api");
    }

    @Test
    public void baseUriDeleteNotSupported() throws Exception
    {
        given().expect()
            .response()
            .statusCode(405)
            .header("Allow", Matchers.equalToIgnoringCase("GET"))
            .when()
            .delete("/api");
    }

    @Test
    public void baseUriGetUnsupportedContentTypes() throws Exception
    {
        given().header("Accept", "application/json").expect().response().statusCode(406).when().get("/api");
        given().header("Accept", "text/pain").expect().response().statusCode(406).when().get("/api");
    }

    @Test
    public void baseUriGetHtml() throws Exception
    {
        given().expect().response().statusCode(200).when().get("/api");
        given().header("Accept", "text/html").expect().response().statusCode(200).when().get("/api");
    }

    @Test
    public void baseUriGetHtmlResources() throws Exception
    {
        given().expect().response().statusCode(200).body(new TypeSafeMatcher<InputStream>()
        {

            @Override
            public void describeTo(Description description)
            {
            }

            @Override
            protected boolean matchesSafely(InputStream item)
            {
                try
                {
                    return IOUtils.contentEquals(item,
                        getClass().getResourceAsStream("org/mule/modules/rest/swagger/lib/swagger.js"));
                }
                catch (IOException e)
                {
                    return false;
                }
            }
        })
            .when()
            .get("/api/_swagger/lib/swagger.js");
    }

    @Test
    public void baseUriGetSwaggerJson() throws Exception
    {
        given().contentType("application/swagger+json")
            .expect()
            .response()
            .statusCode(200)
            .body(
                Matchers.equalTo("{\"apiVersion\":\"1.0\",\"swaggerVersion\":\"1.0\",\"basePath\":\"http://localhost:"
                                 + serverPort.getNumber()
                                 + "/api\",\"apis\":[{\"path\":\"/leagues\",\"description\"}]}"))
            .when()
            .get("/api");
    }

    @Test
    public void baseUriGetSwaggerJsonResourcesDotJson() throws Exception
    {
        given().expect()
            .response()
            .statusCode(200)
            .body(
                Matchers.equalTo("{\"apiVersion\":\"1.0\",\"swaggerVersion\":\"1.0\",\"basePath\":\"http://localhost:"
                                 + serverPort.getNumber()
                                 + "/api\",\"apis\":[{\"path\":\"/leagues\",\"description\"}]}"))
            .when()
            .get("/api/resources.json");
    }

}
