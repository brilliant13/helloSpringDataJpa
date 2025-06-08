package kr.ac.hansung.cse.hellospringdatajpa.service;

import kr.ac.hansung.cse.hellospringdatajpa.entity.Product;
import kr.ac.hansung.cse.hellospringdatajpa.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository repo;

    public Product get(long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
    }

    public List<Product> listAll() {
        return repo.findAll();
    }

    public void save(Product product) {
        // 유효성 검사
        if (product.getPrice() < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("상품명은 필수입니다.");
        }
        if (product.getBrand() == null || product.getBrand().trim().isEmpty()) {
            throw new IllegalArgumentException("브랜드는 필수입니다.");
        }
        if (product.getMadeIn() == null || product.getMadeIn().trim().isEmpty()) {
            throw new IllegalArgumentException("제조국가는 필수입니다.");
        }

        repo.save(product);
    }

    public void delete(long id) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Product not found with id: " + id);
        }
        repo.deleteById(id);
    }
}