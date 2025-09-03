package com.brokersystems.brokerapp.users.repository;

import com.brokersystems.brokerapp.users.model.PasswordResetToken;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by HP on 4/26/2018.
 */
public interface PasswordResetTokenRepo extends PagingAndSortingRepository<PasswordResetToken, Long>, QueryDslPredicateExecutor<PasswordResetToken> {
}
