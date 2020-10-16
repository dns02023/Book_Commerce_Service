package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    // orderitem 입장에서는 item과는 다대일 관계이다. 또한 item 외래키를 가지고 있으므로, 연관관계의 주인임
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    // 하나의 order가 여러개의 orderitem을 가질 수 있고, orderitem은 하나의 order만을 가질 수 있으므로,
    // orderitem 입장에서는 다대일 관계임
    // 또한 order_id라는 외래키를 포함하고 있으므로 이 연관관계의 주인은 orderitem이다
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; //주문 가격
    private int count; // 주문 수량

    // Order와 연계되는 비즈니스 로직
    public void cancel() {
        // 이 주문아이템의 해당 item의 재고 수량을 회복시켜준다.
        // this.getItem().addStock(count);
        getItem().addStock(count);
        // this 앞에 안써도 되는 건가???

    }
}
