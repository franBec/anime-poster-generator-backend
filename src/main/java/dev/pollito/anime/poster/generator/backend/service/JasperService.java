package dev.pollito.anime.poster.generator.backend.service;

import dev.pollito.anime.poster.generator.backend.models.PosterContent;

public interface JasperService {
  byte[] makePoster(PosterContent posterContent);
}
