package com.kapture.security.repository;

import com.kapture.security.user.User;
import com.kapture.security.util.QueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository{

    @Autowired
    private QueryUtil queryUtil;

    @Override
    public Optional<User> findByUsername(String username) {
        String query = "select * from users where username=:username";
        Map<String,Object> parametersToSet = new HashMap<>();
        parametersToSet.put("username",username);
        List<User> list= queryUtil.runQueryHelper(query,parametersToSet,User.class,null,null);

        Optional<User> optionalUser = (list==null || list.isEmpty()) ? Optional.empty() : Optional.of(list.get(0));
        return optionalUser;
    }

    public boolean isUserAvailable(String username) {
        String query = "select * from users where username=:username";
        Map<String,Object> parametersToSet = new HashMap<>();
        parametersToSet.put("username",username);
        List<User> list= queryUtil.runQueryHelper(query,parametersToSet,User.class,null,null);
       return (list==null || list.isEmpty());
    }

    @Override
    public User findById(int ID) {
        try {
            String query = "select * from users where id=:id";
            Map<String, Object> parametersToSet = new HashMap<>();
            parametersToSet.put("id", ID);
            List<User> users = queryUtil.runQueryHelper(query, parametersToSet, User.class, null, null);
            return users.isEmpty() ? null : users.get(0);
        }
        catch (NullPointerException e){
            System.out.println("User Not Found");
            return null;
        }
    }

    @Override
    public User saveOrUpdateUser(User user) {
        return queryUtil.saveOrUpdateUser(user)?user:null;
    }

    @Override
    public List<User> findAll() {
        String query = "select * from users";
        Map<String, Object> parametersToSet = new HashMap<>();
        return queryUtil.runQueryHelper(query, parametersToSet, User.class, null, null);
    }

//    @Override
//    public User updateUser(User updatedUser){
//        int id=updatedUser.getId();
//        long clientId = updatedUser.getClient_id();
//        int empId = updatedUser.getEmp_id();
//        String username = updatedUser.getUsername();
//        String password = updatedUser.getPassword();
//        Date lastPasswordReset = updatedUser.getLastPasswordReset();
//        int enable = updatedUser.getEnable();
//        String query = "UPDATE users SET client_id = :clientId , emp_id = :empId , username = :username , password =:password, last_password_reset=:lastPasswordReset, enable=:enable where id=:id";
//        Map<String,Object> parameterToPass = new HashMap<>();
//        parameterToPass.put("id",id);
//        parameterToPass.put("clientId",clientId);
//        parameterToPass.put("empId",empId);
//        parameterToPass.put("username",username);
//        parameterToPass.put("password",password);
//        parameterToPass.put("lastPasswordReset",lastPasswordReset);
//        parameterToPass.put("enable",enable);
//       return  queryUtil.updateQueryHelper(parameterToPass,query)>0?updatedUser:null;
//    }

//    public User saveUser(User user) {
//        long clientId = user.getClient_id();
//        int empId = user.getEmp_id();
//        String userName = user.getUsername();
//        String password = user.getPassword();
//        int enable = user.getEnable();
//        String queryString = "INSERT INTO users (client_id, emp_id, username, password, last_login_time,last_password_reset,enable) " +
//                "VALUES (:clientId, :empId, :userName, :password, :lastLoginTime,:lastPasswordReset,:enable)";
//        Map<String, Object> parametersToSet = new HashMap<>();
//        parametersToSet.put("clientId", clientId);
//        parametersToSet.put("empId", empId);
//        parametersToSet.put("userName", userName);
//        parametersToSet.put("password", password);
//        parametersToSet.put("lastLoginTime", new Date(System.currentTimeMillis()));
//        parametersToSet.put("lastPasswordReset", new Date(System.currentTimeMillis()));
//        parametersToSet.put("enable",enable);
//        return queryUtil.executeSaveUserQuery(queryString,parametersToSet)>0?user:null;
//    }
}
