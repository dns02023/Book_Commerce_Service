package jpabook.jpashop.service;


import jpabook.jpashop.domain.item.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemUpdateTest {

    @Autowired
    EntityManager em;

    @Test
    public void updateTest() throws Exception {
        // 수정할 엔티티 가져오기
        Book book = em.find(Book.class, 1L);

        // 트랜잭션 내에서 수정
        book.setName("testname");
        // => jpa가 변경을 감지해서 수정해준다. (update query 날림) : dirty checking
        // 변경감지 == dirty checking

        // 유사한 로직은 주문 취소할 때
        // 주문 취소 시에 주문 상태를 setStatus로 바꾸어 주면, em의 메서드 호출 없이 수정이 가능했음.
        // 즉, jpa가 변경을 감지해서 알아서 em의 메서드 호출 없이 트랜잭션 commit 시점에 update query날리고 최종 commit한다.
        // 즉, 플러시할 때 dirty checking 발생

        // 트랜잭션 commit


    }
}
