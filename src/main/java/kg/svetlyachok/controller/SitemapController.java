package kg.svetlyachok.controller;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static kg.svetlyachok.config.globalvariable.GlobalVar.*;

@Controller
public class SitemapController {

  @GetMapping("/sitemap.xml")
  @ResponseBody
  public FileSystemResource getSitemap(HttpServletResponse responseFirst) {
    FileOutputStream fos;

    Path file = Paths.get(SITEMAP);
    BasicFileAttributes attributes;

    try {
      attributes = Files.readAttributes(file, BasicFileAttributes.class);

      FileTime lastModifiedTime = attributes.lastModifiedTime();
      LocalDateTime modifiedTime =
          LocalDateTime.ofInstant(lastModifiedTime.toInstant(), ZoneId.systemDefault());

      LocalDateTime now = LocalDateTime.now();

      if (modifiedTime.plusMinutes(10).isBefore(now)) {
        // Получение сайтмэп
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

        HttpGet request =
            new HttpGet(
                mainURL + "getSiteMap?baseUrl=https://www.svetlyachok.kg/product/&storeId=" + storeId);

        CloseableHttpResponse response = closeableHttpClient.execute(request);
        HttpEntity entity = response.getEntity();

        // Перезаписываем уже существующий файл, т.к. он уже устарел
        // флаг false означает перезапись файла
        fos = new FileOutputStream(SITEMAP, false);
        fos.write(EntityUtils.toByteArray(entity));
        fos.flush();
        fos.close();

        System.out.println("о, 10 минут прошли, ГЕНЕРИМ новый sitemap!!!");
      } else if (modifiedTime.plusMinutes(10).isAfter(now)) {
        System.out.println("еще не прошли 10 минут, так что довольствуйся тем, что есть дорогой");
      }
    } catch (IOException e) {
      System.err.println(e);
    }

    responseFirst.setContentType("application/xml");
    //        return new FileSystemResource(new File("C:\\Users\\Dosmir\\Desktop\\sitemap.xml"));
    return new FileSystemResource(new File(SITEMAP));
  }
}
