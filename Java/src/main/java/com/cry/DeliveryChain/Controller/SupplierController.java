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
import com.cry.DeliveryChain.Entity.ProductPhoto;

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
    public String Index(HttpSession httpSession, Model model, HttpServletRequest httpRequest) {
        try {
            if (!loginController.IsLoggedIn(httpSession)) {
                return "redirect:/Login/";
            }

            var userAccount = restService.FindUserAccountByUniqueId(loginController.GetSessionUserUniqueId(httpSession));
            if (!userAccount.AccountType.equals("Supplier")) {
                return "redirect:/";
            }

            var notification = httpRequest.getParameter("Notification");
            if (notification != null && notification.length() > 64) {
                notification = notification.substring(0, 64);
            }
            model.addAttribute("Notification", notification);

            var productList = restService.FindAllProductsBySupplierUniqueId(userAccount.UniqueId.toString());
            for (var product : productList) {
                product.setPhotoList(restService.FindAllProductPhotosByProductUniqueId(product.UniqueId.toString()));
            }

            model.addAttribute("title", "??r??n Y??net");
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

            var userAccount = restService.FindUserAccountByUniqueId(loginController.GetSessionUserUniqueId(httpSession));
            if (!userAccount.AccountType.equals("Supplier")) {
                return "redirect:/";
            }

            var header = httpRequest.getParameter("Header").isEmpty() ? "BO??BA??LIK" : httpRequest.getParameter("Header");
            var description = httpRequest.getParameter("Description").isEmpty() ? "" : httpRequest.getParameter("Description");
            var price = httpRequest.getParameter("Price").isEmpty() ? "0" : httpRequest.getParameter("Price");
            var discount = httpRequest.getParameter("Discount").isEmpty() ? "0" : httpRequest.getParameter("Discount");
            var quantity = httpRequest.getParameter("Quantity").isEmpty() ? "0" : httpRequest.getParameter("Quantity");
            var additionDate = httpRequest.getParameter("AdditionDate").isEmpty() ? LocalDateTime.now().toString() : httpRequest.getParameter("AdditionDate");
            var isActive = httpRequest.getParameter("IsActive").isEmpty() ? "false" : httpRequest.getParameter("IsActive");
            var photoList = ((MultipartHttpServletRequest) httpRequest).getFiles("PhotoList");

            var currentAdditionDate = LocalDateTime.now().isBefore(LocalDateTime.parse(additionDate)) ? LocalDateTime.now() : LocalDateTime.parse(additionDate);

            var product = new Product(userAccount, header, description, new BigDecimal(price), Float.parseFloat(discount), Integer.parseInt(quantity),
                    currentAdditionDate, Boolean.parseBoolean(isActive), UUID.randomUUID());
            restService.SaveProduct(product);

            var productPhoto = new ProductPhoto();
            if (photoList.isEmpty()) {
                productPhoto = new ProductPhoto(product, productBase64Photo, UUID.randomUUID());
                restService.SaveProductPhoto(productPhoto);
            } else {
                for (var photo : photoList) {
                    var photoBase64 = "data:" + photo.getContentType() + ";base64," + new String(Base64.getEncoder().encode(photo.getBytes()));
                    productPhoto = new ProductPhoto(product, photoBase64, UUID.randomUUID());
                    restService.SaveProductPhoto(productPhoto);
                }
            }

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

            var userAccount = restService.FindUserAccountByUniqueId(loginController.GetSessionUserUniqueId(httpSession));
            if (!userAccount.AccountType.equals("Supplier")) {
                return false;
            }

            var uniqueId = httpRequest.getParameter("UniqueId");
            var product = restService.FindProductByUniqueId(uniqueId);
            if (product == null || product.UserAccount.Id != userAccount.Id) {
                return false;
            }

            var header = httpRequest.getParameter("Header").isEmpty() ? "BO??BA??LIK" : httpRequest.getParameter("Header");
            var description = httpRequest.getParameter("Description").isEmpty() ? "" : httpRequest.getParameter("Description");
            var price = httpRequest.getParameter("Price").isEmpty() ? "0" : httpRequest.getParameter("Price");
            var discount = httpRequest.getParameter("Discount").isEmpty() ? "0" : httpRequest.getParameter("Discount");
            var quantity = httpRequest.getParameter("Quantity").isEmpty() ? "0" : httpRequest.getParameter("Quantity");
            var additionDate = httpRequest.getParameter("AdditionDate").isEmpty() ? LocalDateTime.now().toString() : httpRequest.getParameter("AdditionDate");
            var isActive = httpRequest.getParameter("IsActive").isEmpty() ? "false" : httpRequest.getParameter("IsActive");
            var photoList = ((MultipartHttpServletRequest) httpRequest).getFiles("PhotoList");
            var removedPhotoList = httpRequest.getParameterValues("RemovedPhotoList");

            var currentAdditionDate = LocalDateTime.now().isBefore(LocalDateTime.parse(additionDate)) ? LocalDateTime.now() : LocalDateTime.parse(additionDate);

            product.Header = header;
            product.Description = description;
            product.Price = new BigDecimal(price);
            product.Discount = Float.parseFloat(discount);
            product.Quantity = Integer.parseInt(quantity);
            product.AdditionDate = currentAdditionDate;
            product.IsActive = Boolean.parseBoolean(isActive);

            restService.SaveProduct(product);

            for (var photo : photoList) {
                var photoBase64 = "data:" + photo.getContentType() + ";base64," + new String(Base64.getEncoder().encode(photo.getBytes()));
                var productPhoto = new ProductPhoto(product, photoBase64, UUID.randomUUID());
                restService.SaveProductPhoto(productPhoto);
            }

            if (removedPhotoList != null) {
                for (var removedPhotoUniqueId : removedPhotoList) {
                    var removedPhoto = restService.FindProductPhotoByUniqueId(removedPhotoUniqueId);
                    if (removedPhoto != null && removedPhoto.Product.UserAccount.Id == userAccount.Id) {
                        restService.DeleteProductPhoto(removedPhoto);
                    }
                }
            }

            var productPhotoList = restService.FindAllProductPhotosByProductUniqueId(product.UniqueId.toString());
            if (productPhotoList.isEmpty()) {
                var productPhoto = new ProductPhoto(product, productBase64Photo, UUID.randomUUID());
                restService.SaveProductPhoto(productPhoto);
            }

            return true;
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return false;
        }
    }

    @RequestMapping(value = "/Delete", method = RequestMethod.POST)
    @ResponseBody
    public Boolean Delete(HttpSession httpSession, HttpServletRequest httpRequest) {
        try {
            if (!loginController.IsLoggedIn(httpSession)) {
                return false;
            }

            var userAccount = restService.FindUserAccountByUniqueId(loginController.GetSessionUserUniqueId(httpSession));
            if (!userAccount.AccountType.equals("Supplier")) {
                return false;
            }

            var uniqueId = httpRequest.getParameter("UniqueId");
            var product = restService.FindProductByUniqueId(uniqueId);
            if (product == null || product.UserAccount.Id != userAccount.Id) {
                return false;
            }

            var billProductList = restService.FindAllBillProductsByProductUniqueId(httpSession, product.UniqueId.toString());
            if (billProductList.size() > 0) {
                return false;
            }

            var productPhotoList = restService.FindAllProductPhotosByProductUniqueId(product.UniqueId.toString());
            for (var productPhoto : productPhotoList) {
                restService.DeleteProductPhoto(productPhoto);
            }

            if (product != null && restService.DeleteProduct(product)) {
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
    public String SupplierOrders(HttpSession httpSession, Model model, HttpServletRequest httpRequest) {
        try {
            if (!loginController.IsLoggedIn(httpSession)) {
                return "redirect:/Login/";
            }

            var userAccount = restService.FindUserAccountByUniqueId(loginController.GetSessionUserUniqueId(httpSession));
            if (!userAccount.AccountType.equals("Supplier")) {
                return "redirect:/";
            }

            var notification = httpRequest.getParameter("Notification");
            if (notification != null && notification.length() > 64) {
                notification = notification.substring(0, 64);
            }
            model.addAttribute("Notification", notification);

            var billProductList = restService.FindAllBillProductsBySupplierUniqueId(httpSession, userAccount.UniqueId.toString());

            MultiValueMap<String, BillProduct> billMapList = new LinkedMultiValueMap<String, BillProduct>();
            MultiValueMap<String, BillProduct> unApprovedBillMapList = new LinkedMultiValueMap<String, BillProduct>();
            for (var billProduct : billProductList) {
                billProduct.Product.setPhotoList(restService.FindAllProductPhotosByProductUniqueId(billProduct.Product.UniqueId.toString()));

                if (billProduct.Bill.IsApproved) {
                    billMapList.add(billProduct.Bill.UniqueId.toString(), billProduct);
                } else {
                    unApprovedBillMapList.add(billProduct.Bill.UniqueId.toString(), billProduct);
                }
            }

            model.addAttribute("title", "Sipari?? Listele");
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
            if (!userAccount.AccountType.equals("Supplier")) {
                return "redirect:/";
            }

            var billUniqueId = httpRequest.getParameter("UniqueId");
            var bill = restService.FindBillByUniqueId(httpSession, billUniqueId);
            if (bill == null) {
                return "Fatura bulunamad??.";
            }

            var billProductList = restService.FindAllBillProductsByBillUniqueId(httpSession, bill.UniqueId.toString());
            if (billProductList.get(0).Supplier.Id != userAccount.Id) {
                return "Bu faturay?? onaylayamazs??n??z.";
            }

            bill.IsApproved = true;
            restService.SaveBill(bill);

            return bill.UniqueId.toString();
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "Sipari?? onaylan??rken hata ile kar????la????ld??.";
        }
    }

    @RequestMapping(value = "/OrderReject", method = RequestMethod.POST)
    @ResponseBody
    public String SupplierOrderReject(HttpSession httpSession, HttpServletRequest httpRequest) {
        try {
            if (!loginController.IsLoggedIn(httpSession)) {
                return "redirect:/Login/";
            }

            var userAccount = restService.FindUserAccountByUniqueId(loginController.GetSessionUserUniqueId(httpSession));
            if (!userAccount.AccountType.equals("Supplier")) {
                return "redirect:/";
            }

            var billUniqueId = httpRequest.getParameter("UniqueId");
            var bill = restService.FindBillByUniqueId(httpSession, billUniqueId);
            if (bill == null) {
                return "Fatura bulunamad??.";
            }

            var billProductList = restService.FindAllBillProductsByBillUniqueId(httpSession, bill.UniqueId.toString());
            if (billProductList.size() <= 0) {
                return "Faturaya ait ??r??n bulunamad??.";
            }

            if (billProductList.get(0).Supplier.Id != userAccount.Id) {
                return "Bu faturay?? silemezsiniz.";
            }

            if (bill.IsApproved) {
                return "Bu fatura ??nceden onaylanm????. Art??k silinemez.";
            }

            for (var billProduct : billProductList) {
                var product = billProduct.Product;
                product.Quantity += billProduct.Quantity;
                restService.SaveProduct(product);
                restService.DeleteBillProduct(billProduct);
            }

            restService.DeleteBill(bill);

            return bill.UniqueId.toString();
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "Sipari?? onaylan??rken hata ile kar????la????ld??.";
        }
    }
}
