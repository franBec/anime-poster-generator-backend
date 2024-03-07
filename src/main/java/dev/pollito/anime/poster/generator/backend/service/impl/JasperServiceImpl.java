package dev.pollito.anime.poster.generator.backend.service.impl;

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
  public static final int TITLE_MAX_LENGTH = 20;
  private final ResourceLoader resourceLoader;

  @Override
  @SneakyThrows
  public byte[] makePoster(PosterContent content) {

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

    String title = content.getTitle();
    if (title.length() > TITLE_MAX_LENGTH) {
      title = title.substring(0, TITLE_MAX_LENGTH - 1) + "...";
    }

    parameters.put("title", title);
    parameters.put("year", content.getYear().toString());
    parameters.put("genres", String.join(", ", content.getGenres()));
    parameters.put("studios", String.join(", ", content.getStudios()));
    parameters.put("image", getImageFromBase64String(content.getImage()));
    parameters.put(
        "background",
        getBufferedImageFromInputStream(
            resourceLoader.getResource(CLASSPATH_REPORTS_BACKGROUND_JPG).getInputStream()));
    return parameters;
  }

  private BufferedImage getBufferedImageFromInputStream(InputStream resourceLoader)
      throws IOException {
    return ImageIO.read(resourceLoader);
  }

  private Image getImageFromBase64String(String imageBytes) throws IOException {
    ByteArrayInputStream bis = new ByteArrayInputStream(Base64.getDecoder().decode(imageBytes));
    Image image = getBufferedImageFromInputStream(bis);
    bis.close();
    return image;
  }
}
