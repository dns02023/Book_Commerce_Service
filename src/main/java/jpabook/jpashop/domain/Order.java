package jpabook.jpashop.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    // Member와의 관계 설정
    // 주문에게 회원은 다대일 관계
    // 외래키 설정 => member_id
    // 연관관계의 주인이므로 여기서는 외래키인 member 값을 변경할 수 있음
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // orderitem의 order필드에 의해서 매핑이 된다는 의미
    // cascade 옵션을 사용하면, order를 persist할때, orderitems 컬렉션 내의 orderitem 객체들을 모두 한번에 persist할 수 있음
    // => persist 전파, delete 역시 한번에 모두 수행
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    // 배송 주문 일대일 연관관계의 주인은 Order (주문이 접근할 일이 더 많기 떄문에)
    // 배송 외래키를 가지게 된다.
    // 원칙적으로는 order와 delivery를 각각 persist해줘야 하지만,
    // cascade를 설정해놓으면 order만 persist하면 delivery도 persist된다.
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;


    private LocalDateTime orderDate; //주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문상태 [ORDER, CANCEL]

    // static 생성 메서드가 아닌 그냥 생성자로 객체를 생성하는 것을 방지 => 응집도 강화
    protected Order() {
    }

    //양방향 연관관계 편의 메서드 : 양방향 연관관계 상황에서는 둘중 좀더 핵심적인 역할을 하는 클래스에 구현해주는 것이 좋음
    //jpa, db상의 연관관계 말고도 객체 사이의 연관관계를 설정을 해줘야 함
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
        // 자바 연습할 때 했던 것 처럼 객체간 협력을 구현 => 양방향 연관관계 설정
        // setMember만 호출하면 Member에서의 order 추가도 자동으로 호출됨
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
        // order <-> orderitem 도 마찬가지로 양방향 연관관계를 코드 내에서 설정해줘야 함
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    // 생성 메서드
    // Order 객체를 생성할 때, setter를 써서 연관관계(필드)를 설정하는 게 아니라,
    // 객체 생성할 때 무조건 createOrder()를 호출해서 연관관계(필드)를 모두 설정
    // order를 생성할 때 복잡한 연관관계가 생성된다. => 별도의 생성 메서드로 처리

    // 왜 static? => 하나의 특정 Order 객체 생성(new) 이전에 호출되야 하므로. + Order 클래스의 시작이라고 할 수 있음
    // OrderItem은 가변인자로 받기 (...)
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        // order 생성과 관련된 수정은 이 메서드만 수정하면 된다.
        // Order 타입 객체 생성을 이 메서드 안에서 수행 => static
        Order order = new Order();

        // order 전체 연관관계 설정
        // 필요한 메서드들(setter) 호출
        order.setMember(member);
        order.setDelivery(delivery);
        // 재고에 대한 비즈니스 로직 처리는? => 이미 인자로 넘어온 orderItem 생성 단계에서 수행했음 (createOrderItem())
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        // 상태, 날짜 설정
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    // 주문 비즈니스 로직을 역시 주문 엔티티 안에 구현
    public void cancel(){
        if(delivery.getStatus() == DeliveryStatus.COMP){
            // 배송이 이미 완료되버리면 취소 못함
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }
        // 주문 상태 변경
        this.setStatus(OrderStatus.CANCEL);
        // 주문을 취소했으므로, 주문에 담겨있던 상품들 재고 회복
        for(OrderItem orderItem : this.orderItems){
            // order 에 포함되는 하위의 orderitem 객체들을 모두 각각 cancel() 해준다.
            orderItem.cancel();
        }

    }

    // 조회 로직
    // 전체 주문가격 조회
    public int getTotalPrice() {
        // orderItem 객체들의 가격을 다 더하면 된다.
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems){
            // orderItem의 total price => 주문 가격과 수량이 있으므로,
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;

        // 똑같은 로직
//        return orderItems.stream()
//                .mapToInt(OrderItem::getTotalPrice)
//                .sum();

    }




}

