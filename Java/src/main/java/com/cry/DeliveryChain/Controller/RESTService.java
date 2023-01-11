package com.cry.DeliveryChain.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.cry.DeliveryChain.Core.Functions;
import com.cry.DeliveryChain.Entity.Bill;
import com.cry.DeliveryChain.Entity.BillProduct;
import com.cry.DeliveryChain.Entity.Cart;
import com.cry.DeliveryChain.Entity.Product;
import com.cry.DeliveryChain.Entity.UserAccount;
import com.cry.DeliveryChain.Repository.BillProductRepo;
import com.cry.DeliveryChain.Repository.BillRepo;
import com.cry.DeliveryChain.Repository.CartRepo;
import com.cry.DeliveryChain.Repository.ProductRepo;
import com.cry.DeliveryChain.Repository.UserAccountRepo;

@Controller
@RequestMapping(value = "/RESTService")
public class RESTService {
    ProductRepo productRepo;
    BillRepo billRepo;
    BillProductRepo billProductRepo;
    CartRepo cartRepo;
    UserAccountRepo userAccountRepo;
    Functions _functions;

    public RESTService(ProductRepo ProductRepo, BillRepo BillRepo, BillProductRepo BillProductRepo, CartRepo CartRepo, UserAccountRepo UserAccountRepo,
            Functions Functions) {
        this.productRepo = ProductRepo;
        this.billRepo = BillRepo;
        this.billProductRepo = BillProductRepo;
        this.cartRepo = CartRepo;
        this.userAccountRepo = UserAccountRepo;
        this._functions = Functions;
    }

    public UserAccount FindUserAccountByUsername(String username) {
        try {
            return userAccountRepo.findByUsername(username);
        }
        catch (Exception e) {
            return new UserAccount();
        }
    }

    public UserAccount FindUserAccountByUsernameOrEmail(String username, String email) {
        try {
            return userAccountRepo.findByUsernameOrEmail(username, email);
        }
        catch (Exception e) {
            return new UserAccount();
        }
    }

    public UserAccount FindUserAccountByUniqueId(String uniqueId) {
        try {
            return userAccountRepo.findByUniqueId(UUID.fromString(uniqueId));
        }
        catch (Exception e) {
            return new UserAccount();
        }
    }

    public void SaveUserAccount(UserAccount userAccount) {
        try {
            userAccountRepo.save(userAccount);
        }
        catch (Exception e) {
            _functions.Logger(e);
        }
    }

    public void SaveProduct(Product product) {
        try {
            productRepo.save(product);
        }
        catch (Exception e) {
            _functions.Logger(e);
        }
    }

    public void DeleteProduct(Product product) {
        try {
            productRepo.delete(product);
        }
        catch (Exception e) {
            _functions.Logger(e);
        }
    }

    @RequestMapping(value = "/FindProductByUniqueId", method = RequestMethod.GET)
    @ResponseBody
    public Product FindProductByUniqueId(String uniqueId) {
        try {
            return productRepo.findByUniqueId(UUID.fromString(uniqueId));
        }
        catch (Exception e) {
            return new Product();
        }
    }

    @RequestMapping(value = "/FindAllProductsByUserAccountUniqueId", method = RequestMethod.GET)
    @ResponseBody
    public List<Product> FindAllProductsByUserAccountUniqueId(String uniqueId) {
        try {
            return productRepo.findAllByUserAccountUniqueId(UUID.fromString(uniqueId));
        }
        catch (Exception e) {
            return new ArrayList<Product>();
        }
    }

    @RequestMapping(value = "/FindAllProductsByIsActiveAndQuantity", method = RequestMethod.GET)
    @ResponseBody
    public List<Product> FindAllProductsByIsActiveAndQuantity(Boolean isActive, Integer quantity) {
        try {
            return productRepo.findAllByIsActiveAndQuantity(isActive, quantity);
        }
        catch (Exception e) {
            return new ArrayList<Product>();
        }
    }

    public void SaveCart(Cart cart) {
        try {
            cartRepo.save(cart);
        }
        catch (Exception e) {
            _functions.Logger(e);
        }
    }

    public void DeleteCart(Cart cart) {
        try {
            cartRepo.delete(cart);
        }
        catch (Exception e) {
            _functions.Logger(e);
        }
    }

    @RequestMapping(value = "/FindCartByBuyerUniqueIdAndProductId", method = RequestMethod.GET)
    @ResponseBody
    public Cart FindCartByBuyerUniqueIdAndProductId(String buyerUniqueId, Integer productId) {
        try {
            return cartRepo.findByBuyerUniqueIdAndProductId(UUID.fromString(buyerUniqueId), productId);
        }
        catch (Exception e) {
            return new Cart();
        }
    }

    @RequestMapping(value = "/FindAllCartsByBuyerUniqueId", method = RequestMethod.GET)
    @ResponseBody
    public List<Cart> FindAllCartsByBuyerUniqueId(String uniqueId) {
        try {
            return cartRepo.findAllByBuyerUniqueId(UUID.fromString(uniqueId));
        }
        catch (Exception e) {
            return new ArrayList<Cart>();
        }
    }

    public void SaveBill(Bill bill) {
        try {
            billRepo.save(bill);
        }
        catch (Exception e) {
            _functions.Logger(e);
        }
    }

    @RequestMapping(value = "/FindBillByUniqueId", method = RequestMethod.GET)
    @ResponseBody
    public Bill FindBillByUniqueId(String uniqueId) {
        try {
            return billRepo.findByUniqueId(UUID.fromString(uniqueId));
        }
        catch (Exception e) {
            return new Bill();
        }
    }

    @RequestMapping(value = "/FindAllBillsByBuyerUniqueIdAndIsApproved", method = RequestMethod.GET)
    @ResponseBody
    public List<Bill> FindAllBillsByBuyerUniqueIdAndIsApproved(String buyerUniqueId, Boolean isApproved) {
        try {
            return billRepo.findAllByBuyerUniqueIdAndIsApproved(UUID.fromString(buyerUniqueId), isApproved);
        }
        catch (Exception e) {
            return new ArrayList<Bill>();
        }
    }

    public void SaveBillProduct(BillProduct billProduct) {
        try {
            billProductRepo.save(billProduct);
        }
        catch (Exception e) {
            _functions.Logger(e);
        }
    }

    @RequestMapping(value = "/FindAllBillProductsByBillUniqueId", method = RequestMethod.GET)
    @ResponseBody
    public List<BillProduct> FindAllBillProductsByBillUniqueId(String uniqueId) {
        try {
            return billProductRepo.findAllByBillUniqueId(UUID.fromString(uniqueId));
        }
        catch (Exception e) {
            return new ArrayList<BillProduct>();
        }
    }

    @RequestMapping(value = "/FindAllBillProductsBySupplierUniqueId", method = RequestMethod.GET)
    @ResponseBody
    public List<BillProduct> FindAllBillProductsBySupplierUniqueId(String uniqueId) {
        try {
            return billProductRepo.findAllBySupplierUniqueId(UUID.fromString(uniqueId));
        }
        catch (Exception e) {
            return new ArrayList<BillProduct>();
        }
    }
}
