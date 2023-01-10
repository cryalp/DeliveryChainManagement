package com.cry.DeliveryChain.Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.cry.DeliveryChain.Core.Functions;
import com.cry.DeliveryChain.Entity.Bill;
import com.cry.DeliveryChain.Entity.Product;
import com.cry.DeliveryChain.Repository.BillRepo;
import com.cry.DeliveryChain.Repository.ProductRepo;
import com.cry.DeliveryChain.Repository.UserAccountRepo;

@Controller
@RequestMapping(value = "/")
public class RetailerController {
    ProductRepo productRepo;
    BillRepo billRepo;
    UserAccountRepo userAccountRepo;
    LoginController loginController;
    Functions _functions;

    public RetailerController(ProductRepo ProductRepo, BillRepo BillRepo, UserAccountRepo UserAccountRepo, LoginController LoginController, Functions Functions) {
        this.productRepo = ProductRepo;
        this.billRepo = BillRepo;
        this.userAccountRepo = UserAccountRepo;
        this.loginController = LoginController;
        this._functions = Functions;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String Index(HttpSession httpSession, Model model, HttpServletRequest httpRequest) {
        try {
            if (!loginController.IsLoggedIn(httpSession)) {
                return "redirect:/Login/";
            }

            var notification = httpRequest.getParameter("Notification");
            if (notification != null && notification.length() > 64) {
                notification = notification.substring(0, 64);
            }
            model.addAttribute("Notification", notification);

            var productList = productRepo.findAllByIsActive(true);

            model.addAttribute("title", "Ürünler");
            model.addAttribute("productList", productList);
            return "Retailer/Index";
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "redirect:/Login/";
        }
    }


    @RequestMapping(value = "Retailer/CheckOut", method = RequestMethod.GET)
    public String RetailerCheckOut(HttpSession httpSession, Model model, HttpServletRequest httpRequest) {
        try {
            if (!loginController.IsLoggedIn(httpSession)) {
                return "redirect:/Login/";
            }

            var cartProductList = new JSONArray(httpRequest.getParameter("CartProductList"));

            var productList = new ArrayList<Product>();
            cartProductList.forEach(cartProduct -> {
                var product = productRepo.findByUniqueId(UUID.fromString(new JSONObject(cartProduct).getString("UniqueId")));
                if (product.IsActive) {
                    productList.add(product);
                }
            });

            model.addAttribute("ProductList", productList);

            return "/Retailer/Checkout";
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "redirect:/Login/";
        }
    }

    @RequestMapping(value = "Retailer/CheckOut", method = RequestMethod.POST)
    @ResponseBody
    public String RetailerCheckOutPOST(HttpSession httpSession, HttpServletRequest httpRequest) {
        try {
            if (!loginController.IsLoggedIn(httpSession)) {
                return "redirect:/Login/";
            }

            UUID productUniqueId = UUID.fromString(httpRequest.getParameter("UniqueId"));
            int productQuantity = Integer.parseInt(httpRequest.getParameter("Quantity"));

            var product = productRepo.findByUniqueId(productUniqueId);
            var currentPrice = product.Price;

            var supplierUserAccount = userAccountRepo.findById(product.UserAccount.Id);

            var buyerUserAccount = userAccountRepo.findByUniqueId(loginController.GetSessionUserUniqueId(httpSession));

            var bill = new Bill(supplierUserAccount, buyerUserAccount, product, productQuantity, currentPrice, LocalDateTime.now(), product.UniqueId);
            billRepo.save(bill);

            return bill.UniqueId.toString();
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "Satın alma sırasında hata ile karşılaşıldı.";
        }
    }
}
