# Validação da integração

Verificação local em 29/10/2026, Windows 11 e Java 17.

| Verificação | Resultado |
| --- | --- |
| Compilação Maven dos três módulos e empacotamento JAR/WAR | PASS |
| Testes JUnit: score-api 1, user-api 21, web 1 | 23 passaram; nenhuma falha ou teste ignorado |
| Fluxo funcional com três aplicações reais, H2 novo e ViaCEP HTTP controlado | PASS |
| Construção e inicialização das três imagens Docker e MySQL 8.4 | PASS |
| Fluxo funcional nas imagens Docker, com MySQL e ViaCEP real | PASS |
| Maven Wrapper 3.9.7 no Windows | PASS |

O fluxo funcional verifica cadastro, CEP e score, login correto/incorreto, token inválido, bloqueio de promoção pública para ADMIN, acesso sem permissão, renderização real das JSPs, escape de apóstrofos e HTML, proteção CSRF, rotação da sessão e logout. Os testes de persistência também verificam atualização administrativa sem CPF no payload, token de conta removida e endereços independentes para usuários com CEP compartilhado.

Houve revisão independente das alterações de autenticação, integridade, JSP e Docker. Os problemas encontrados foram corrigidos antes da aceitação local. O envio real de e-mail não foi testado: permanece desativado e depende de configuração SMTP do operador. Não foi executado um scanner de vulnerabilidades.

O workflow CI do GitHub repete os testes e os cenários H2/MySQL em Ubuntu. Seu estado para cada commit está disponível na aba Actions do repositório; os resultados locais acima não substituem essa execução remota.
