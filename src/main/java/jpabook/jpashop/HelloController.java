package jpabook.jpashop;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    // 정적 페이지는 static에서 관리, 렌더링되어야 하는 것은 templates에서 관리



    @GetMapping("hello")
    public String hello(Model model){
        // Model: 데이터를 실어서 뷰에 넘길 수 있다.
        model.addAttribute("data", "hello!");
        // 장고 context와 유사함, 뷰 템플릿에 data 값을 보냄
        return "hello";
        // .html이 생략되어 있음 hello.html로 보내주는 것
    }
}
