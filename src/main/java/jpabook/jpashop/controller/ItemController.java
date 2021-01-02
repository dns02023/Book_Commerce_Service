package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // items/new의 get 방식: return => 폼 화면 띄워주기
    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    // items/new의 post 방식: 생성 로직 수행 후 return => 홈으로 redirect
    @PostMapping("/items/new")
    public String create(BookForm form) {
        Book book = new Book();
        // form을 통해 받은 데이터로 Book 엔티티 생성
        // 사실 setter를 통해서 생성 로직들을 수행하는 것은 바람직 하지 않다.
        // => Order와 같이 별도의 생성 메서드를 정의해서 사용하는 것이 깔끔하다.
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/";
    }

    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    // 수정 (생성과 유사한 흐름, get: 폼 띄우고 => post)
    // edit의 get 방식: return => 기존 데이터를 담은 수정 폼 화면 띄워주기
    // 가변적인 url에 대해서는 path variable
    @GetMapping("items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {

        // 간단하게 책만 받는 다고 설정 => 캐스팅
        Book item = (Book) itemService.findOne(itemId);

        // 수정할 때, 해당하는 Book 엔티티를 보내는 것이 아니라, Book 폼에 해당 엔티티의 데이터를 담아서 보낼 것
        // createForm과의 차이점은 이미 등록된 정보가 있으므로, 이것을 model에 담아 띄워 주는 것
        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    @PostMapping("items/{itemId}/edit")
    public String updateItem(@PathVariable("itemId") Long itemId, @ModelAttribute("form") BookForm form) {

//        Book book = new Book();
//        book.setId(form.getId());
//        book.setName(form.getName());
//        book.setPrice(form.getPrice());
//        book.setStockQuantity(form.getStockQuantity());
//        book.setAuthor(form.getAuthor());
//        book.setIsbn(form.getIsbn());

//        itemService.saveItem(book);

        // itemService save 호출 => itemRepository save 호출
        // itemRepository의 save에서 인자로 들어온 item 엔티티의 id 값이 null이 아니라면,
        // 새로운 엔티티가 아니라 이미 DB에 저장되어 id를 부여받은 엔티티  => 수정을 의미하므로, merge 수행

        // 위의 book과 같이 한번 DB에 저장되어서 id값이 부여되었던 엔티티를 준영속 상태 객체라고 한다.
        // 영속 상태 엔티티는 jpa가 변경 감지를 하면서 수정이 가능하지만, 준영속 상태 엔티티는 영속 엔티티 처럼 수정을 해도 DB에 수정이 되지 않는다.
        // 그래서 준영속 상태 엔티티를 수정하는 2가지 방법을 사용하여야 함. 그 중 하나가 위와 같이 merge인 것

        // merge를 사용할 때 위험한 점은 어떤 속성에 대해서 수정에 제약을 거는 로직을 구현할 때임
        // 즉, 위 setter들 중에서 수정이 제약된 속성의 setter를 생략하면, 원래 의도대로 그 속성을 수정 안하는 것이 아니라, 그 속성이 null로 수정되버린다.
        // => 즉, 변경감지처럼 하는 기법을 통해서 수정을 허용할 속성들에 대해서만 set을 해야 한다. => merge는 쓰지 말자.

        // 또한 위와 같이 어설프게 엔티티를 만들지 말자. 서비스에서 정의해둔 영속 상태 엔티티 변경 감지 수정 메서드를 사용하자.
        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());
        return "redirect:/items";



    }






}
