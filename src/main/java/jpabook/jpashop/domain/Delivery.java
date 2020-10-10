package jpabook.jpashop.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Delivery {


    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    // 주문과 배송은 서로 일대일 관계
    // 일대일 관계에서는 외래키가 둘 중 아무데나 있을 수 있다.
    // 이 상황에서는 자주 접근하는 객체에 외래키를 두는 것이 좋다. => Order에 둔다.
    // => 연관관계의 주인은 Order
    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    @Embedded
    private Address address;
    // 내장 타입이다.

    // 상태 필드를 String 타입으로 저장? enumerated 공부하자.
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status; // 배송 상태 [READY, COMP]




}
