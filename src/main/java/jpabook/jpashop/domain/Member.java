package jpabook.jpashop.domain;



import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    //칼럼명은 member_id로 설정정

   private String name;

    //내장 타입을 포함하였다.
    @Embedded
    private Address address;

    //회원에게 주문은 일대다 관계(서로 반대), (한명의 회원이 여러 주문을 할 수 있으므로)
    // 연관관계의 주인은 order이다. => 즉, order 테이블에 있는 member 필드에 의해 매핑된다는 것을 의미
    // mappedBy 이므로, 연관관계의 주인이 아니라 거울임을 의미
    // 즉 이 필드에 값을 추가해도 order의 외래키 값이 변경되지 않는다.
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
    // 리스트형 같이 컬렉션 필드값들은 어떻게 정의해야할까?
    // 생성자에서 초기화 vs 필드에서 바로 초기화 => 필드에서 바로 초기화하는 것이 null문제에서 안전하다.
    // 또한 한번 필드에서 초기화한 후 수정하거나 건들지를 말자.
//    public Member(){
//        this.orders = new ArrayList<>();
//    }











}
