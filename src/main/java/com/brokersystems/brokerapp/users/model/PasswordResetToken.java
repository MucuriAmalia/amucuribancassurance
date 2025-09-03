package com.brokersystems.brokerapp.users.model;

import com.brokersystems.brokerapp.setup.model.User;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by HP on 4/26/2018.
 */

@Entity
@Table(name ="sys_brk_password_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pt_id")
    private Long id;

    @Column(nullable = false, unique = true,name = "pt_token")
    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "pt_user_id")
    private User user;

    @Column(nullable = false,name = "pt_exp_date")
    private Date expiryDate;


    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isExpired() {
        return new Date().after(this.expiryDate);
    }
}
