package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import org.junit.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;


public class Helper {
    public static final String ENCODED_PASSWORD = "passwordEncoded";


    public static void injectObjects(Object target, String fieldName, Object toInject){
        boolean wasPrivate = false;

        try {
            Field field = target.getClass().getDeclaredField(fieldName);

            if(!field.isAccessible()){
                field.setAccessible(true);
                wasPrivate = true;
            }

            field.set(target, toInject);

            if(wasPrivate){
                field.setAccessible(false);
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static User getUser(){
        User user = new User();
        user.setId(0l);
        user.setCart(new Cart());
        user.setUsername("myuser");
        user.setPassword("mypassword");

        Cart cart  = new Cart();
        cart.setId(0l);
        cart.setUser(user);
        cart.setTotal(BigDecimal.ZERO);

        user.setCart(cart);

        return user;
    }


    public static Item getItem(){
        Item item = new Item();
        item.setId(0l);
        item.setName("Item square");
        item.setDescription("This is item 01");
        item.setPrice(BigDecimal.valueOf(100.00));
        return item;
    }


    public static List<Item> getItems(){
        Item item1 = new Item();
        item1.setId(0l);
        item1.setName("square widget");
        item1.setDescription("four sides widget");
        item1.setPrice(BigDecimal.valueOf(299.00));

        Item item2 = new Item();
        item2.setId(1l);
        item2.setName("triangle widget");
        item2.setDescription("three sides widget");
        item2.setPrice(BigDecimal.valueOf(50.55));

        Item item3 = new Item();
        item3.setId(2l);
        item3.setName("round widget");
        item3.setDescription("circle widget");
        item3.setPrice(BigDecimal.valueOf(150.00));

        Item item4 = new Item();
        item4.setId(3l);
        item4.setName("hexagon");
        item4.setDescription("six sides widget");
        item4.setPrice(BigDecimal.valueOf(9.99));

        return Arrays.asList(item1,item2,item3,item4);
    }




}
