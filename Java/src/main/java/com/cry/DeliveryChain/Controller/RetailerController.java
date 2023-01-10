package com.cry.DeliveryChain.Controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.cry.DeliveryChain.Core.Functions;
import com.cry.DeliveryChain.Entity.Bill;
import com.cry.DeliveryChain.Entity.BillProduct;
import com.cry.DeliveryChain.Entity.Product;
import com.cry.DeliveryChain.Repository.BillProductRepo;
import com.cry.DeliveryChain.Repository.BillRepo;
import com.cry.DeliveryChain.Repository.ProductRepo;
import com.cry.DeliveryChain.Repository.UserAccountRepo;

@Controller
@RequestMapping(value = "/")
public class RetailerController {
    ProductRepo productRepo;
    BillRepo billRepo;
    BillProductRepo billProductRepo;
    UserAccountRepo userAccountRepo;
    LoginController loginController;
    Functions _functions;

    public RetailerController(ProductRepo ProductRepo, BillRepo BillRepo, BillProductRepo BillProductRepo, UserAccountRepo UserAccountRepo,
            LoginController LoginController, Functions Functions) {
        this.productRepo = ProductRepo;
        this.billRepo = BillRepo;
        this.billProductRepo = BillProductRepo;
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
                var jsonCartProduct = (JSONObject) cartProduct;
                var product = productRepo.findByUniqueId(UUID.fromString(jsonCartProduct.getString("UniqueId")));
                if (product.IsActive) {
                    product.Quantity = jsonCartProduct.getInt("Quantity");
                    productList.add(product);
                }
            });

            model.addAttribute("title", "Sepet - Satın Al");
            model.addAttribute("productList", productList);

            return "/Retailer/CheckOut";
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "redirect:/";
        }
    }

    @RequestMapping(value = "Retailer/CheckOutPOST", method = RequestMethod.POST)
    @ResponseBody
    public String RetailerCheckOutPOST(HttpSession httpSession, HttpServletRequest httpRequest) {
        try {
            if (!loginController.IsLoggedIn(httpSession)) {
                return "redirect:/Login/";
            }

            var buyerUserAccount = userAccountRepo.findByUniqueId(loginController.GetSessionUserUniqueId(httpSession));

            var cartProductList = new JSONArray(httpRequest.getParameter("CartProductList"));

            var bill = new Bill(buyerUserAccount, BigDecimal.valueOf(0), LocalDateTime.now(), UUID.randomUUID());
            billRepo.save(bill);

            cartProductList.forEach(cartProduct -> {
                var jsonCartProduct = (JSONObject) cartProduct;
                var product = productRepo.findByUniqueId(UUID.fromString(jsonCartProduct.getString("UniqueId")));
                if (product.IsActive) {
                    product.Quantity = jsonCartProduct.getInt("Quantity");
                    var billProduct = new BillProduct(bill, product, product.Quantity, product.Price);
                    bill.TotalPrice = bill.TotalPrice.add(product.Price.multiply(BigDecimal.valueOf(product.Quantity)));
                    billProductRepo.save(billProduct);
                }
            });

            billRepo.save(bill);

            return bill.UniqueId.toString();
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "Satın alma sırasında hata ile karşılaşıldı.";
        }
    }
}
