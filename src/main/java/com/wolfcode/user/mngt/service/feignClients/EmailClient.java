package com.wolfcode.user.mngt.service.feignClients;

import com.wolfcode.user.mngt.service.dto.EmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "EMAIL-SERVICE", path = "/api/v1/email")
public interface EmailClient {

    @PostMapping("/send")
    ResponseEntity<String> sendEmail(@RequestBody EmailRequest request);
}
