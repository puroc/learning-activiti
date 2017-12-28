package com.example.activiti.testcase.rest.deploy;

import com.example.activiti.testcase.util.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:rest/deploy/application-context.xml")
public class DeploymentTestcase {

    @Autowired
    private RestTemplate restTemplate;

//    @Test
//    public void fd() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("auth_token", "asdfgh");
//        headers.set("Other-Header", "othervalue");
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        JSONObject parm = new JSONObject();
//        parm.put("parm", "1234");
//        HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(parm, headers);
//    }

    @Test
    public void deploy() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","Basic "+ Base64.encode("kermit:kermit"));
        HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
        ResponseEntity<net.sf.json.JSONObject> exchange = restTemplate.exchange("http://localhost:8080/activiti-rest/service/repository/deployments", HttpMethod.GET, requestEntity, net.sf.json.JSONObject.class);
        System.out.println(exchange.getStatusCode());
        System.out.println(exchange.getBody().getJSONArray("data").getJSONObject(0).get("id"));


    }
}
