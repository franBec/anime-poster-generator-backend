package dev.pollito.anime.poster.generator.backend.controller;

import dev.pollito.anime.poster.generator.backend.api.PosterApi;
import dev.pollito.anime.poster.generator.backend.models.PosterContent;
import dev.pollito.anime.poster.generator.backend.service.JasperService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PosterController implements PosterApi {
  private final JasperService jasperService;

  @Override
  public ResponseEntity<Resource> makePoster(PosterContent posterContent) {
    ByteArrayResource resource = new ByteArrayResource(jasperService.makePoster(posterContent));

    String filename = posterContent.getTitle() + ".pdf";

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
    headers.setContentLength(resource.contentLength());
    headers.setContentType(MediaType.APPLICATION_PDF);

    return ResponseEntity.ok().headers(headers).body(resource);
  }
}
