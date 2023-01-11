package com.cry.DeliveryChain.Controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.cry.DeliveryChain.Core.Functions;
import com.cry.DeliveryChain.Entity.BillProduct;
import com.cry.DeliveryChain.Entity.Product;

@Controller
@RequestMapping(value = "/Supplier")
public class SupplierController {
    RESTService restService;
    LoginController loginController;
    Functions _functions;

    @Value("${App.ProductBase64Photo}")
    private String productBase64Photo;

    public SupplierController(RESTService RESTService, LoginController LoginController, Functions Functions) {
        this.restService = RESTService;
        this.loginController = LoginController;
        this._functions = Functions;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String Index(HttpSession httpSession, Model model) {
        try {
            if (!loginController.IsLoggedIn(httpSession)) {
                return "redirect:/Login/";
            }

            var userAccount = restService.FindUserAccountByUniqueId(loginController.GetSessionUserUniqueId(httpSession));

            var productList = restService.FindAllProductsBySupplierUniqueId(userAccount.UniqueId.toString());

            model.addAttribute("title", "Sipariş Yönet");
            model.addAttribute("productBase64Photo", productBase64Photo);
            model.addAttribute("productList", productList);
            return "Supplier/Index";
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "redirect:/Login/";
        }
    }

    @RequestMapping(value = "/Add", method = RequestMethod.POST)
    @ResponseBody
    public String Add(HttpSession httpSession, HttpServletRequest httpRequest) {
        try {
            if (!loginController.IsLoggedIn(httpSession)) {
                return "NotLoggedIn";
            }

            var header = httpRequest.getParameter("Header");
            var description = httpRequest.getParameter("Description");
            var price = httpRequest.getParameter("Price");
            var quantity = httpRequest.getParameter("Quantity");
            var additionDate = httpRequest.getParameter("AdditionDate");
            var photo = ((MultipartHttpServletRequest) httpRequest).getFile("Photo");

            if (photo != null) {
                productBase64Photo = "data:" + photo.getContentType() + ";base64," + new String(Base64.getEncoder().encode(photo.getBytes()));
            }

            var userAccount = restService.FindUserAccountByUniqueId(loginController.GetSessionUserUniqueId(httpSession));

            var currentAdditionDate = LocalDateTime.now().isBefore(LocalDateTime.parse(additionDate)) ? LocalDateTime.now() : LocalDateTime.parse(additionDate);

            var product = new Product(userAccount, header, description, new BigDecimal(price), Integer.parseInt(quantity), currentAdditionDate, productBase64Photo, true,
                    UUID.randomUUID());
            restService.SaveProduct(product);

            return product.UniqueId.toString();
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "Error";
        }
    }

    @RequestMapping(value = "/Edit", method = RequestMethod.POST)
    @ResponseBody
    public Boolean Edit(HttpSession httpSession, HttpServletRequest httpRequest) {
        try {
            if (!loginController.IsLoggedIn(httpSession)) {
                return false;
            }

            var header = httpRequest.getParameter("Header");
            var description = httpRequest.getParameter("Description");
            var price = httpRequest.getParameter("Price");
            var quantity = httpRequest.getParameter("Quantity");
            var additionDate = httpRequest.getParameter("AdditionDate");
            var photo = ((MultipartHttpServletRequest) httpRequest).getFile("Photo");
            var isActive = httpRequest.getParameter("IsActive");
            var uniqueId = httpRequest.getParameter("UniqueId");

            var product = restService.FindProductByUniqueId(uniqueId);
            if (product == null) {
                return false;
            }

            product.Header = header;
            product.Description = description;
            product.Price = new BigDecimal(price);
            product.Quantity = Integer.parseInt(quantity);
            product.AdditionDate = LocalDateTime.parse(additionDate);
            if (photo != null) {
                product.Photo = "data:" + photo.getContentType() + ";base64," + new String(Base64.getEncoder().encode(photo.getBytes()));
            } else {
                product.Photo = productBase64Photo;
            }
            product.IsActive = Boolean.parseBoolean(isActive);

            restService.SaveProduct(product);

            return true;
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return false;
        }
    }

    @RequestMapping(value = "/Delete", method = RequestMethod.POST)
    @ResponseBody
    public boolean Delete(HttpSession httpSession, HttpServletRequest httpRequest) {
        try {
            if (!loginController.IsLoggedIn(httpSession)) {
                return false;
            }

            var uniqueId = httpRequest.getParameter("UniqueId");

            var product = restService.FindProductByUniqueId(uniqueId);
            if (product != null) {
                restService.DeleteProduct(product);
                return true;
            }
            return false;
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return false;
        }
    }

    @RequestMapping(value = "/Orders", method = RequestMethod.GET)
    public String SupplierOrders(HttpSession httpSession, Model model) {
        try {
            if (!loginController.IsLoggedIn(httpSession)) {
                return "redirect:/Login/";
            }

            var userAccount = restService.FindUserAccountByUniqueId(loginController.GetSessionUserUniqueId(httpSession));

            var billProductList = restService.FindAllBillProductsBySupplierUniqueId(httpSession, userAccount.UniqueId.toString());

            MultiValueMap<String, BillProduct> billMapList = new LinkedMultiValueMap<String, BillProduct>();
            MultiValueMap<String, BillProduct> unApprovedBillMapList = new LinkedMultiValueMap<String, BillProduct>();
            for (BillProduct billProduct : billProductList) {
                if (billProduct.Bill.IsApproved) {
                    billMapList.add(billProduct.Bill.UniqueId.toString(), billProduct);
                } else {
                    unApprovedBillMapList.add(billProduct.Bill.UniqueId.toString(), billProduct);
                }
            }

            model.addAttribute("title", "Sipariş Listele");
            model.addAttribute("productBase64Photo", productBase64Photo);
            model.addAttribute("billMapList", billMapList);
            model.addAttribute("unApprovedBillMapList", unApprovedBillMapList);

            return "Supplier/Orders";
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "redirect:/Supplier/";
        }
    }

    @RequestMapping(value = "/OrderApprove", method = RequestMethod.POST)
    @ResponseBody
    public String SupplierOrderApprove(HttpSession httpSession, HttpServletRequest httpRequest) {
        try {
            if (!loginController.IsLoggedIn(httpSession)) {
                return "redirect:/Login/";
            }

            var userAccount = restService.FindUserAccountByUniqueId(loginController.GetSessionUserUniqueId(httpSession));

            var billUniqueId = httpRequest.getParameter("UniqueId");

            var bill = restService.FindBillByUniqueId(httpSession, billUniqueId);
            if (bill == null) {
                return "Fatura bulunamadı.";
            }

            var billProductList = restService.FindAllBillProductsByBillUniqueId(httpSession, bill.UniqueId.toString());
            if (billProductList.get(0).Supplier.Id != userAccount.Id) {
                return "Bu faturayı onaylayamazsınız.";
            }

            bill.IsApproved = true;
            restService.SaveBill(bill);

            return bill.UniqueId.toString();
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "Sipariş onaylanırken hata ile karşılaşıldı.";
        }

    }
}
