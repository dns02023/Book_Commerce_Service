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

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    // 주문
    @Transactional
    public Long order(Long memberId, Long itemId, int count){


        // 인자들을 통해 엔티티 가져오기
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());


        // 주문 상품 생성 (static 메서드 호출)
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

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


    // 취소


    // 검색





}
