package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookForm {

    // Item class 공통 속성
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    // Book class만의 속성
    private String author;
    private String isbn;
}
