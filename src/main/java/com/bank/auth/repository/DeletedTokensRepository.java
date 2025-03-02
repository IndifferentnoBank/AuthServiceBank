package com.bank.auth.repository;

import com.bank.auth.model.entity.DeletedTokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeletedTokensRepository extends JpaRepository<DeletedTokens, String> {
}
