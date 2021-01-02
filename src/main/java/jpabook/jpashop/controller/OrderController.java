package jpabook.jpashop.controller;


import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/order")
    public String creataForm(Model model) {

        // 어떤 회원이 어떤 아이템을 주문하는 지 선택해야 하므로 먼저 전체 리스트 가져오기
        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        // 리스트 안에서 선택할 수 있도록 폼에 리스트들을 담아서 화면에 넘겨주기
        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";



    }

    // get 방식에서 작성한 form을 submit 했을 때, 담겨져 있는 parameter들의 html name을 명시해준다. (RequestParam)
    // => form의 해당 html name의 parameter들을 각각 클래스 parameter 변수들에 바인딩
    @PostMapping("/order")
    public String order(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long itemId,
                        @RequestParam("count") int count) {

        // controller에서 엔티티를 찾아서 처리하는 것이 아니라, 서비스의 메서드를 호출하는 방식으로 구현하는 것이 깔끔하다.
        // 또한 서비스에서 트랜잭션 내에서 비즈니스 로직을 수행해야 영속 상태로 엔티티를 다룰 수 있으므로 편리하다.
        // 만약 contoller에서 엔티티를 생성하면 그것은 영속 상태가 아니므로, 그것을 서비스에 보내도 jpa로 처리하기가 까다로워 지므로 하지 말자.
        // controller는 식별자만 념겨주고, 비즈니스 로직은 다른 곳에서 처리하도록 하자.
        orderService.order(memberId, itemId, count);
        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model) {
        // ModelAttribute 어노테이션으로 처리한 엔티티는 바로 model의 attribute에 담겨진다.
        // 검색을 누르면 폼이 submit 되면서 orderSearch 클래스의 name, status 속성값들이 바인딩되서 넘어온다.
        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);

        return "order/orderList";

    }

    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId) {
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }




}
