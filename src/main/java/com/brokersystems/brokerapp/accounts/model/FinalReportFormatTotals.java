package com.brokersystems.brokerapp.accounts.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import javax.persistence.*;

@Entity
@Table(name="sys_brk_rpt_format_totals")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FinalReportFormatTotals {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="rft_id")
    @Setter
    private Long rftId;

    @Column(name="rft_sign")
    @Setter
    private Boolean sign;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="rft_total")
    @Setter
    private FinalReportFormats total;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="rft_column")
    @Setter
    private FinalReportFormats column;

}