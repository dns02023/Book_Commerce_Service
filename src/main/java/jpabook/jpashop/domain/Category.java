package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {

    // jpa 테이블 매핑될때 id명 따로 정해주기 위해서 column 어노테이션 쓰는 듯?
    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    // 카테고리와 아이템은 다대다 관계임
    // 다대다 관계에서도 연관관계 주인이 필요하긴 함
    // 다대다 관계에서는 중간 테이블에 매핑을 해줘야함
    // 객체는 컬렉션으로 다대다를 표현할 수 있지만, 관계형 DB는 그렇게 안됨
    // => 관계형 DB는 일대다-중간 테이블-다대일 의 형태로 풀어내줘야 함
    // 실무에서는 권장되지 않음
    @ManyToMany
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
            // 중간 테이블에서 사용되는 외래키값들 넣어주기
    private List<Item> items = new ArrayList<>();


    // 더 상위 카테고리 이므로 이 카테고리 입장에서는 다대일이다. 부모는 여러 자식을 가지므로
    // 자식이 부모의 id를 외래키로 가진다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent; // 더 상위 개념의 카테고리

    // 반대로 더 하위 카테고리들은 여러 카테고리를 가질 수 있으므로 일대다 관계
    // 이 부분은 잘 모르겠다. 왜 parent로 mappedBy 하는 걸까? 계층구조 공부하자.
    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    // 연관관계 편의 메서드
    // 카테고리 간 부모 자식 관계는 양방향에서 다 설정해줘야 함
    public void addChildCategory(Category child){
        this.child.add(child);
        child.setParent(this);
    }


}
