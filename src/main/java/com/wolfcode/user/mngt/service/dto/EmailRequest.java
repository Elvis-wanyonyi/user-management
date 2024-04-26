package com.wolfcode.user.mngt.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {

    private String from;
    private String to;
    private String subject;
    private String text;
}
