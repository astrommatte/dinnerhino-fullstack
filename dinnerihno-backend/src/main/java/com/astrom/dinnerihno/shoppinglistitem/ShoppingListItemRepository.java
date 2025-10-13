package com.astrom.dinnerihno.shoppinglistitem;

import com.astrom.dinnerihno.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItem, Long> {
    List<ShoppingListItem> findByUserUsername(String name);
    List<ShoppingListItem> findByUser(User user);
}
