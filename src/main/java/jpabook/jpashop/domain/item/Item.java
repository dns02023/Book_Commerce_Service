package jpabook.jpashop.domain.item;


import jpabook.jpashop.domain.Category;
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
@Getter @Setter
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



}
