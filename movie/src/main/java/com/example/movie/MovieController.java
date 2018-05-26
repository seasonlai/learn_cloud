package com.example.movie;

import com.google.common.collect.Maps;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import feign.Client;
import feign.Contract;
import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.feign.FeignAutoConfiguration;
import org.springframework.cloud.netflix.feign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;

/**
 * Created by Administrator on 2018/5/21.
 */
@Import(FeignClientsConfiguration.class)
@RestController
public class MovieController {

    static Logger LOGGER = LoggerFactory.getLogger(MovieController.class);
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    DiscoveryClient discoveryClient;//微服务客户端

    @Autowired
    LoadBalancerClient loadBalancerClient;//有负载均衡功能的

    @Autowired
    UserFeignClient userFeignClient;


    UserFeignClient userClient;
    UserFeignClient adminClient;

    @Autowired
    public MovieController(Decoder decoder, Encoder encoder, Client client, Contract contract){
        this.userClient = Feign.builder().client(client).encoder(encoder)
                .decoder(decoder).contract(contract)
                .requestInterceptor(new BasicAuthRequestInterceptor("user","password1"))
                .target(UserFeignClient.class,"http://provider-user/");
        this.adminClient = Feign.builder().client(client).encoder(encoder)
                .decoder(decoder).contract(contract)
                .requestInterceptor(new BasicAuthRequestInterceptor("admin","password2"))
                .target(UserFeignClient.class,"http://provider-user/");
    }

    @GetMapping("/user/{id}")
    public User findById(@PathVariable("id")Long id){
//        return restTemplate.getForObject("http://provider-user/"+id,User.class);
        return userFeignClient.findById(id);
    }

    @HystrixCommand(fallbackMethod = "findByFallback",commandProperties = {
            //请求超时时间
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "5500"),
            @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds",value = "10000"),
            //线程隔离策略:默认线程隔离，可指定信号量隔离
//            @HystrixProperty(name = "execution.isolation.strategy",value = "SEMAPHORE")
    },threadPoolProperties = {
            @HystrixProperty(name = "coreSize",value = "1"),
            @HystrixProperty(name = "maxQueueSize",value = "10"),
    })
    @GetMapping("/user-user/{id}")
    public User findUserById(@PathVariable("id")Long id){
        return userClient.findById(id);
    }

    @GetMapping("/user-admin/{id}")
    public User findAdminById(@PathVariable("id")Long id){
        return adminClient.findById(id);
    }

    @GetMapping("/user-instance")
    public List<ServiceInstance> showInfo(){
        return discoveryClient.getInstances("provider-user");
    }

    @GetMapping("/log-instance")
    public void logUserInstance(){
        ServiceInstance serviceInstance =  loadBalancerClient.choose("provider-user");

        LOGGER.info("{},{},{}",serviceInstance.getServiceId(),serviceInstance.getHost(),serviceInstance.getPort());
    }


    public User findByFallback(Long id){
        User user = new User();
        user.setId(-1L);
        user.setName("默认用户");
        return user;
    }
}
