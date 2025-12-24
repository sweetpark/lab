package com.example.playground.validation.source.version_1;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ValidateVersion1 {

    private final CustomValidateV1 testValidate;;

    @Data
    public static class TestDto{

        private String name;
        private String password;
        private String phone;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(testValidate);
    }

    @RequestMapping(value="/api/v1/test", method = RequestMethod.POST)
    public ResponseEntity<?> TestDtoVersion1(@Validated @RequestBody TestDto testDto, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(
                    bindingResult.getFieldError().getCodes()
                    ,HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(testDto, HttpStatus.OK);
    }


}
