package com.example.minibankaccount.model.audit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.Instant;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(
        value = {"dateOfTransaction"},
        allowGetters = true
)
@Getter
@Setter
public class DateOfTransactionAudit implements Serializable {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant dateOfTransaction;

}
