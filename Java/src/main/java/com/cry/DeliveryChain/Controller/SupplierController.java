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
import org.springframework.web.multipart.MultipartFile;
import com.cry.DeliveryChain.Core.Functions;
import com.cry.DeliveryChain.Entity.BillProduct;
import com.cry.DeliveryChain.Entity.Product;
import com.cry.DeliveryChain.Repository.BillProductRepo;
import com.cry.DeliveryChain.Repository.BillRepo;
import com.cry.DeliveryChain.Repository.ProductRepo;
import com.cry.DeliveryChain.Repository.UserAccountRepo;

@Controller
@RequestMapping(value = "/Supplier")
public class SupplierController {
    ProductRepo productRepo;
    BillRepo billRepo;
    BillProductRepo billProductRepo;
    UserAccountRepo userAccountRepo;
    LoginController loginController;
    Functions _functions;

    @Value("${App.ProductBase64Photo}")
    private String productBase64Photo;

    public SupplierController(ProductRepo ProductRepo, BillRepo BillRepo, BillProductRepo BillProductRepo, UserAccountRepo UserAccountRepo,
            LoginController LoginController, Functions Functions) {
        this.productRepo = ProductRepo;
        this.billRepo = BillRepo;
        this.billProductRepo = BillProductRepo;
        this.userAccountRepo = UserAccountRepo;
        this.loginController = LoginController;
        this._functions = Functions;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String Index(HttpSession httpSession, Model model) {
        try {
            if (!loginController.IsLoggedIn(httpSession)) {
                return "redirect:/Login/";
            }

            var userAccount = userAccountRepo.findByUniqueId(loginController.GetSessionUserUniqueId(httpSession));

            var productList = productRepo.findAllByUserAccountId(userAccount.Id);

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
    public String Add(HttpSession httpSession, String Header, String Description, String Price, String Quantity, String AdditionDate, MultipartFile Photo) {
        try {
            if (!loginController.IsLoggedIn(httpSession)) {
                return "NotLoggedIn";
            }

            if (Photo != null) {
                productBase64Photo = "data:" + Photo.getContentType() + ";base64," + new String(Base64.getEncoder().encode(Photo.getBytes()));
            }

            var userAccount = userAccountRepo.findByUniqueId(loginController.GetSessionUserUniqueId(httpSession));

            var currentAdditionDate = LocalDateTime.now().isBefore(LocalDateTime.parse(AdditionDate)) ? LocalDateTime.now() : LocalDateTime.parse(AdditionDate);

            var product = new Product(userAccount, Header, Description, new BigDecimal(Price), Integer.parseInt(Quantity), currentAdditionDate, productBase64Photo, true,
                    UUID.randomUUID());
            productRepo.save(product);

            return product.UniqueId.toString();
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "Error";
        }
    }

    @RequestMapping(value = "/Edit", method = RequestMethod.POST)
    @ResponseBody
    public Boolean Edit(HttpSession httpSession, String Header, String Description, String Price, String Quantity, String AdditionDate, MultipartFile Photo,
            String IsActive, String UniqueId) {
        try {
            if (!loginController.IsLoggedIn(httpSession)) {
                return false;
            }

            var product = productRepo.findByUniqueId(UUID.fromString(UniqueId));
            if (product == null) {
                return false;
            }

            product.Header = Header;
            product.Description = Description;
            product.Price = new BigDecimal(Price);
            product.Quantity = Integer.parseInt(Quantity);
            product.AdditionDate = LocalDateTime.parse(AdditionDate);
            product.Photo = "data:" + Photo.getContentType() + ";base64," + new String(Base64.getEncoder().encode(Photo.getBytes()));
            product.IsActive = Boolean.parseBoolean(IsActive);

            productRepo.save(product);

            return true;
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return false;
        }
    }

    @RequestMapping(value = "/Delete", method = RequestMethod.POST)
    @ResponseBody
    public boolean Delete(HttpSession httpSession, String UniqueId) {
        try {
            if (!loginController.IsLoggedIn(httpSession)) {
                return false;
            }

            var product = productRepo.findByUniqueId(UUID.fromString(UniqueId));
            if (product != null) {
                productRepo.delete(product);
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

            var userAccount = userAccountRepo.findByUniqueId(loginController.GetSessionUserUniqueId(httpSession));

            var billProductList = billProductRepo.findAllBySupplierId(userAccount.Id);

            MultiValueMap<String, BillProduct> billMapList = new LinkedMultiValueMap<String, BillProduct>();
            for (BillProduct billProduct : billProductList) {
                if (billProduct.Bill.IsApproved) {
                    billMapList.add(billProduct.Bill.UniqueId.toString(), billProduct);
                }
            }

            var unApprovedBillProductList = billProductRepo.findAllBySupplierId(userAccount.Id);
            MultiValueMap<String, BillProduct> unApprovedBillMapList = new LinkedMultiValueMap<String, BillProduct>();
            for (BillProduct unApprovedBillProduct : unApprovedBillProductList) {
                if (!unApprovedBillProduct.Bill.IsApproved) {
                    unApprovedBillMapList.add(unApprovedBillProduct.Bill.UniqueId.toString(), unApprovedBillProduct);
                }
            }

            model.addAttribute("title", "Sipariş Listele");
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

            var userAccount = userAccountRepo.findByUniqueId(loginController.GetSessionUserUniqueId(httpSession));

            var billUniqueId = UUID.fromString(httpRequest.getParameter("UniqueId"));

            var bill = billRepo.findByUniqueId(billUniqueId);
            if (bill == null) {
                return "Fatura bulunamadı.";
            }

            var billProductList = billProductRepo.findAllByBillId(bill.Id);
            if (billProductList.get(0).Supplier.Id != userAccount.Id) {
                return "Bu faturayı onaylayamazsınız.";
            }

            bill.IsApproved = true;
            billRepo.save(bill);

            return bill.UniqueId.toString();
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "Sipariş onaylanırken hata ile karşılaşıldı.";
        }

    }
}
