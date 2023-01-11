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
import com.cry.DeliveryChain.Repository.BillProductRepo;
import com.cry.DeliveryChain.Repository.BillRepo;
import com.cry.DeliveryChain.Repository.CartRepo;
import com.cry.DeliveryChain.Repository.ProductRepo;
import com.cry.DeliveryChain.Repository.UserAccountRepo;

@Controller
@RequestMapping(value = "/")
public class RetailerController {
    ProductRepo productRepo;
    BillRepo billRepo;
    BillProductRepo billProductRepo;
    CartRepo cartRepo;
    UserAccountRepo userAccountRepo;
    LoginController loginController;
    Functions _functions;

    public RetailerController(ProductRepo ProductRepo, BillRepo BillRepo, BillProductRepo BillProductRepo, CartRepo CartRepo, UserAccountRepo UserAccountRepo,
            LoginController LoginController, Functions Functions) {
        this.productRepo = ProductRepo;
        this.billRepo = BillRepo;
        this.billProductRepo = BillProductRepo;
        this.cartRepo = CartRepo;
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

            var productList = productRepo.findAllByIsActiveAndQuantity(true, 1);

            model.addAttribute("title", "Ürünler");
            model.addAttribute("productList", productList);
            return "Retailer/Index";
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

            var buyerUserAccount = userAccountRepo.findByUniqueId(loginController.GetSessionUserUniqueId(httpSession));

            var productUniqueId = UUID.fromString(httpRequest.getParameter("UniqueId"));
            var productQuantity = Integer.parseInt(httpRequest.getParameter("Quantity"));

            var product = productRepo.findByUniqueId(productUniqueId);

            if (!product.IsActive || product.Quantity < productQuantity) {
                return "Ürün aktif değil ya da stok aşıldı.";
            }

            var cart = cartRepo.findByBuyerIdAndProductId(buyerUserAccount.Id, product.Id);
            if (cart == null) {
                cart = new Cart(buyerUserAccount, product, productQuantity, UUID.randomUUID());
                cartRepo.save(cart);
            } else {
                cart.Quantity = productQuantity;
                cartRepo.save(cart);
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

            var buyerUserAccount = userAccountRepo.findByUniqueId(loginController.GetSessionUserUniqueId(httpSession));

            var productUniqueId = UUID.fromString(httpRequest.getParameter("UniqueId"));
            var product = productRepo.findByUniqueId(productUniqueId);

            var cartList = cartRepo.findAllByBuyerId(buyerUserAccount.Id);
            for (Cart cart : cartList) {
                if (cart.Product.UniqueId == product.UniqueId) {
                    cartRepo.delete(cart);
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

            var buyerUserAccount = userAccountRepo.findByUniqueId(loginController.GetSessionUserUniqueId(httpSession));
            var cartProductList = cartRepo.findAllByBuyerId(buyerUserAccount.Id);

            var productList = new ArrayList<Product>();
            var inActiveProductList = new ArrayList<Product>();

            var cartTotalPriceObject = new Object() {
                BigDecimal totalPrice = BigDecimal.ZERO;
            };

            for (Cart cartProduct : cartProductList) {
                var product = productRepo.findByUniqueId(cartProduct.Product.UniqueId);
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

            return "/Retailer/CheckOut";
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

            var buyerUserAccount = userAccountRepo.findByUniqueId(loginController.GetSessionUserUniqueId(httpSession));

            var cartList = cartRepo.findAllByBuyerId(buyerUserAccount.Id);

            if (cartList.isEmpty()) {
                return "Sepet boş";
            }

            for (Cart cart : cartList) {
                var product = productRepo.findByUniqueId(cart.Product.UniqueId);
                if (!product.IsActive || product.Quantity < cart.Quantity) {
                    return "'" + product.Header + "' isimli ürün artık aktif değil.";
                }
            }

            var bill = new Bill(buyerUserAccount, BigDecimal.valueOf(0), LocalDateTime.now(), false, UUID.randomUUID());
            billRepo.save(bill);

            for (Cart cart : cartList) {
                var product = productRepo.findByUniqueId(cart.Product.UniqueId);
                var billProduct = new BillProduct(buyerUserAccount, bill, product, product.Quantity, product.Price);
                bill.TotalPrice = bill.TotalPrice.add(product.Price.multiply(BigDecimal.valueOf(product.Quantity)));
                billProductRepo.save(billProduct);

                product.Quantity -= cart.Quantity;
                productRepo.save(product);

                cartRepo.delete(cart);
            }

            billRepo.save(bill);

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

            var buyerUserAccount = userAccountRepo.findByUniqueId(loginController.GetSessionUserUniqueId(httpSession));

            var billList = billRepo.findAllByBuyerIdAndIsApproved(buyerUserAccount.Id, true);
            var billProductList = new ArrayList<List<BillProduct>>();
            billList.forEach(bill -> {
                billProductList.add(billProductRepo.findAllByBillId(bill.Id));
            });

            var unApprovedBillList = billRepo.findAllByBuyerIdAndIsApproved(buyerUserAccount.Id, false);
            var unApprovedBillProductList = new ArrayList<List<BillProduct>>();
            unApprovedBillList.forEach(bill -> {
                unApprovedBillProductList.add(billProductRepo.findAllByBillId(bill.Id));
            });

            model.addAttribute("title", "Siparişleri Görüntüle");
            model.addAttribute("billProductList", billProductList);
            model.addAttribute("unApprovedBillProductList", unApprovedBillProductList);

            return "/Retailer/Orders";
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "redirect:/";
        }
    }
}
