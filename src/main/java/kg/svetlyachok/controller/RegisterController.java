package kg.svetlyachok.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.svetlyachok.config.globalvariable.GlobalVar;
import kg.svetlyachok.config.session.Cookie;
import kg.svetlyachok.model.UserForRegister;
import kg.svetlyachok.model.products.SearchText;
import kg.svetlyachok.repository.CookiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static kg.svetlyachok.controller.ProductsController.getCategoriesByStoreId;

@Controller
public class RegisterController {

    @Autowired
    CookiesRepository cookiesRepository;

    @Autowired
    EntityManager entityManager;

    @GetMapping("/register")
    public String register(Model model,
                         @CookieValue(name = "JSESSIONID") String givenCookie,
                         HttpServletResponse response, HttpSession session) {
        Cookie.workingWithCookie(cookiesRepository, givenCookie, response, model);

        model.addAttribute("title", GlobalVar.storeName + " - Регистрация");
        model.addAttribute("searchForm", new SearchText());
        model.addAttribute("categoryName", getCategoriesByStoreId());
        if (session.getAttribute("userInfo")!=null){
            model.addAttribute("userInSession", "Кабинет");
        }
        if (session.getAttribute("userInfo")==null){
            model.addAttribute("userNotInSession", "Войти");
        }
        return "register";
    }

    //@PostMapping("/registerUser")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void getUserInfoForRegister(
            @RequestParam("userName") String userName,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("phone") String phone,
            Model model,
            @CookieValue(name = "JSESSIONID") String givenCookie,
            HttpServletResponse response) throws IOException {
        Cookie.workingWithCookie(cookiesRepository, givenCookie, response, model);

        model.addAttribute("title", GlobalVar.storeName + " - Регистрация");
        model.addAttribute("searchForm", new SearchText());
        model.addAttribute("categoryName", getCategoriesByStoreId());

        UserForRegister user = new UserForRegister(userName, email, password, phone, GlobalVar.storeId);
        /*System.out.println(user.toString());*/
        sendJSONtoRegister(user);
    }


    private void sendJSONtoRegister(UserForRegister user) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonUser = mapper.writeValueAsString(user);
        /*System.out.println(jsonUser);*/

        URL url = new URL(GlobalVar.mainURL+"registerUser");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonUser.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            /*System.out.println(response.toString());*/
        }
    }

}
