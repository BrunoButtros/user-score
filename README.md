# User Score

Os três projetos originais reunidos em um único repositório Maven, com Java 17 e Spring Boot 3.5.16.

| Módulo | Origem | Função | Porta local |
| --- | --- | --- | --- |
| `score-api` | UserScoreClient | Serviço de pontuação | 8081 |
| `user-api` | Projeto_User_Score | Cadastro, autenticação JWT, usuários, endereços e relatórios | 8080 |
| `web` | UserScoreJSP | Login e lista de scores em JSP | 8082 |

O navegador acessa `web`, que chama `user-api`. A API principal consulta `score-api`, o banco e o ViaCEP. **O score continua sendo uma simulação aleatória entre 0 e 100**, como no projeto original; não consulta um serviço real de crédito.

## Executar localmente

Requisitos: JDK 17, acesso à internet para baixar dependências e consultar ViaCEP. Node.js 22+ é necessário para os scripts auxiliares. Maven pode ser usado instalado ou pelo wrapper incluído.

```powershell
# Windows PowerShell, na raiz do repositório
.\mvnw.cmd verify
node scripts/start.mjs
```

No Linux: `./mvnw verify`, seguido de `node scripts/start.mjs`.

Abra [http://localhost:8082](http://localhost:8082). Ctrl+C encerra os três serviços. Alternativamente, execute os artefatos `score-api/target/score-api.jar`, `user-api/target/user-api.jar` e `web/target/web.war` com `java -jar` em três terminais.

O perfil padrão `local` usa H2 persistente em `./data/userscore`, sem exigir instalação de banco. A chave JWT padrão desse perfil serve somente para desenvolvimento. O envio de e-mail fica desligado. O MySQL é usado no perfil `mysql`.

## Criar um usuário e entrar

Não há usuário ou senha cadastrados automaticamente. No PowerShell:

```powershell
$cadastro = @{
  login = 'demo'
  password = 'uma-senha-de-teste-123'
  nome = 'Pessoa de Teste'
  email = 'demo@example.test'
  telefone = '11999999999'
  cpf = '12345678901'
  cep = '01001000'
} | ConvertTo-Json
Invoke-RestMethod -Uri 'http://localhost:8080/user/cadastrar' -Method Post -ContentType 'application/json; charset=utf-8' -Body ([Text.Encoding]::UTF8.GetBytes($cadastro))
```

Entre na interface com o login e a senha escolhidos. Use dados fictícios para testar. CPF deve conter 11 dígitos e CEP 8 dígitos; a verificação de CPF é de formato. O cadastro depende de `score-api` e ViaCEP disponíveis.

O cadastro público sempre cria `USER`, mesmo que o cliente envie `role: ADMIN`. Administradores devem ser provisionados por um operador confiável no banco. Por exemplo, após cadastrar a conta administrativa, o operador pode executar `UPDATE users SET role='ADMIN' WHERE login='login-escolhido';` no banco correto. A aplicação não expõe promoção pública de privilégios.

## Docker com MySQL

```powershell
Copy-Item .env.example .env
# Edite .env e substitua as três senhas/chaves por valores aleatórios distintos.
docker compose up --build -d
docker compose ps
```

O Compose cria a rede, o banco e os três serviços. Aguarde o banco ficar saudável e as aplicações iniciarem. As portas ficam vinculadas a `127.0.0.1`. A interface está em [http://localhost:8082](http://localhost:8082). `docker compose down` encerra o conjunto e preserva os volumes do banco e relatórios. `.env`, logs e dados locais não são versionados.

## Configuração

| Variável | Uso |
| --- | --- |
| `SCORE_API_URL` | URL completa do POST `/score`; padrão `http://localhost:8081/score` |
| `USER_SCORE_BASE_URL` | URL da API usada pelo JSP; padrão `http://localhost:8080` |
| `VIACEP_URL` | Base de CEP; padrão `https://viacep.com.br/ws/` |
| `SPRING_PROFILES_ACTIVE` | `local` ou `mysql` |
| `USER_SCORE_JDBC_URL`, `MYSQL_USERNAME`, `MYSQL_PASSWORD` | Conexão MySQL |
| `JWT_SECRET` | Obrigatória fora do perfil local |
| `APP_FILE_PATH` | Relatório; padrão `./data/usuarios_scores.txt` |
| `REPORTS_SCHEDULE` | Intervalo e atraso inicial do relatório, em ms; padrão 500000 |
| `ENVIAR_EMAIL` | Ativa envio periódico aos administradores; padrão `false` |
| `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD` | Servidor SMTP |
| `MAIL_AUTH`, `MAIL_STARTTLS` | Autenticação e STARTTLS; padrão `false` |

O Compose carrega `.env`. Para executar Java/Node diretamente, defina as variáveis no ambiente do terminal. A configuração SMTP e a entrega real de e-mails dependem de um servidor fornecido pelo operador. Use HTTPS ao disponibilizar o sistema fora da máquina local.

## API e verificações

- `POST /user/cadastrar`: cadastro público.
- `POST /auth/login`: retorna `{ "token": "..." }`.
- `GET /user/scores` e `GET /user/{id}/score`: exigem `Authorization: Bearer TOKEN`.
- `GET /user/buscar`, `PUT /user/{id}` e `DELETE /user/{id}`: exigem administrador.
- `GET /cep/{cep}`: consulta autenticada de endereço.
- `GET /swagger-ui/index.html`: documentação da API principal.
- `GET /actuator/health`: saúde de cada serviço.

```powershell
.\mvnw.cmd verify
node scripts/smoke.mjs
node scripts/smoke.mjs --live-cep
```

O primeiro comando executa os testes preservados e os testes de regressão de autenticação/persistência. O smoke inicia os três artefatos reais em portas temporárias, usa banco novo e valida cadastro, autenticação, permissões, renderização JSP, escape de HTML, CSRF, sessão e logout. Por padrão somente o ViaCEP é substituído por um servidor HTTP controlado; `--live-cep` consulta o ViaCEP real. Os processos são encerrados ao final e os caminhos dos logs são informados.

Para repetir o cenário em MySQL, use um banco **vazio dedicado a testes**, configure `MYSQL_USERNAME`, `MYSQL_PASSWORD`, `JWT_SECRET` e `SMOKE_MYSQL_URL`, e execute `node scripts/smoke.mjs --mysql`. A CI executa Maven, o cenário H2 e o cenário MySQL.

Para validar o Compose já iniciado nas portas padrão, execute `node scripts/smoke.mjs --running`. Esse comando usa ViaCEP real e acrescenta dois usuários fictícios ao banco existente.

## Origem e correções

O código-fonte e os testes dos três repositórios foram importados; artefatos compilados, logs históricos e relatórios gerados foram deixados nos repositórios de origem. Os históricos Git anteriores continuam disponíveis neles.

| Repositório | Commit de origem |
| --- | --- |
| [UserScoreClient](https://github.com/BrunoButtros/UserScoreClient) | `1de69a2fe05e85f423f3d8213c514556f163cfd6` |
| [Projeto_User_Score](https://github.com/BrunoButtros/Projeto_User_Score) | `fc19da6f4f977c38338be372a2fd39a5a62de1fe` |
| [UserScoreJSP](https://github.com/BrunoButtros/UserScoreJSP) | `18256774e65f9c975ff3c285d68b94f3bf3d730f` |

As correções incluem dependências Jakarta compatíveis, WAR executável com JSP, URLs configuráveis, autorização administrativa, cadastro sem elevação de privilégio, tratamento de JWT inválido/usuário excluído, expiração independente de fuso, validação de CEP, unicidade de CPF, atualização usando o CPF persistido e relatórios sem dependência de caminho absoluto. O teste de serviço JUnit 4 foi migrado para JUnit 5 para passar a ser executado junto aos demais.

Referências técnicas: [requisitos Spring Boot](https://docs.spring.io/spring-boot/3.5/system-requirements.html) e [suporte a JSP/WAR](https://docs.spring.io/spring-boot/3.5/reference/web/servlet.html).
