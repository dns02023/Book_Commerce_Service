package jpabook.jpashop.repository;


import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

// component scan에 의해 spring bean으로 자동 관리
@Repository
@RequiredArgsConstructor
public class MemberRepository {

    // entity manager 주입 두가지 방법 : persist context 어노테이션 / 생성자 주입

//    // 이 어노테이션에 의해 spring이 em을 만들어서 주입해준다.
//    @PersistenceContext
//    private EntityManager em;

    // 스프링 부트에서는 jpa를 사용할 때 이런식으로 entity manager를 autowired로 주입해줄 수도 있다.(스프링 부트와 스프링 데이터 jpa하에서 가능)
    // 원래는 PersistenceContext 어노테이션 처리 해주는 게 원칙
    // @Autowired // 생략
    private final EntityManager em;

    // lombok 으로 생략 가능
//    public MemberRepository(EntityManager em) {
//        this.em = em;
//    }

    public void save(Member member){
        em.persist(member);
        // persist : 영속성 컨텍스트에 회원 객체를 넣는다 => 트랜잭션 커밋 시점에 DB에 반영(insert query)
    }

    public Member findOne(Long id){
        return em.find(Member.class, id);
        // find : 단건 조회 (타입, PK)
    }

    // 회원 목록 등의 기능을 위해서 리스트로 조회도 가능해야 함함
    public List<Member> findAll(){
        // jpql 작성, (쿼리, 반환타입)
        // jpql 과 sql의 차이점 : sql은 테이블을 FROM 대상으로 쿼리 vs jpql은 객체를 FROM 대상으로 쿼리
        // Member 객체의 별칭을 m으로 하여 조회
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                // 입력으로 받은 매개변수를 쿼리에 바인딩해준다.
                .getResultList();
    }





}
