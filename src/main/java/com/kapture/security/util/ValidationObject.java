package com.kapture.security.util;
import com.kapture.security.dto.RegisterRequestDto;
public class ValidationObject {
    public static boolean validateDto(RegisterRequestDto registerRequestDto){
        if(registerRequestDto.getClient_id()<0){
            return false;
        }
        else if(registerRequestDto.getEmp_id()<0){
            return false;
        }
        else if(registerRequestDto.getEnable()<0 || registerRequestDto.getEnable()>1){
            return false;
        }
        else if(registerRequestDto.getPassword().length()<8){
            return false;
        }
        return true;
    }
}
