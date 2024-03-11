package dev.pollito.anime.poster.generator.backend.service.impl;

import dev.pollito.anime.poster.generator.backend.exception.InvalidBase64ImageException;
import dev.pollito.anime.poster.generator.backend.models.PosterContent;
import dev.pollito.anime.poster.generator.backend.service.JasperService;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JasperServiceImpl implements JasperService {
  public static final String CLASSPATH_REPORTS_POSTER_JASPER = "classpath:reports/poster.jasper";
  public static final String CLASSPATH_REPORTS_BACKGROUND_JPG = "classpath:reports/background.jpg";
  public static final int TITLE_MAX_LENGTH = 21;
  public static final int GENRES_MAX_SIZE = 3;
  public static final int STUDIOS_MAX_SIZE = 2;
  public static final int YEAR_MAX_LENGTH = 4;
  private final ResourceLoader resourceLoader;

  @Override
  @SneakyThrows
  public byte[] makePoster(PosterContent content) {
    if (!isValidBase64Image(content.getImage())) {
      throw new InvalidBase64ImageException();
    }

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    JasperExportManager.exportReportToPdfStream(
        JasperFillManager.fillReport(
            (JasperReport)
                JRLoader.loadObject(
                    resourceLoader.getResource(CLASSPATH_REPORTS_POSTER_JASPER).getInputStream()),
            mapPosterContentToParameters(content),
            new JREmptyDataSource()),
        byteArrayOutputStream);

    return byteArrayOutputStream.toByteArray();
  }

  private Map<String, Object> mapPosterContentToParameters(PosterContent content)
      throws IOException {
    Map<String, Object> parameters = new HashMap<>();

    String title = content.getTitle().toUpperCase();
    if (title.length() > TITLE_MAX_LENGTH) {
      title = title.substring(0, TITLE_MAX_LENGTH - 1) + "...";
    }
    parameters.put("title", title);
    parameters.put("year", content.getYear().toString().substring(0, YEAR_MAX_LENGTH));
    parameters.put(
        "genres",
        content.getGenres().stream()
            .limit(GENRES_MAX_SIZE)
            .map(String::toUpperCase)
            .collect(Collectors.joining("\t")));
    parameters.put("director", content.getDirector().toUpperCase());
    parameters.put(
        "producers",
        content.getProducers().stream()
            .limit(STUDIOS_MAX_SIZE)
            .map(String::toUpperCase)
            .collect(Collectors.joining("\t")));
    parameters.put("image", getImageFromBase64String(content.getImage()));
    parameters.put(
        "background",
        getBufferedImageFromInputStream(
            resourceLoader.getResource(CLASSPATH_REPORTS_BACKGROUND_JPG).getInputStream()));
    return parameters;
  }

  private static BufferedImage getBufferedImageFromInputStream(InputStream resourceLoader)
      throws IOException {
    return ImageIO.read(resourceLoader);
  }

  private static Image getImageFromBase64String(String imageBytes) throws IOException {
    ByteArrayInputStream bis = new ByteArrayInputStream(Base64.getDecoder().decode(imageBytes));
    Image image = getBufferedImageFromInputStream(bis);
    bis.close();
    return image;
  }

  private static boolean isValidBase64Image(String base64ImageString) {
    byte[] imageBytes;
    try {
      imageBytes = Base64.getDecoder().decode(base64ImageString);
    } catch (IllegalArgumentException e) {
      return false;
    }

    try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
      BufferedImage image = ImageIO.read(bais);
      return Objects.nonNull(image);
    } catch (Exception e) {
      return false;
    }
  }
}
