package com.kapture.security.user;

import lombok.AllArgsConstructor;

import java.util.Date;
@AllArgsConstructor
public class AbstractUser {
    public int id;
    public int empId;
    public long clientID;
    public String username;
    public Date lastLoginTime;
    public Date lastPasswordReset;
}
