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

        Book book = new Book();
        book.setId(form.getId());
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        // itemService save 호출 => itemRepository save 호출
        // itemRepository의 save에서 인자로 들어온 item 엔티티의 id 값이 null이 아니라면,
        // 새로운 엔티티가 아니라 이미 DB에 저장되어 id를 부여받은 엔티티  => 수정을 의미하므로, merge 수행
        itemService.saveItem(book);
        return "redirect:/items";



    }






}
