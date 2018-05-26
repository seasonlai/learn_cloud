package com.example.movie;

import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Administrator on 2018/5/25.
 */
@FeignClient(name = "provider-user"
        , configuration = FeignLogConfiguration.class
//        , fallback = UserFeignClient.FeignClientFallback.class
        ,fallbackFactory = UserFeignClient.FeignClientFallbackFactory.class
)
public interface UserFeignClient {

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public User findById(@PathVariable("id") Long id);


    @Component
    class FeignClientFallback implements UserFeignClient {
        @Override
        public User findById(Long id) {
            User user = new User();
            user.setId(-1L);
            user.setName("默认用户");
            return user;
        }
    }

    @Component
    class FeignClientFallbackFactory implements FallbackFactory<UserFeignClient>{

        private static final Logger LOGGER = LoggerFactory.getLogger(FeignClientFallbackFactory.class);

        @Override
        public UserFeignClient create(Throwable throwable) {
            return new UserFeignClient() {
                @Override
                public User findById(Long id) {
                    FeignClientFallbackFactory.LOGGER.info("fallback reason was：",throwable);
                    User user = new User();
                    user.setId(-1L);
                    user.setName("默认用户");
                    return user;
                }
            };
        }
    }
}

