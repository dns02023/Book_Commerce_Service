package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {


    // 테스트 용
    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;


    @Test
    public void 상품주문() throws Exception {
        // given
        Member member = createTestMember();

        Book book = createTestBook("jpa book", 10000, 10);

        int orderCount = 2;

        // when
        // 주문하기
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // then
        // 잘 주문이 되었는지 검증
        Order testOrder = orderRepository.findOne(orderId);

        Assert.assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, testOrder.getStatus());
        Assert.assertEquals("주문한 상품 종류 수가 정확해야 한다.", 1, testOrder.getOrderItems().size());
        Assert.assertEquals("주문 가격은 가격 * 수량이다.", 10000*orderCount, testOrder.getTotalPrice());
        Assert.assertEquals("주문 수량만큼 재고가 줄어야 한다.", 8, book.getStockQuantity());

    }

    @Test
    public void 주문취소() throws Exception {
        // given
        Member member = createTestMember();
        Book item = createTestBook("jpa book", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // when
        orderService.cancelOrder(orderId);

        // then
        Order testOrder = orderRepository.findOne(orderId);
        Assert.assertEquals("주문 취소시 상태는 CANCEL이다.", OrderStatus.CANCEL, testOrder.getStatus());
        Assert.assertEquals("주문 취소된 상품은 재고가 복구되야 한다.", 10, item.getStockQuantity());

    }

   // Item 엔티티에서 removeStock에서 남은 수량이 음수가 되면 발생하기로 한 예외가 발생하는지 테스트
   @Test(expected = NotEnoughStockException.class)
    public void 상품주문_재고수량초과() throws Exception {


        // given
        Member member = createTestMember();
        Item item = createTestBook("jpa book", 10000, 10);

        // 재고 수량 보다 1개 더 많은 수량을 주문함 => 예외 발생해야 함
        int orderCount = 11;
        // when
        orderService.order(member.getId(), item.getId(), orderCount);
        // 위 라인에서 예외가 터져서 밑 라인까지 내려가면 안됨

        // then
        // 여기까지 오면 잘못된 거
        fail("재고 수량 부족 예외가 발생해야 한다.");

    }

    private Book createTestBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createTestMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "경기", "123-123"));
        em.persist(member);
        return member;
    }




}