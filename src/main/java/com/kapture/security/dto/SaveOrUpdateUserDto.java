package com.kapture.security.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SaveOrUpdateUserDto {
    private int id;
    private int empId;
    private long clientId;
    private String username;
    private String password;
    private Date lastLoginTime;
    private Date lastPasswordReset;
    private int enable;

    @Override
    public String toString(){
        return "username :- "+this.username+" " +
                "password "+"enable "+this.enable+" emp_id "
                +this.empId+" client_id "+this.clientId+" lastLoginTime "+
                this.lastLoginTime+" lastPasswordReset "+this.lastPasswordReset;
    }
}
