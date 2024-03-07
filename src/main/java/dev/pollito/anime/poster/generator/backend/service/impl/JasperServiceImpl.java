package dev.pollito.anime.poster.generator.backend.service.impl;

import dev.pollito.anime.poster.generator.backend.models.PosterContent;
import dev.pollito.anime.poster.generator.backend.service.JasperService;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
  private final ResourceLoader resourceLoader;

  @Override
  @SneakyThrows
  public byte[] makePoster(PosterContent content) {

    Map<String, Object> parameters = new HashMap<>();
    parameters.put("title", content.getTitle());
    parameters.put("year", content.getYear().toString());
    parameters.put("genres", String.join(", ", content.getGenres()));
    parameters.put("studios", String.join(", ", content.getStudios()));
    parameters.put("image", getImageFromBase64String(content.getImage()));

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    JasperExportManager.exportReportToPdfStream(
        JasperFillManager.fillReport(
            (JasperReport)
                JRLoader.loadObject(
                    resourceLoader.getResource(CLASSPATH_REPORTS_POSTER_JASPER).getInputStream()),
            parameters,
            new JREmptyDataSource()),
        byteArrayOutputStream);

    return byteArrayOutputStream.toByteArray();
  }

  private Image getImageFromBase64String(String imageBytes) throws IOException {
    ByteArrayInputStream bis = new ByteArrayInputStream(Base64.getDecoder().decode(imageBytes));
    Image image = ImageIO.read(bis);
    bis.close();
    return image;
  }
}
