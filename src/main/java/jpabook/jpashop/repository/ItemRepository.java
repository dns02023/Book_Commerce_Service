package jpabook.jpashop.repository;


import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item){
        if(item.getId() == null){
            em.persist(item);
            // 새롭게 생성된 객체는 jpa에 저장하기 전까지는 id값이 존재하지 않으므로
            // id값이 null => persist 수행 (신규 등록)
        }
        else{
            em.merge(item);
            // 만약에 이미 DB에 존재하는 아이템을 등록하려고 한다면? => update와 유사한 merge 수행
        }
    }

    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    public List<Item> findAll(){
        return em.createQuery("select  i from Item i", Item.class)
                .getResultList();
    }



}
