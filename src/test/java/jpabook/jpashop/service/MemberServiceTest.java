package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;



@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional // 롤백 가능하게 해줌 (테스트에서는 기본적으로 롤백시킴) 같은 트랜잭션 내에서 PK 값이 같으면 같은 영속성 객체로 관리가 된다
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @Test
    // 롤백하지 않고 커밋하기
    //@Rollback(false)
    public void 회원가입() throws  Exception{
        // given
        Member member = new Member();
        member.setName("kim");

        // when
        Long saveId = memberService.join(member);

        // then
        assertEquals(member, memberRepository.findOne(saveId));
        // 객체랑 저장된 객체가 동일한 지 테스트 (잘 가입되는 지 확인)

    }

    @Test
    public void 중복_회원_예외() throws Exception{

    }

}