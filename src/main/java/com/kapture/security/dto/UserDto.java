package com.kapture.security.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    public int id;
    public int empId;
    public long clientID;
    public String username;
    public Date lastLoginTime;
    public Date lastPasswordReset;
    public int enable;

    @Override
    public String toString(){

        return "username :- "+this.username+" " +
                "password "+"enable "+this.enable+" emp_id "
                +this.empId+" client_id "+this.clientID+" lastLoginTime "+
                this.lastLoginTime+" lastPasswordReset "+this.lastPasswordReset;
    }
}
