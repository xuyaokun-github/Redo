package cn.com.kun.component.redo.invoke;

import cn.com.kun.component.redo.bean.vo.RedoExecResVo;
import cn.com.kun.component.redo.bean.vo.RedoResultVo;
import cn.com.kun.component.redo.configuration.RedoProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * 默认实现：走RestTemplate
 *
 * author:xuyaokun_kzx
 * date:2024/11/12 15:06
 * desc:
*/
@Component
public class DefaultRedoExecInvoker implements RedoExecInvoker{

    /**
     * 假如容器里没有可用的RestTemplate，则创建一个简单的RestTemplate
     */
    @Autowired(required = false)
    private RestTemplate restTemplate;

    @Autowired
    private RedoProperties redoProperties;

    /**
     * 微服务应用名
     */
    @Value("${spring.application.name}")
    private String applicationName;

    @PostConstruct
    public void init(){
        if (restTemplate == null){
            restTemplate = new RestTemplate(newClientHttpRequestFactory());
        }
    }

    private ClientHttpRequestFactory newClientHttpRequestFactory() {

        PoolingHttpClientConnectionManager pollingConnectionManager = new PoolingHttpClientConnectionManager(55, TimeUnit.SECONDS);
        pollingConnectionManager.setMaxTotal(200);
        pollingConnectionManager.setDefaultMaxPerRoute(50);
        pollingConnectionManager.setValidateAfterInactivity(1000 * 2000);

        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setConnectionManager(pollingConnectionManager);
        httpClientBuilder.setConnectionTimeToLive(5500, TimeUnit.MILLISECONDS);

        HttpClient httpClient = httpClientBuilder.build();
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        clientHttpRequestFactory.setConnectionRequestTimeout(10000);//ms
        clientHttpRequestFactory.setReadTimeout(10000);//ms
        clientHttpRequestFactory.setConnectTimeout(15000);//ms
        return clientHttpRequestFactory;
    }


    @Override
    public RedoResultVo<RedoExecResVo> exec(String applicationName) {

        String domain = redoProperties.getDomainMap().get(applicationName);
        String url = domain + "/" + applicationName + "/redo-exec/exec" + "?applicationName=" + applicationName;
        ParameterizedTypeReference<RedoResultVo<RedoExecResVo>> responseBodyType = new ParameterizedTypeReference<RedoResultVo<RedoExecResVo>>(){};
        ResponseEntity<RedoResultVo<RedoExecResVo>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, responseBodyType);
        RedoResultVo<RedoExecResVo> resultVo = responseEntity.getBody();
        return resultVo;
    }

    @Override
    public RedoResultVo<String> stopQuery() {

        String domain = redoProperties.getGlobalDomain();
        RedoResultVo<String> resultVo = RedoResultVo.valueOfSuccess("Y");
        if (StringUtils.isNotEmpty(domain)){
            String url = domain + "/" + applicationName + "/redo-control/stop-query";
            ParameterizedTypeReference<RedoResultVo<String>> responseBodyType = new ParameterizedTypeReference<RedoResultVo<String>>(){};
            ResponseEntity<RedoResultVo<String>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, responseBodyType);
            resultVo = responseEntity.getBody();
        }

        return resultVo;
    }

}
