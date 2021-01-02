package jpabook.jpashop.service;


import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    // item service의 경우, 이미 item repository에서 구현해놓은 기능들을 위임만 하는 수준
    // 이럴 경우, controller에서 바로 item repository를 호출하는 것을 생각해볼수도 있다.


    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    // 준영속 상태 엔티티에 대한 수정을 위한 방법: 영속 상태 엔티티에 대한 jpa의 변경 감지 수정처럼 수행하기
    // 트랜잭션 어노테이션에 의해 트랜잭션 commit이 발생 => jpa가 플러시 수행 중에 영속 상태 엔티티 중에 변경된 엔티티들을 감지한다.
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {
        // parameter들은 수정을 위한 정보를 가지고 있음. 만약 parameter가 많으면 Dto에 정보를 담아서 넘겨도 된다.
        // 실제 DB에서 영속 상태 엔티티를 가져오고 setter로 수정
        Item findItem = itemRepository.findOne(itemId);
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);
        // 수정을 허용할 속성에 대해서 setter 호출
        // 하지만 setter들을 호출하지 말고, 엔티티에서 별도의 수정 메서드를 정의해서 사용하는 것이 좋다. => 응집도 강화

        // 위와 같이 setter를 수행하면, 영속 상태 엔티티를 jpa의 변경 감지를 통해 간단하게 DB에 수정을 반영하는 방식으로 구현 가능
        // 즉, 별도의 repository save 등을 호출할 필요가 없음

    }
    // merge 방식도 결국 위와 같은 코드를 실행하는 것과 동일하다. 하지만 차이점도 존재한다. => 변경 감지를 쓰자.

    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }




}
