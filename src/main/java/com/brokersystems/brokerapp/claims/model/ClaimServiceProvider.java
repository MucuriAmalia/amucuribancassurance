package com.brokersystems.brokerapp.claims.model;

import com.brokersystems.brokerapp.setup.model.User;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
@Slf4j
@Table(name = "sys_brk_clm_srv_provider")
public class ClaimServiceProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "csp_id")
    private Long sprId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="csp_clm_id",nullable=false)
    private ClaimBookings claimBookings;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="csp_spr_id")
    private ServiceProviderDef serviceProvider;

    @Column(name = "csp_created_dt")
    private Date createdDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="csp_created_user")
    private User createdUser;


}
