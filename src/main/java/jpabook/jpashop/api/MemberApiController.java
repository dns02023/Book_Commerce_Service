package jpabook.jpashop.api;


import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    // MVC Controller와 RestController의 차이?? (메서드 반환값 타입 등)



    // api 공통 흐름: request => 비즈니스 로직 => response
    // RESTful하게 개발하기

    // 조회(GET)

    // 전체 회원 조회
    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        // 단순히 엔티티 리스트를 담아서 응답
        // 문제점: Member 엔티티들이 가지고 있는 모든 정보들이 외부로 노출됨
        // => JsonIgnore로 해결 가능하지만 다른 문제가 여전히 존재함: 엔티티와 api 스펙 간 서로 영향을 주고 받게 됨
        // 또한 응답을 리스트 형태로 반환하므로, api 응답 스펙 확장이 힘들다.
        // [객체json, 객체json, ...]
        // 스펙 확장이 가능하려면, { "data": [객체json, 객체json, ...], "count": ... } 형식이 되야 함.
        // => DTO를 통해 응답하면서, DTO 필드 추가를 통해 api 스펙이 확장될 수 있어야 한다.
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result memberV2() {
        // DB에서 먼저 회원 엔티티들을 받고, => 하나 씩 회원 DTO에 담기 => 회원 DTO 리스트 만들기
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());
        // collect(회원 DTO 리스트)는 현재 리스트형이므로, 이것을 바로 응답하지 말고, 또 다른 응답 DTO로 감싸 줘야 함. => { "data": [...] } 형식
        return new Result(collect);
    }

    // 등록(POST)

    // 방법1: 엔티티를 직접 인자로 받아서 바인딩, 노출됨
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        // RequestBody 어노테이션: json 형태로 온 request의 body를 Member로 매핑
        // Valid 어노테이션: 받은 객체에 대한 엔티티의 javax validation 수행 (NotEmpty 등)
        // 현재 문제점: 엔티티를 직접 바인딩 => 화면에서 받는 검증 로직이 엔티티에 영향을 주고 있음.
        // 또한 반대로 엔티티 수정에 의해 api 스펙이 변할 수 있음
        // 또한 엔티티의 속성들 만으로는 요청에 어떤 인자들이 담겨져 있는지, 즉 api 요청 스펙을 추측하기 어렵다.
        // => 엔티티 자체와 api 스펙과의 서로 영향을 주면 안된다.
        Long id = memberService.join(member);
        // 요청에 의해 생성된 회원 엔티티의 데이터를 담아서 응답
        // Postman으로 응답을 확인해보자.
        return new CreateMemberResponse(id);
    }

    // 방법2: 별도의 data transformation object로 데이터 받기
    // => 즉, request, response 둘다 별도의 객체로 이루어짐 (요청 => request DTO => 비즈니스 로직 => response DTO => 응답)
    // => 엔티티와 api 스펙이 서로 영향을 주지 않는다.
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    // 수정(PUT)

    // 요청 => 수정 request DTO => 비즈니스 로직 => 수정 response DTO => 응답
    // 위의 등록과 수정은 일반적으로 api 스펙이 다르기 때문에 다른 DTO를 정의해서 쓰는 게 좋음
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request) {

        // 커맨드(update), 쿼리(findOne으로 엔티티 조회)를 분리해서 수행
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        // 수정된 결과를 조회하고, DTO 생성 후, 담아서 응답
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    // 요청에 담긴 데이터를 받을 request DTO, 응답에 데이터를 담기 위한 response DTO 정의
    // DTO를 통해서 api 스펙을 명시할 수 있음. + DTO 필드 추가를 통해 api 스펙을 확장시킬 수 있음.

    // 회원
    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    // 조회
    @Data
    @AllArgsConstructor
    static class Result<T> {
        // Generic 타입
        private T data;
    }

    // 수정
    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    // api 스펙에 따른 응답을 위해서 DTO 생성자 별도 정의 필요
    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    // 등록
   @Data
    static class CreateMemberRequest {
        // javax validation을 엔티티 자체에 적용하지 않고, DTO에 적용할 수 있음.
        @NotEmpty
        private String name;
    }

    @Data
    static class CreateMemberResponse{
        private Long id;
        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }



}
