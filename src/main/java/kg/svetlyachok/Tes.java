package kg.svetlyachok;

public class Tes {
    public static void main(String args[]) {
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
            *//*System.out.println(response.body());*//*

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
    }
}
