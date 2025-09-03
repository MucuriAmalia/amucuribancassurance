package com.brokersystems.brokerapp.uw.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="sys_brk_pol_misc_info")
public class PolicyMiscInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "strike_day")
    private String strikeDay;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "bank_id")
    private String bankId;

    @Column(name = "branch_id")
    private String branchId;

    @Column(name = "inflation_percent")
    private Double inflationPercent;

    @Column(name = "product_name")
    private String covername;

    @Column(name = "cover_option")
    private String coverOption;

    @Column(name = "med_quiz_1")
    private String medicalUWQuiz1;

    @Column(name = "quiz_ans_1")
    private String medicalUWQ1; //store  1 True, 2 False

    @Column(name = "med_quiz_2")
    private String medicalUWQuiz2;

    @Column(name = "quiz_ans_2")
    private String medicalUWQ2; //store  1 True, 2 False

    @Column(name = "med_quiz_3")
    private String medicalUWQuiz3;

    @Column(name = "quiz_ans_3")
    private String medicalUWQ3; //store  1 True, 2 False

    @Column(name = "policy_id")
    private Long policyId;
}
