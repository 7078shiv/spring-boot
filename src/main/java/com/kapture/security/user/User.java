package com.kapture.security.user;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails{
    // make id primary key
    @Id
    // auto increment
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "emp_id")
    private int emp_id;
    @Column(name = "client_id") // This annotation maps the property to the correct column
    private long client_id;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "last_login_time")
    @Temporal(TemporalType.TIMESTAMP)@UpdateTimestamp
    private Date lastLoginTime ;
    @Column(name = "last_password_reset")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastPasswordReset;
    @Column(name = "enable", columnDefinition = "TINYINT(1)")
    private int enable;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role=Role.USER;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return List.of(new SimpleGrantedAuthority(role.USER.name()));
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
    @Override
    public String toString(){
        return "username: "+this.username+" password: "+this.password;
    }
}
