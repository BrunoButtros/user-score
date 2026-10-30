package com.github.brunobuttros.score.api.server.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CpfRequest {

    private String cpf;

    public CpfRequest() {
    }

    public CpfRequest(String cpf) {
        this.cpf = cpf;
    }
}
