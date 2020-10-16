package jpabook.jpashop.domain.item;


import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

// 상속관계 매핑
// item 을 상속받는 엔티티들을 처리하기 위해 한 테이블에 모든 필드값들을 다 관리하는 것(그림 참고)
// 이렇게 한 개의 테이블만을 사용하기 위해 각 아이템의 종류를 구분하기 위해 dtype이라는 구분 칼럼을 둔다.
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
//@Setter // setter로 item의 필드를 수정하지 말고, 핵심 비즈니스 로직을 통해서 수정하자.
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;


    // 다대다 관계에서 연관관계의 주인은 카테고리로 한건가?
    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    // 비즈니스 로직을 엔티티에 직접 구현!!
    // 재고 수량 증가
    // 만약 서비스 계층에서 아이템 관련 비즈니스 로직을 처리한다고 가정해보면,
    // stockQuantitiy 가져와서 setter 쓰고 등등 할 것이다.
    // 하지만 객체지향적인 접근법으로 하면, 데이터(재고 수량)를 가지고 있는 엔티티에 비즈니스 로직이 존재하는 것이 제일 바람직하다. => 응집력 강화
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }



}
