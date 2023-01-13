package com.cry.DeliveryChain.Controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.cry.DeliveryChain.Core.Functions;
import com.cry.DeliveryChain.Entity.Bill;
import com.cry.DeliveryChain.Entity.BillProduct;
import com.cry.DeliveryChain.Entity.Cart;
import com.cry.DeliveryChain.Entity.Product;

@Controller
@RequestMapping(value = "/")
public class RetailerController {
    RESTService restService;
    LoginController loginController;
    Functions _functions;

    public RetailerController(RESTService RESTService, LoginController LoginController, Functions Functions) {
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

            var notification = httpRequest.getParameter("Notification");
            if (notification != null && notification.length() > 64) {
                notification = notification.substring(0, 64);
            }
            model.addAttribute("Notification", notification);

            var productList = restService.FindAllProductsByIsActiveAndQuantity(true, 1);

            for (var product : productList) {
                product.setPhotoList(restService.FindAllProductPhotosByProductUniqueId(product.UniqueId.toString()));
            }

            model.addAttribute("title", "Ürünler");
            model.addAttribute("productList", productList);
            return "Retailer/Index";
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "redirect:/Login/";
        }
    }

    @RequestMapping(value = "/OutOfStock", method = RequestMethod.GET)
    public String OutOfStock(HttpSession httpSession, Model model, HttpServletRequest httpRequest) {
        try {
            if (!loginController.IsLoggedIn(httpSession)) {
                return "redirect:/Login/";
            }

            var notification = httpRequest.getParameter("Notification");
            if (notification != null && notification.length() > 64) {
                notification = notification.substring(0, 64);
            }
            model.addAttribute("Notification", notification);

            var productList = restService.FindAllProductsByLessQuantity(1);
            for (var product : productList) {
                product.setPhotoList(restService.FindAllProductPhotosByProductUniqueId(product.UniqueId.toString()));
            }

            model.addAttribute("title", "Stok Dışı Ürünler");
            model.addAttribute("productList", productList);
            return "Retailer/OutOfStock";
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "redirect:/Login/";
        }
    }

    @RequestMapping(value = "Retailer/AddToCart", method = RequestMethod.POST)
    @ResponseBody
    public String RetailerAddToCart(HttpSession httpSession, HttpServletRequest httpRequest) {
        try {
            if (!loginController.IsLoggedIn(httpSession)) {
                return "redirect:/Login/";
            }

            var buyerUserAccount = restService.FindUserAccountByUniqueId(loginController.GetSessionUserUniqueId(httpSession));

            var productUniqueId = httpRequest.getParameter("UniqueId");
            var productQuantity = Integer.parseInt(httpRequest.getParameter("Quantity"));

            var product = restService.FindProductByUniqueId(productUniqueId);

            if (!product.IsActive) {
                return "Ürün aktif değil.";
            }
            if (product.Quantity < productQuantity) {
                return "İstenilen stok mevcut değil.";
            }

            var cart = restService.FindCartByBuyerUniqueIdAndProductUniqueId(httpSession, buyerUserAccount.UniqueId.toString(), product.UniqueId.toString());
            if (cart == null) {
                cart = new Cart(buyerUserAccount, product, productQuantity, UUID.randomUUID());
                restService.SaveCart(cart);
            } else {
                cart.Quantity = productQuantity;
                restService.SaveCart(cart);
            }

            return cart.UniqueId.toString();
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "Sepete eklerken hata ile karşılaşıldı.";
        }
    }

    @RequestMapping(value = "Retailer/RemoveFromCart", method = RequestMethod.POST)
    @ResponseBody
    public String RetailerRemoveFromCart(HttpSession httpSession, HttpServletRequest httpRequest) {
        try {
            if (!loginController.IsLoggedIn(httpSession)) {
                return "redirect:/Login/";
            }

            var buyerUserAccount = restService.FindUserAccountByUniqueId(loginController.GetSessionUserUniqueId(httpSession));

            var productUniqueId = httpRequest.getParameter("UniqueId");
            var product = restService.FindProductByUniqueId(productUniqueId);

            var cartList = restService.FindAllCartsByBuyerUniqueId(httpSession, buyerUserAccount.UniqueId.toString());
            for (var cart : cartList) {
                if (cart.Product.UniqueId == product.UniqueId) {
                    restService.DeleteCart(cart);
                    return cart.UniqueId.toString();
                }
            }

            return "Ürün sepette değil.";
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "Sepetten çıkarırken hata ile karşılaşıldı.";
        }
    }

    @RequestMapping(value = "Retailer/CheckOut", method = RequestMethod.GET)
    public String RetailerCheckOut(HttpSession httpSession, HttpServletRequest httpRequest, Model model) {
        try {
            if (!loginController.IsLoggedIn(httpSession)) {
                return "redirect:/Login/";
            }

            var notification = httpRequest.getParameter("Notification");
            if (notification != null && notification.length() > 64) {
                notification = notification.substring(0, 64);
            }
            model.addAttribute("Notification", notification);

            var buyerUserAccount = restService.FindUserAccountByUniqueId(loginController.GetSessionUserUniqueId(httpSession));
            var cartProductList = restService.FindAllCartsByBuyerUniqueId(httpSession, buyerUserAccount.UniqueId.toString());

            var productList = new ArrayList<Product>();
            var inActiveProductList = new ArrayList<Product>();

            var cartTotalPriceObject = new Object() {
                BigDecimal totalPrice = BigDecimal.ZERO;
            };

            for (var cartProduct : cartProductList) {
                var product = restService.FindProductByUniqueId(cartProduct.Product.UniqueId.toString());
                product.setPhotoList(restService.FindAllProductPhotosByProductUniqueId(product.UniqueId.toString()));

                if (product.IsActive && product.Quantity > 0 && product.Quantity >= cartProduct.Quantity) {
                    product.Quantity = cartProduct.Quantity;
                    productList.add(product);

                    cartTotalPriceObject.totalPrice = cartTotalPriceObject.totalPrice.add(product.Price.multiply(BigDecimal.valueOf(product.Quantity)));
                } else {
                    inActiveProductList.add(product);
                }
            }

            model.addAttribute("title", "Sepet - Satın Al");
            model.addAttribute("productList", productList);
            model.addAttribute("inActiveProductList", inActiveProductList);
            model.addAttribute("cartTotalPrice", cartTotalPriceObject.totalPrice);

            return "Retailer/CheckOut";
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "redirect:/";
        }
    }

    @RequestMapping(value = "Retailer/CheckOutPOST", method = RequestMethod.POST)
    @ResponseBody
    public String RetailerCheckOutPOST(HttpSession httpSession) {
        try {
            if (!loginController.IsLoggedIn(httpSession)) {
                return "redirect:/Login/";
            }

            var buyerUserAccount = restService.FindUserAccountByUniqueId(loginController.GetSessionUserUniqueId(httpSession));

            var cartList = restService.FindAllCartsByBuyerUniqueId(httpSession, buyerUserAccount.UniqueId.toString());

            if (cartList.isEmpty()) {
                return "Sepet boş";
            }

            var bill = new Bill(buyerUserAccount, BigDecimal.valueOf(0), LocalDateTime.now(), false, UUID.randomUUID());
            restService.SaveBill(bill);

            for (var cart : cartList) {
                var product = restService.FindProductByUniqueId(cart.Product.UniqueId.toString());
                if (!product.IsActive || product.Quantity < cart.Quantity) {
                    return "'" + product.Header + "' isimli ürün artık aktif değil.";
                }

                var billProduct = new BillProduct(buyerUserAccount, bill, product, cart.Quantity, product.Price);
                bill.TotalPrice = bill.TotalPrice.add(product.Price.multiply(BigDecimal.valueOf(cart.Quantity)));
                restService.SaveBillProduct(billProduct);

                product.Quantity -= cart.Quantity;
                restService.SaveProduct(product);

                restService.DeleteCart(cart);
            }

            restService.SaveBill(bill);

            return bill.UniqueId.toString();
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "Satın alma sırasında hata ile karşılaşıldı.";
        }
    }

    @RequestMapping(value = "Retailer/Orders", method = RequestMethod.GET)
    public String RetailerOrders(HttpSession httpSession, Model model) {
        try {
            if (!loginController.IsLoggedIn(httpSession)) {
                return "redirect:/Login/";
            }

            var buyerUserAccount = restService.FindUserAccountByUniqueId(loginController.GetSessionUserUniqueId(httpSession));

            var billList = restService.FindAllBillsByBuyerUniqueIdAndIsApproved(httpSession, buyerUserAccount.UniqueId.toString(), true);
            var billProductList = new ArrayList<List<BillProduct>>();
            billList.forEach(bill -> {
                var dbBillProductList = restService.FindAllBillProductsByBillUniqueId(httpSession, bill.UniqueId.toString());
                for (BillProduct billProduct : dbBillProductList) {
                    billProduct.Product.setPhotoList(restService.FindAllProductPhotosByProductUniqueId(billProduct.Product.UniqueId.toString()));
                }
                billProductList.add(dbBillProductList);
            });

            var unApprovedBillList = restService.FindAllBillsByBuyerUniqueIdAndIsApproved(httpSession, buyerUserAccount.UniqueId.toString(), false);
            var unApprovedBillProductList = new ArrayList<List<BillProduct>>();
            unApprovedBillList.forEach(bill -> {
                var dbUnApprovedBillProductList = restService.FindAllBillProductsByBillUniqueId(httpSession, bill.UniqueId.toString());
                for (BillProduct billProduct : dbUnApprovedBillProductList) {
                    billProduct.Product.setPhotoList(restService.FindAllProductPhotosByProductUniqueId(billProduct.Product.UniqueId.toString()));
                }
                unApprovedBillProductList.add(dbUnApprovedBillProductList);
            });

            model.addAttribute("title", "Siparişleri Görüntüle");
            model.addAttribute("billProductList", billProductList);
            model.addAttribute("unApprovedBillProductList", unApprovedBillProductList);

            return "Retailer/Orders";
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "redirect:/";
        }
    }

    @RequestMapping(value = "Retailer/OrderCancel", method = RequestMethod.POST)
    @ResponseBody
    public String RetailerOrderCancel(HttpSession httpSession, HttpServletRequest httpRequest) {
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
            if (bill.IsApproved) {
                return "Bu fatura önceden onaylanmış. Artık silinemez.";
            }

            var billProductList = restService.FindAllBillProductsByBillUniqueId(httpSession, bill.UniqueId.toString());
            if (billProductList.get(0).Supplier.Id != userAccount.Id) {
                return "Bu faturayı silemezsiniz.";
            }

            if (billProductList.size() < 0) {
                return "Faturaya ait ürün bulunamadı.";
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
            return "redirect:/";
        }
    }
}
