package com.wolfcode.user.mngt.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wolfcode.eventservice.dto.EventCategory;

import java.time.LocalDate;
import java.time.LocalTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EventResponse(Long id,
                            String eventCode,
                            String eventName,
                            String description,
                            LocalDate date,
                            LocalTime time,
                            String location,
                            String organizer,
                            EventCategory eventCategory) {
}
