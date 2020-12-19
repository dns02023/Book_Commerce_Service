package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    // 도메인 모델 패턴으로 설계 (교재 참고)
    // 엔티티에서 이미 설계된 비즈니스 로직들을 서비스에서 사용한다
    // 서비스는 단순히 레포지토리를 통해 엔티티 가져오고, 엔티티의 비즈니스 로직을 호출하는 역할만 한다.

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    // 주문하기
    @Transactional
    public Long order(Long memberId, Long itemId, int count){


        // 인자들을 통해 엔티티 가져오기
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 순서: 주문상품 먼저 생성 -> 주문 생성

        // 주문 상품 생성 (static 메서드 호출)
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 일단 간단하게 주문 상품은 하나의 주문 상품 객체만 주문 객체에 넘기도록 하였음 (서비스 계층에서는)
        // 물론 Order 엔티티 내에서 설계한 생성메서드는 여러 개의 주문 상품 객체를 받을 수 있도록 설계되어 있음

        // 주문 생성 (static 메서드 호출)
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order);
        // 이때, delivery, orderItem 객체는 따로 persist(save) 해줘야 하는 거 아닌가?
        // deliveryRepository, orderItemRepository는 필요없는건가?
        // 왜 order 객체만 persist 하는가?
        // => CascadeType 설정해주었기 때문에 order를 persist하면 delivery와 orderItem 리스트들도 각각 다 자동으로 persist된다.
        // cascade의 범위: 어디까지 cascade처리 할까? => 오직 order만이 delivery와 orderItem을 참조한다. (private owner)
        // 같은 라이프 사이클을 가지는 관계, persist를 함께하는 관계
        // 만약 다른 엔티티들도 저 둘을 참조한다면 cascade 처리하면 안됨 => 별도의 repository 생성해야 함
        return order.getId();
    }

    // 주문 취소하기
    @Transactional
    public void cancelOrder(Long orderId) {
        // 주문 엔티티 가져오기
        Order order = orderRepository.findOne(orderId);
        // 주문 취소
        order.cancel();
        // jpa의 강점: update 쿼리를 따로 날릴 필요없이 jpa가 비즈니스 로직에 따라서 데이터베이스를 다 업데이트 해준다.
    }

    // 주문 검색하기
    // 동적 쿼리

}
