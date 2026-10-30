package com.github.brunobuttros.userscore.entity;

import com.github.brunobuttros.userscore.dto.UsuarioDTO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table(name = "users")
@Entity(name = "UsersJPA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UsuarioEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String login;

    private String password;
    private String nome;
    private String email;
    private String telefone;
    @Column(unique = true)
    private String cpf;
    private int score;
    private String cep;

    @Enumerated(EnumType.STRING)
    private UsuarioRole role;

    @OneToOne(cascade = CascadeType.ALL)
    private EnderecoEntity endereco;


    public UsuarioEntity(String login, String password, UsuarioRole role) {
        this.login = login;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UsuarioRole.ADMIN) return
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("ROLE_USER"));
        else return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public UsuarioDTO convertEntityToDTO() {
        return new UsuarioDTO(
                this.id,
                this.nome,
                this.email,
                this.telefone,
                this.cpf,
                this.cep,
                this.score,
               this.endereco != null ?this.endereco.toDTO() : null
        );
    }
}
