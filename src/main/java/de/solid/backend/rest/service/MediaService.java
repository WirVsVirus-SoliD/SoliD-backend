package de.solid.backend.rest.service;

import java.io.IOException;
import java.io.InputStream;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.HttpHeaders;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.springframework.util.StringUtils;
import de.solid.backend.dao.AccountEntity;
import de.solid.backend.dao.MediaEntity;
import de.solid.backend.dao.repository.AccountRepository;
import de.solid.backend.rest.service.exception.RequiredArgumentException;
import de.solid.backend.rest.service.exception.SolidException;

/*
 * provides methods for handling picture upload and download
 * 
 */
@ApplicationScoped
public class MediaService {

  @Inject
  private AccountService accountService;

  @Inject
  private AccountRepository accountRepository;

  @Transactional
  public void persistMedia(MultipartFormDataInput input, String authenticatedUserEmail) {
    if (input.getParts() != null && input.getParts().size() == 1) {
      try {
        AccountEntity entity = this.accountService.findByEmail(authenticatedUserEmail);
        InputPart inputPart = input.getParts().get(0);
        InputStream inputStream = inputPart.getBody(InputStream.class, null);
        byte[] bytes = IOUtils.toByteArray(inputStream);
        MediaEntity mediaEntity = MediaEntity.builder().media(bytes)
            .mediaName(
                getFilename(inputPart.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION)))
            .build();
        entity.setMedia(mediaEntity);
        this.accountRepository.persist(entity);
      } catch (NumberFormatException | IOException mue) {
        throw new SolidException(this.getClass(), "persistMedia",
            String.format("error persisting media for user with email %s", ""));
      }
    } else {
      throw new RequiredArgumentException(this.getClass(), "persistMedia",
          "missing media for persisting");
    }
  }


  @Transactional
  public MediaEntity getMediaEntity(long accountId) {
    AccountEntity entity = this.accountRepository.findById(accountId);
    if (entity != null) {
      return entity.getMedia();
    } else {
      throw new RequiredArgumentException(this.getClass(), "persistMedia",
          "no media found for account with id " + accountId);
    }
  }

  private String getFilename(String header) {
    if (!StringUtils.isEmpty(header)) {
      String[] contentDisposition = header.split(";");
      for (String filename : contentDisposition) {
        if ((filename.trim().startsWith("filename"))) {
          String[] name = filename.split("=");
          if (name.length >= 2) {
            String finalFileName = name[1].trim().replaceAll("\"", "");
            return finalFileName;
          }
        }
      }
    }
    return "file";
  }
}
