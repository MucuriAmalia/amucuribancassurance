package com.brokersystems.brokerapp.uw.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "sys_brk_pol_dependant_misc_info")
public class PolicyDependentsInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(name = "dep_type")
    private String depType; //efm, parent, child, spouse, main_member

    @Column(name = "dep_full_name")
    private String fullName;

    @Column(name = "dep_birth_date")
    private Date DOB;

    @Column(name = "dep_sum_insured")
    private BigDecimal SumInsured;

    @Column(name = "monthly_prem")
    private BigDecimal MonthlyPremium;

    @Column(name = "anual_prem")
    private BigDecimal AnnualPremium;

    @Column(name = "income_benefit_prem")
    private BigDecimal IncomeBenefitPremium;

    @Column(name = "policy_id")
    private Long policyId;

    @Column(name = "still_born")
    private Boolean stillBorn;

    @Column(name = "is_student")
    private Boolean isStudent;

    @Column(name = "identification_type")
    private String identificationType;

    @Column(name = "identification_no")
    private String identificationNumber;

    @Column(name = "gender")
    private String gender;
}
