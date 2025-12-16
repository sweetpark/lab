package com.example.playground.validation.source.version_2;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ValidateVersion2 {

    public static class TestV2Dto{
        @NotBlank
        private String name;
        @PasswordValidate
        private String password;
        @Pattern(regexp = "^([0-9]{0,3})-([0-9]{0,4})-([0-9]{0,4})$")
        private String phone;

        public String getName() {return name;}
        public String getPassword() {return password;}
        public String getPhone() {return phone;}

        public void setName(String name) {this.name = name;}
        public void setPassword(String password) {this.password = password;}
        public void setPhone(String phone) {this.phone = phone;}
    }


    //BindingResult 생략 가능 ( 유효성 안 맞으면 -> 400 Response 전달)
    @ResponseBody
    @RequestMapping(value="/api/v2/test", method= RequestMethod.POST)
    public ResponseEntity<?> validateTestV2(@Validated @RequestBody TestV2Dto testDto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new ResponseEntity<>(bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
