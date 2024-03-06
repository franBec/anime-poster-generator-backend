package dev.pollito.anime.poster.generator.backend.controller;

import dev.pollito.anime.poster.generator.backend.api.PosterApi;
import dev.pollito.anime.poster.generator.backend.models.PosterContent;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PosterController implements PosterApi {
    @Override
    public ResponseEntity<Resource> makePoster(PosterContent posterContent) {
        return PosterApi.super.makePoster(posterContent);
    }
}
