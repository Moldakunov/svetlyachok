package kg.svetlyachok.controller;

import kg.svetlyachok.config.globalvariable.GlobalVar;
import kg.svetlyachok.config.session.Cookie;
import kg.svetlyachok.model.forms.FeedbackMessage;
import kg.svetlyachok.model.products.SearchText;
import kg.svetlyachok.repository.CookiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static kg.svetlyachok.controller.ProductsController.getCategoriesByStoreId;

@Controller
@RequestMapping("/contact-us")
public class ContactUsController {

    @Autowired
    CookiesRepository cookiesRepository;

    @GetMapping
    public String contactUs(Model model,
                            @CookieValue(name = "JSESSIONID") String givenCookie,
                            HttpServletResponse response,
                            HttpSession session) {
        Cookie.workingWithCookie(cookiesRepository, givenCookie, response, model);

        model.addAttribute("title", GlobalVar.storeName + " - Контакты");
        model.addAttribute("categoryName", getCategoriesByStoreId());
        model.addAttribute("searchForm", new SearchText());
        model.addAttribute("feedbackForm", new FeedbackMessage());
        if (session.getAttribute("userInfo")!=null){
            model.addAttribute("userInSession", "Кабинет");
        }
        if (session.getAttribute("userInfo")==null){
            model.addAttribute("userNotInSession", "Войти");
        }
        return "contact-us";
    }

    @PostMapping
    public String getFeedbackMessage(@ModelAttribute("feedbackForm") FeedbackMessage feedbackMessage,
                                     Model model) {


        System.out.println(feedbackMessage.toString()); //  Планируем отправить полученную форму на почту Илгиза

        model.addAttribute("title", GlobalVar.storeName + " - Контакты");
        model.addAttribute("categoryName", getCategoriesByStoreId());
        model.addAttribute("searchForm", new SearchText());
        return "contact-us";
    }

}
