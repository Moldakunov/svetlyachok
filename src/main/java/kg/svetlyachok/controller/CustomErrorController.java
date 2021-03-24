package kg.svetlyachok.controller;

import kg.svetlyachok.config.globalvariable.GlobalVar;
import kg.svetlyachok.config.session.Cookie;
import kg.svetlyachok.model.products.SearchText;
import kg.svetlyachok.repository.CookiesRepository;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class CustomErrorController implements ErrorController {

    final
    CookiesRepository cookiesRepository;

    public CustomErrorController(CookiesRepository cookiesRepository) {
        this.cookiesRepository = cookiesRepository;
    }

    @RequestMapping("/error")
    public String handleError(Model model,
                              HttpServletRequest request,
                              HttpServletResponse response,
                              @CookieValue(name = "JSESSIONID") String givenCookie) {
        Cookie.workingWithCookie(cookiesRepository, givenCookie, response, model);

        model.addAttribute("title", GlobalVar.storeName);
        model.addAttribute("name", "name");
        model.addAttribute("searchForm", new SearchText());

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "500";
            }
        }
        return "index";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
