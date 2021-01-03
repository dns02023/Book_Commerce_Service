package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;



// JPA의 데이터 변경과 로직들은 반드시 트랜잭션 내에서 실행되어야 함
// 조회 관련 메서드들이 대부분 이므로 readOnly를 설정해주지만, 가입과 같은 쓰기 메서드는 readOnly를 쓰면 안됨
// => 해당 메서드에 따로 트랜잭션 어노테이션을 넣어준다.
// @AllArgsConstructor // 이 어노테이션을 통해서 모든 필드값 설정하는 생성자 선언을 자동으로 해준다.(LOMBOK)
@RequiredArgsConstructor // final 인 필드값들만 설정하는 생성자 선언
@Service
@Transactional(readOnly = true)
public class MemberService {
    // 트랜잭션 어노테이션에 의해 public method들(가입, 조회 등)은 트랜잭션에 걸림

// 서비스는 레포지토리를 사용하므로, 주입해줘야 한다. 주입 방법 3가지 (필드, setter, 생성자)

//    // 1. 필드 주입 방법 => 단점 : 바꿀 수가 없음. 즉, test 등에서 바꿔가며 할 수가 없다.
//    @Autowired
//    private MemberRepository memberRepository;
//
//    // 2. setter 주입 방법 => 장점 : test 수행 시 가짜 레포지토리 주입 가능, 단점 : 실무에서는 이미 어플리케이션 조립이 끝난 후 setter를 쓸일이 없다.
//    // 또한 누군가가 새로 set을 해버리는 문제가 발생할 수도 있음
//    private MemberRepository memberRepository;
//    @Autowired
//    public void setMemberRepository(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    // 3. 의존관계 주입 권장 방법 : 생성자 주입
    // 장점 : 생성될때 주입되고, 이후 setter로 변경될 일이 없다.
    // 또한 test 수행 시, 주입해야 하므로, 의존관계를 명확히 알 수 있다.
    private final MemberRepository memberRepository; // 생성 후 변경할 일 없으므로 final로 처리
    // 또한 final처리해주면, 생성자 호출시 반드시 설정해줘야 하므로, 에러 캐치 가능
    // *** final 필드 : 필드값을 필드 선언시에 초기화하거나, 클래스 생성자에서 초기화를 반드시 해야함, 또한 한번 초기화되고 나면 실행 도중 수정 불가
    // https://kephilab.tistory.com/51

//    // lombok 어노테이션으로 지정하면 생성자 생략가능: RequiredArgsConstructor => final 필드 생성
//    @Autowired // 생략 가능, autowired 어노테이션 안해줘도, 생성자가 하나만 존재하는 경우 spring이 자동으로 주입해준다.
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }


    //회원 가입
    // 쓰기 메서드 이므로 readOnly = false가 디폴트인 트랜잭션 어노테이션 설정
    @Transactional
    public Long join(Member member){
        validateDuplicateMember(member); // 중복 회원 검증 로직
        memberRepository.save(member);
        return member.getId();
        // persist 확인
    }

    // 실무에서는 아래와 같은 검증 방법이 먹히지 않는다 => 동시에 멤버가 등록될 수 있으므로
    // 즉 DB에 어트리뷰트 유니크 제약 조건을 거는 것이 확실한 방법이다.
    private void validateDuplicateMember(Member member){
        // 중복회원이면 예외를 발생 (exception)
        // 같은 이름을 가진 회원 객체가 존재하는 지 찾기
        List<Member> findMembers = memberRepository.findByName(member.getName());
        // 검색했는데 이미 존재한다면
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체 조회
    //조회의 경우 옵션으로 readOnly를 주면 성능을 최적화할 수 있음
    // @Transactional(readOnly = true)
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    // 단건 조회
    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }


    // 수정같은 경우 굳이 수정된 Member 엔티티를 반환하지 않도록 한다. void로 마무리
    // 커맨드(update)와 쿼리(id로 엔티티 조회)를 분리??
    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id);
        // 트랜잭션 내에서 수행되므로 영속 상태 엔티티로 받음 => 변경 감지
        member.setName(name);
    }
}
