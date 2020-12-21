package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        // model: controller에서 view로 넘어갈 때, 데이터를 넘겨주는 객체 (django의 context)
        model.addAttribute("memberForm", new MemberForm());
        // empty여부 체크 등을 위해 빈 폼 객체를 넘겨준다.
        // 렌더링
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm memberForm, BindingResult result) {
        // @Valid를 통해서 form의 NotEmpty 등의 제약 조건을 검증한다.
        // validation의 결과, error가 있다면, BindingResult 객체에 에러를 담는다.
        if (result.hasErrors()) {
            // 타임리프와 스프링의 연동을 통해서 에러에 대한 정보를 폼에 담아서 다시 폼을 렌더링
            return "members/createMemberForm";
        }

        Address address = new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode());

        Member member = new Member();
        member.setName(memberForm.getName());
        member.setAddress(address);

        memberService.join(member);
        // home으로 리다이렉트
        return "redirect:/";

    }
    
}
