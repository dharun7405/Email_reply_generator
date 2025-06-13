package com.email.writer.app;

import lombok.Data;

@Data
public class EmailRequest {
    String emailContent;
    String tone;
}
