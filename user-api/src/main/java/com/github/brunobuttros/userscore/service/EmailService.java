package com.github.brunobuttros.userscore.service;

import com.github.brunobuttros.userscore.config.EmailConfiguration;
import com.github.brunobuttros.userscore.entity.UsuarioEntity;
import com.github.brunobuttros.userscore.exceptions.EmailSendingException;
import com.github.brunobuttros.userscore.repository.UsuarioRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private EmailConfiguration emailConfiguration;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public void enviarDocumentoParaAdmins(String caminhoDoDocumento) {
        List<UsuarioEntity> admins = usuarioRepository.findByRole("ADMIN");

        for (UsuarioEntity admin : admins) {
            enviarEmail(admin.getEmail(), "Relatório", "ID e Score de cada usuário cadastrado.", caminhoDoDocumento);
        }
    }

    private void enviarEmail(String emailDestino, String assunto, String texto, String caminhoDoAnexo) {
        MimeMessage mensagem = emailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true);
            helper.setFrom(emailConfiguration.getUsername());
            helper.setTo(emailDestino);
            helper.setSubject(assunto);
            helper.setText(texto);

            File anexo = new File(caminhoDoAnexo);
            helper.addAttachment("usuario_score.txt", anexo);

            emailSender.send(mensagem);
            logger.info("E-mail enviado para {}: {}", emailDestino, assunto);
        } catch (MessagingException e) {
            logger.error("Erro ao enviar e-mail para {}", emailDestino, e);
            throw new EmailSendingException("Erro ao enviar e-mail: " + e.getMessage(), e);
        }
    }
}
