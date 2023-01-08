package com.cry.DeliveryChain.Controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.cry.DeliveryChain.Core.Functions;
import com.cry.DeliveryChain.Entity.Product;
import com.cry.DeliveryChain.Repository.ProductRepo;
import com.cry.DeliveryChain.Repository.UserAccountRepo;

@Controller
@RequestMapping(value = "/")
public class ProductController {
    ProductRepo productRepo;
    UserAccountRepo userAccountRepo;
    LoginController loginController;
    Functions _functions;

    public ProductController(ProductRepo ProductRepo, UserAccountRepo UserAccountRepo, LoginController LoginController, Functions Functions) {
        this.productRepo = ProductRepo;
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

            var productList = productRepo.findAll();

            model.addAttribute("title", "Sipariş Yönet");
            model.addAttribute("productList", productList);
            return "Product/Index";
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

            var photoBase64 =
                    "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAQAAAAAYLlVAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAAAmJLR0QA/4ePzL8AAAAHdElNRQfkAxgQICe/pMQtAAADZklEQVRo3u2ZS0hUURjHf3dGHc1X4ytLkUxL3TgliUgJFm60jSCRgZtcRA8iCNpGFEi6qUVCRIuwhVEE7UqCqKiwjNRMkdJeYva0JsdRR53bIjHvODP3nDv3uvL/LWfu9/+dxz3nfOfCqlYVmRRskSYw9lQaLlzk4sSOh1EG6OY9syvT6jxO0YMXdUnMMUIbu4mx2jyWRgbxa8z/x29aybHS3smFgJYvj6eUWmW/livM69irqLymzAr7GFqYE7BXUeki33yAeiYE7VVU2ogz134DLyTsVbzsNRfgiNDoL40OEsyzj+eepL2KmwqR1GIL6SaKpaGT2GkeQAGpBvrNhd0sgByRVMuULfImiAEkG7CHBJGdIcLNNHKJAbgN5Z7EZxbACPMGAEaZMgvgDeMGAHpFsMUAhumTtp/gscjfxAA83EaVBHjOS2noMMqmW2ohnmK/mfYADUxKALQTbzaAg/PCO2I3hWbbA6TQJoQwyA4r7AFSaWVa9zhWLpNSb5NxUko1tZTzkx94ecAXinCG+LeHaxynD3BSzz62kc4ME9Jv0ILSOEYnEwtVwDBHSQIUCmiiP6An5hnjBtU4ABsV3GUWFRUf77iIK3QFFro0K6GFSk0PzXKHJrrwo5BJCSXk4sTGJKP008UQM0AmhzhMhibXCGe5Kle4bacv6AiP0Uwx0Yv4UUQtLmYKmRzgWdCJ6uGEzJkii0dhptkY7TRQSOKCtUIsWVTRzCt8IZ/6RZ3oENg4x0kdxDm+84ERxvGTwHo2ksUanWd6qOWjSPvL+Cp9BhaLMyL2Ni5ZZK8ytLxkW74bbqZGfLJIKo89+gCVZFsGADWBMyUQwM4ug9c2YiomNzxAhoEaSEbpuMIDbCTLUgA7W8MDbCHRUgAo0pYrgQD5ls4AgBxtE7UAirV3XACkkRIaIIZ1lgMkaE8TgQDGylAZBXhoAaLNvloKoijtedkW8GO0VDIjsoV7CxRDFxHyfRASQMW/AgCaklULMG3wJkBGPm2lrQXwMGw5wDifQgP4eWj5IPSGA4AOBiy193Fd796kUaoKlo2bJOkxOjit+1HCaNwnT6SbHBzkbcjPMkbDzeVgW13wzVchjzqqyCdZu2xIS8GPl890cosnzIgC/FMcGaTgiAgA5vnDN9wrssStyoj+AhbAtJAsggKZAAAAJXRFWHRkYXRlOmNyZWF0ZQAyMDIwLTAzLTI0VDE2OjMyOjM5KzAwOjAwFC32vAAAACV0RVh0ZGF0ZTptb2RpZnkAMjAyMC0wMy0yNFQxNjozMjozOSswMDowMGVwTgAAAAAASUVORK5CYII";
            if (Photo != null) {
                photoBase64 = "data:" + Photo.getContentType() + ";base64," + new String(Base64.getEncoder().encode(Photo.getBytes()));
            }

            var userAccount = userAccountRepo.findByUniqueId(loginController.GetSessionUserUniqueId(httpSession));

            var currentAdditionDate = LocalDateTime.now().isBefore(LocalDateTime.parse(AdditionDate)) ? LocalDateTime.now() : LocalDateTime.parse(AdditionDate);

            var product = new Product(userAccount, Header, Description, new BigDecimal(Price), Integer.parseInt(Quantity), currentAdditionDate, photoBase64, true,
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
}
