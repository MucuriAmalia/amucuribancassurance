package com.brokersystems.brokerapp.trans.model;

import com.brokersystems.brokerapp.trans.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_apa_motor_private_api_logs")
public class APAMotorPrivateApiLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Column(name = "response_message", columnDefinition = "TEXT")
    private String responseMessage;

    @Column(name = "success", nullable = false)
    private boolean success;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}

