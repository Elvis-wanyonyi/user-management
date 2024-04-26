package com.wolfcode.user.mngt.service.feignClients;

import com.wolfcode.eventservice.dto.MyEvents;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@FeignClient(name = "EVENTS", path = "/api/v1/events")
public interface EventsClient {


    @GetMapping("/organizer/{organizer}")
    @ResponseStatus(HttpStatus.OK)
    List<MyEvents> getEventByOrganizer(@PathVariable String organizer);
}
