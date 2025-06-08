package kr.ac.hansung.cse.hellospringdatajpa.controller;

import jakarta.validation.Valid;
import kr.ac.hansung.cse.hellospringdatajpa.entity.Product;
import kr.ac.hansung.cse.hellospringdatajpa.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

//@Controller
//@RequestMapping("/products")
//// localhost:8080/helloSpringDataJpa/products
//
//public class ProductController {
//
//    @Autowired
//    private ProductService service;
//
//    // 모든 사용자 접근 가능 (상품 목록 조회)
//    @GetMapping({"", "/"})
//    public String viewHomePage(Model model) {
//        List<Product> listProducts = service.listAll();
//        model.addAttribute("listProducts", listProducts);
//        return "products/index";
//    }
//
//    // ADMIN만 접근 가능 (상품 등록 폼)
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/new")
//    public String showNewProductPage(Model model) {
//        Product product = new Product();
//        model.addAttribute("product", product);
//        return "products/new_product";
//    }
//
//    // ADMIN만 접근 가능 (상품 수정 폼)
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/edit/{id}")
//    public String showEditProductPage(@PathVariable(name = "id") Long id, Model model) {
//        try {
//            Product product = service.get(id);
//            model.addAttribute("product", product);
//            return "products/edit_product";
//        } catch (Exception e) {
//            return "redirect:/products?error=productNotFound";
//        }
//    }
//
//    // ADMIN만 접근 가능 (상품 저장)
//    @PreAuthorize("hasRole('ADMIN')")
//    @PostMapping("/save")
//    public String saveProduct(@ModelAttribute("product") Product product,
//                              RedirectAttributes redirectAttributes) {
//        try {
//            service.save(product);
//            redirectAttributes.addFlashAttribute("successMessage", "상품이 성공적으로 저장되었습니다.");
//            return "redirect:/products";
//        } catch (IllegalArgumentException e) {
//            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
//            if (product.getId() != null) {
//                return "redirect:/products/edit/" + product.getId();
//            } else {
//                return "redirect:/products/new";
//            }
//        }
//    }
//
//    // ADMIN만 접근 가능 (상품 삭제)
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/delete/{id}")
//    public String deleteProduct(@PathVariable(name = "id") Long id,
//                                RedirectAttributes redirectAttributes) {
//        try {
//            service.delete(id);
//            redirectAttributes.addFlashAttribute("successMessage", "상품이 성공적으로 삭제되었습니다.");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("errorMessage", "상품 삭제 중 오류가 발생했습니다.");
//        }
//        return "redirect:/products";
//    }
//}
@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService service;

    // 모든 사용자 접근 가능 (상품 목록 조회)
    @GetMapping({"", "/"})
    public String viewHomePage(Model model) {
        List<Product> listProducts = service.listAll();
        model.addAttribute("listProducts", listProducts);
        return "products/index";
    }

    // ADMIN만 접근 가능 (상품 등록 폼)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String showNewProductPage(Model model) {
        Product product = new Product();
        model.addAttribute("product", product);
        return "products/new_product";
    }

    // ADMIN만 접근 가능 (상품 수정 폼)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String showEditProductPage(@PathVariable(name = "id") Long id, Model model) {
        try {
            Product product = service.get(id);
            model.addAttribute("product", product);
            return "products/edit_product";
        } catch (Exception e) {
            return "redirect:/products?error=productNotFound";
        }
    }

    // ADMIN만 접근 가능 (상품 저장) - 유효성 검사 추가
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute("product") Product product,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {

        // 🆕 유효성 검사 실패 시 처리
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder("입력 오류: ");
            bindingResult.getAllErrors().forEach(error -> {
                errorMessage.append(error.getDefaultMessage()).append(" ");
            });

            redirectAttributes.addFlashAttribute("errorMessage", errorMessage.toString());
            redirectAttributes.addFlashAttribute("product", product);

            // 수정인지 신규 등록인지 확인하여 적절한 페이지로 리다이렉트
            if (product.getId() != null) {
                return "redirect:/products/edit/" + product.getId();
            } else {
                return "redirect:/products/new";
            }
        }

        // 🆕 추가 비즈니스 로직 검증
        try {
            // 가격 검증 (음수 방지)
            if (product.getPrice() < 0) {
                throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
            }

            // 중복 상품명 검증 (선택사항)
            if (isDuplicateProductName(product)) {
                throw new IllegalArgumentException("동일한 상품명이 이미 존재합니다.");
            }

            service.save(product);
            redirectAttributes.addFlashAttribute("successMessage", "상품이 성공적으로 저장되었습니다.");
            return "redirect:/products";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("product", product);

            if (product.getId() != null) {
                return "redirect:/products/edit/" + product.getId();
            } else {
                return "redirect:/products/new";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "저장 중 예상치 못한 오류가 발생했습니다: " + e.getMessage());

            if (product.getId() != null) {
                return "redirect:/products/edit/" + product.getId();
            } else {
                return "redirect:/products/new";
            }
        }
    }

    // ADMIN만 접근 가능 (상품 삭제)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") Long id,
                                RedirectAttributes redirectAttributes) {
        try {
            // 🆕 삭제 전 검증 (선택사항)
            Product product = service.get(id);
            if (product == null) {
                throw new IllegalArgumentException("존재하지 않는 상품입니다.");
            }

            service.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "상품이 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "상품 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/products";
    }

    // 중복 상품명 검증 메서드
    private boolean isDuplicateProductName(Product product) {
        List<Product> existingProducts = service.listAll();
        return existingProducts.stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(product.getName()) &&
                        !p.getId().equals(product.getId()));
    }
}

