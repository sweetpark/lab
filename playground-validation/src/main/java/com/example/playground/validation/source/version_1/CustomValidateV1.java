package com.example.playground.validation.source.version_1;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CustomValidateV1 implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(ValidateVersion1.TestDto.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        ValidateVersion1.TestDto testDto = (ValidateVersion1.TestDto) target;
        String regex = "^([0-9]{0,3})-([0-9]{0,4})-([0-9]{0,4})$";

        if(testDto.getName() == null || testDto.getName().trim().equals("")){
            errors.rejectValue("name","name.required");
        }
        if(testDto.getPassword() == null || testDto.getPassword().trim().equals("")){
            errors.rejectValue("password","password.required");
        }

        if(testDto.getPhone() == null || testDto.getPhone().trim().equals("")){
            errors.rejectValue("phone","phone.required");
        }else if(!testDto.getPhone().matches(regex)){
            errors.rejectValue("password","password.match");
        }


    }
}
