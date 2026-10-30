package com.github.brunobuttros.userscore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.brunobuttros.userscore.entity.UsuarioEntity;
import com.github.brunobuttros.userscore.entity.UsuarioRole;
import com.github.brunobuttros.userscore.repository.UsuarioRepository;
import com.github.brunobuttros.userscore.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties={"spring.profiles.active=local", "spring.datasource.url=jdbc:h2:mem:journey;MODE=MySQL;DB_CLOSE_DELAY=-1", "reports.schedule=3600000", "score.api.url=http://score.test/score", "viacep.url=http://cep.test/"})
@AutoConfigureMockMvc
class UserJourneyTest {
 @Autowired MockMvc mvc;
 @Autowired ObjectMapper json;
 @Autowired UsuarioRepository users;
 @Autowired TokenService tokens;
 @Autowired RestTemplate rest;
 MockRestServiceServer remote;
 @BeforeEach void setup(){users.deleteAll();remote=MockRestServiceServer.bindTo(rest).ignoreExpectOrder(true).build();}
 String data(String login,String cpf){return """
 {"login":"%s","password":"password123","email":"test@example.test","nome":"Teste","cpf":"%s","telefone":"11999999999","cep":"01001000","role":"ADMIN"}
 """.formatted(login,cpf);}
 void expectRegistration(){remote.expect(requestTo("http://score.test/score")).andRespond(withSuccess("{\"score\":78}",MediaType.APPLICATION_JSON));remote.expect(requestTo("http://cep.test/01001000/json/")).andRespond(withSuccess("{\"cep\":\"01001-000\",\"localidade\":\"São Paulo\",\"uf\":\"SP\",\"ibge\":\"3550308\"}",MediaType.APPLICATION_JSON));}
 UsuarioEntity register(String login,String cpf) throws Exception {expectRegistration();mvc.perform(post("/user/cadastrar").contentType(MediaType.APPLICATION_JSON).content(data(login,cpf))).andExpect(status().isCreated());return (UsuarioEntity)users.findByLogin(login);}
 @Test void registrationAuthenticationAndAuthorization() throws Exception {
  UsuarioEntity user=register("user","12345678901");assertThat(user.getRole()).isEqualTo(UsuarioRole.USER);assertThat(user.getPassword()).doesNotContain("password123");
  String auth=mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"login\":\"user\",\"password\":\"password123\"}")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
  String token=json.readTree(auth).get("token").asText();
  mvc.perform(get("/user/scores").header("Authorization","Bearer "+token)).andExpect(status().isOk()).andExpect(jsonPath("$[0].score").value(78));
  mvc.perform(delete("/user/"+user.getId()).header("Authorization","Bearer "+token)).andExpect(status().isForbidden());
  mvc.perform(put("/user/"+user.getId()).header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"other@example.test\"}")).andExpect(status().isForbidden());
  user.setRole(UsuarioRole.ADMIN);users.save(user);String admin=tokens.generateToken(user);
  remote.reset();remote.expect(requestTo("http://score.test/score")).andExpect(org.springframework.test.web.client.match.MockRestRequestMatchers.content().json("{\"cpf\":\"12345678901\"}")).andRespond(withSuccess("{\"score\":88}",MediaType.APPLICATION_JSON));
  mvc.perform(put("/user/"+user.getId()).header("Authorization","Bearer "+admin).contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"other@example.test\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.score").value(88));
  remote.verify();
  mvc.perform(delete("/user/"+user.getId()).header("Authorization","Bearer "+admin)).andExpect(status().isNoContent());
  mvc.perform(get("/user/scores").header("Authorization","Bearer "+admin)).andExpect(status().isUnauthorized());
 }
 @Test void invalidCredentialsAndInput() throws Exception {
  mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"login\":\"missing\",\"password\":\"bad\"}")).andExpect(status().isUnauthorized());
  mvc.perform(post("/user/cadastrar").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest());
  mvc.perform(get("/user/scores").header("Authorization","Bearer invalid")).andExpect(status().isUnauthorized());
 }
 @Test void samePostalCodeCreatesIndependentAddresses() throws Exception {
  UsuarioEntity one=register("one","12345678901");remote.reset();UsuarioEntity two=register("two","12345678902");
  assertThat(one.getEndereco().getId()).isNotEqualTo(two.getEndereco().getId());assertThat(users.count()).isEqualTo(2);
  remote.verify();
 }
 @Test void invalidCepDoesNotPersistUser() throws Exception {
  remote.expect(requestTo("http://score.test/score")).andRespond(withSuccess("{\"score\":78}",MediaType.APPLICATION_JSON));
  remote.expect(requestTo("http://cep.test/01001000/json/")).andRespond(withSuccess("{\"erro\":true}",MediaType.APPLICATION_JSON));
  mvc.perform(post("/user/cadastrar").contentType(MediaType.APPLICATION_JSON).content(data("bad","12345678901"))).andExpect(status().isBadRequest());assertThat(users.count()).isZero();remote.verify();
 }
 @Test void updatingToSharedPostalCodeCreatesNewAddress() throws Exception {
  UsuarioEntity one=register("one","12345678901");remote.reset();UsuarioEntity two=register("two","12345678902");
  two.setCep("02000000");two.setRole(UsuarioRole.ADMIN);users.save(two);remote.reset();expectRegistration();
  mvc.perform(put("/user/"+two.getId()).header("Authorization","Bearer "+tokens.generateToken(two)).contentType(MediaType.APPLICATION_JSON).content("{\"cep\":\"01001000\"}"))
    .andExpect(status().isOk()).andExpect(jsonPath("$.cep").value("01001000"));
  assertThat(users.findById(two.getId()).orElseThrow().getEndereco().getId()).isNotEqualTo(one.getEndereco().getId());remote.verify();
 }
}
