package kg.svetlyachok;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

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
import static kg.svetlyachok.config.globalvariable.GlobalVar.SITEMAP;

public class Tes {
  public static void main(String[] args) {
    /*try {
        // Возьмите файл
        File f = new File("C:\\Users\\Dosmir\\Desktop\\sitemap.xml");
        //Создайте новый файл
        // Убедитесь, что он не существует
        if (f.createNewFile())
            System.out.println("File created");
        else
            System.out.println("File already exists");

        //Получение сайтмэп
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://web.imd.kg:8080/genSiteMap?baseUrl=https://www.stores.kg/product/"))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        //Запись в файл
        //String fileData = "Досмир красавчик";
        FileOutputStream fos = new FileOutputStream("C:\\Users\\Dosmir\\Desktop\\sitemap.xml");
        fos.write(response.body().getBytes());
        fos.flush();
        fos.close();
    }
    catch (Exception e) {
        System.err.println(e);
    }*/
    Path file = Paths.get(SITEMAP);
    BasicFileAttributes attributes;

    try {
      // обязательная проверка на существование файла, без него раньше сыпались ошибки
      if (!file.toFile().exists()) {
        generateSitemap();
      } else {
        // получаем атрибуты файла (дата изменения, дата последнего доступа к файлу и т.д.)
        attributes = Files.readAttributes(file, BasicFileAttributes.class);

        // но нас интересует только дата изменения файла
        FileTime lastModifiedTime = attributes.lastModifiedTime();

        // получаем читабельную и понятную нам дату изменения
        LocalDateTime modifiedTime =
            LocalDateTime.ofInstant(lastModifiedTime.toInstant(), ZoneId.systemDefault());

        // нужно еще текущее время для дальнейшего сравнивания с временем выше
        LocalDateTime now = LocalDateTime.now();

        // логика для обновления файла каждые 10 минут
        if (modifiedTime.plusMinutes(10).isBefore(now)) {
          generateSitemap();

          System.out.println("о, 10 минут прошли, ГЕНЕРИМ новый sitemap!!!");
        } else if (modifiedTime.plusMinutes(10).isAfter(now)) {
          System.out.println("еще не прошли 10 минут, так что довольствуйся тем, что есть дорогой");
        }
      }
    } catch (IOException e) {
      System.err.println(e);
    }
  }

  // получаем от сервера уже подготовленный под наши требования сайтмэп
  // и создаем (в случае, если его еще генерили)
  // или перезаписываем его новыми данными
  public static void generateSitemap() throws IOException {
    FileOutputStream fos;
    CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

    HttpGet request =
        new HttpGet(mainURL + "getSiteMap?baseUrl=https://www.emin.kg/product/&storeId=" + storeId);

    CloseableHttpResponse response = closeableHttpClient.execute(request);
    HttpEntity entity = response.getEntity();

    // Перезаписываем уже существующий файл, т.к. он уже устарел или создаем новый файл
    // флаг false означает перезапись файла
    fos = new FileOutputStream(SITEMAP, false);
    fos.write(EntityUtils.toByteArray(entity));
    fos.flush();
    fos.close();
  }
}
