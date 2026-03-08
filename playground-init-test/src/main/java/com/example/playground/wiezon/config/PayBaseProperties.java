package com.example.playground.wiezon.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "pay")
public class PayBaseProperties {
    @NotBlank(message = "pay.ptnCd 확인필요")
    private String ptnCd;

    @NotBlank(message = "pay.mid 확인필요")
    @Pattern(regexp = "^[A-Za-z0-9]{10}$", message = "pay.mid 10자리")
    private String mid;

    @NotBlank(message = "pay.gid 확인필요")
    @Pattern(regexp = "^[A-Za-z0-9]{10}$", message = "pay.gid 10자리")
    private String gid;

    @NotBlank(message = "pay.vid 확인필요")
    @Pattern(regexp = "^[A-Za-z0-9]{10}$", message = "pay.vid 10자리")
    private String vid;
}
