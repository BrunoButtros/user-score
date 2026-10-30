package com.github.brunobuttros.userscore.repository;

import com.github.brunobuttros.userscore.entity.EnderecoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnderecoRepository extends JpaRepository<EnderecoEntity, Long> {

    EnderecoEntity findByCep(String cep);
}
