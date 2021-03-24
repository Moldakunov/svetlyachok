package kg.svetlyachok.controller;

import kg.svetlyachok.config.globalvariable.GlobalVar;
import kg.svetlyachok.config.session.Cookie;
import kg.svetlyachok.model.product.Product;
import kg.svetlyachok.model.products.SearchText;
import kg.svetlyachok.repository.CookiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/shopping-cart")
public class ShoppingCartController {

    @Autowired
    CookiesRepository cookiesRepository;

    @Autowired
    EntityManager entityManager;

    @GetMapping
    public String shoppingCart(Model model,
                               @CookieValue(value = "JSESSIONID", defaultValue = "") String givenCookie,
                               HttpServletResponse response,
                               HttpSession session) {
        Cookie.workingWithCookie(cookiesRepository, givenCookie, response, model);

        if (!givenCookie.isEmpty() && cookiesRepository.findBySessionId(givenCookie) != null) {
            List<Product> products = ProductsController.getProductsByIds(cookiesRepository.findBySessionId(givenCookie).getProducts());
            model.addAttribute("products", products);
        }

        model.addAttribute("title", GlobalVar.storeName + " - Корзина");
        model.addAttribute("searchForm", new SearchText());
        model.addAttribute("categoryName", ProductsController.getCategoriesByStoreId());
        if (session.getAttribute("userInfo")!=null){
            model.addAttribute("userInSession", "Кабинет");
        }
        if (session.getAttribute("userInfo")==null){
            model.addAttribute("userNotInSession", "Войти");
        }
        return "shopping-cart";
    }
}
