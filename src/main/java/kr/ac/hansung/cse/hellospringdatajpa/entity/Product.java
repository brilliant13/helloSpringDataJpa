package kr.ac.hansung.cse.hellospringdatajpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "상품명은 필수 입력 항목입니다.")
    @Size(min = 1, max = 100, message = "상품명은 1~100자 사이여야 합니다.")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "브랜드는 필수 입력 항목입니다.")
    @Size(min = 1, max = 50, message = "브랜드는 1~50자 사이여야 합니다.")
    private String brand;

    @Column(nullable = false)
    @NotBlank(message = "제조국가는 필수 입력 항목입니다.")
    @Size(min = 1, max = 30, message = "제조국가는 1~30자 사이여야 합니다.")
    private String madeIn;

    @Column(nullable = false)
    @NotNull(message = "가격은 필수 입력 항목입니다.")
    @DecimalMin(value = "0.0", inclusive = true, message = "가격은 0 이상이어야 합니다.")
    @DecimalMax(value = "999999999.99", message = "가격이 너무 큽니다.")
    private double price;

    public Product(String name, String brand, String madeIn, double price) {
        this.name = name;
        this.brand = brand;
        this.madeIn = madeIn;
        this.price = price;
    }
}