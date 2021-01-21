package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.controllers.ItemController;
import com.example.demo.controllers.OrderController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.hibernate.criterion.Order;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestRestController {

    @Autowired
    private MockMvc mvc;

    private UserController userController;
    private CartController cartController;
    private ItemController itemController;
    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @BeforeClass
    static public void setup(){
        System.out.println("Setup TestRestController");
    }

    @Before
    public void init(){
        System.out.println("init controllers and repos");
        this.userController = new UserController();
        this.itemController = new ItemController();
        this.cartController = new CartController();
        this.orderController = new OrderController();

        //setup userController
        Helper.injectObjects(userController, "userRepository", userRepository);
        Helper.injectObjects(userController, "cartRepository", cartRepository);
        Helper.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);

        //setup itemController
        Helper.injectObjects(itemController, "itemRepository", itemRepository);

        //setuo cartController
        Helper.injectObjects(cartController, "userRepository", userRepository);
        Helper.injectObjects(cartController, "cartRepository", cartRepository);
        Helper.injectObjects(cartController, "itemRepository", itemRepository);


        //setuo orderController
        Helper.injectObjects(orderController, "userRepository", userRepository);
        Helper.injectObjects(orderController, "orderRepository", orderRepository);

    }

    @AfterClass
    static public void tearDown(){
        System.out.println("tearDown TestRestController...");
    }


    @After
    public void initEnd(){
        System.out.println("next test");
    }



    @Test
    public void verify_createUser(){
        System.out.println("verify_createUser");
        User userToCreate = Helper.getUser();
        when( bCryptPasswordEncoder.encode(userToCreate.getPassword()) ).thenReturn(Helper.ENCODED_PASSWORD);
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(userToCreate.getUsername());
        request.setPassword(userToCreate.getPassword());
        request.setConfirmPassword(userToCreate.getPassword());

        ResponseEntity<User> response = userController.createUser(request);
        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        User userCreated = response.getBody();
        Assert.assertEquals( userToCreate.getUsername(), userCreated.getUsername() );
        Assert.assertEquals( Helper.ENCODED_PASSWORD, userCreated.getPassword() );

    }




    @Test
    public void verify_getUserByUsername(){
        System.out.println("verify_getUserByUsername");
        User userToFind = Helper.getUser();
        when(userRepository.findByUsername(userToFind.getUsername())).thenReturn(userToFind);
        ResponseEntity<User> responseFind = userController.findByUserName(userToFind.getUsername());
        Assert.assertNotNull(responseFind);
        Assert.assertEquals(HttpStatus.OK, responseFind.getStatusCode());
        User userCreated = responseFind.getBody();
        Assert.assertEquals( userToFind.getUsername(), userCreated.getUsername() );

    }


    @Test
    public void verify_getUserById(){
        System.out.println("verify_getUserById");
        User userToFind = Helper.getUser();
        Optional<User> optionalUser =Optional.of(userToFind);
        when(userRepository.findById(userToFind.getId())).thenReturn(optionalUser);
        ResponseEntity<User> responseFind = userController.findById(userToFind.getId());
        Assert.assertNotNull(responseFind);
        Assert.assertEquals(HttpStatus.OK, responseFind.getStatusCode());
        User userCreated = responseFind.getBody();
        Assert.assertEquals( userToFind.getUsername(), userCreated.getUsername() );
    }

    @Test
    public void verify_ItemFindByName(){
        System.out.println("verify_ItemFindByName");
        Item item = Helper.getItem();
        List<Item> itemsFound = Arrays.asList(item);
        when( itemRepository.findByName(item.getName()) ).thenReturn(itemsFound);
        ResponseEntity<List<Item>> response = itemController.getItemsByName(item.getName());
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Item> itemsWithName = response.getBody();
        Assert.assertArrayEquals(  itemsFound.toArray(), itemsWithName.toArray()  );
    }

    @Test
    public void verify_ItemFindById(){
        System.out.println("verify_ItemFindById");
        Item item = Helper.getItem();
        Optional<Item> optionalItem = Optional.of(item);
        when( itemRepository.findById(item.getId()) ).thenReturn(optionalItem);
        ResponseEntity<Item> response = itemController.getItemById(item.getId());
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Item itemsWithId = response.getBody();
        Assert.assertEquals(  item.getName() , itemsWithId.getName()  );
        Assert.assertEquals(  item.getDescription() , itemsWithId.getDescription()  );
        Assert.assertEquals(  item.getPrice() , itemsWithId.getPrice()  );
    }

    @Test
    public void verify_itemsListAll(){
        System.out.println("verify_itemsListAll");
        List<Item> items = Helper.getItems();
        when( itemRepository.findAll()).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItems();
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Item> itemsOnDb = response.getBody();
        Assert.assertArrayEquals(  items.toArray(), itemsOnDb.toArray()  );
    }



    @Test
    public void verify_addToCart(){
        System.out.println("verify_addToCart");
        Item itemToAdd = Helper.getItem();
        User user = Helper.getUser();

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(itemToAdd.getId());
        request.setQuantity(2);

        Optional<Item> optionalItem = Optional.of(itemToAdd);
        when( userRepository.findByUsername(  request.getUsername()  )).thenReturn(user);
        when( itemRepository.findById(  request.getItemId()  )).thenReturn(optionalItem);

        ResponseEntity<Cart> response = cartController.addTocart(request);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Cart cart = response.getBody();
        Assert.assertEquals( 2 , cart.getItems().size() );
        Assert.assertEquals( itemToAdd.getName() , cart.getItems().get(0).getName() );

    }

    @Test
    public void verify_removeFromCart(){
        System.out.println("verify_removeFromCart");

        List<Item> itemsOnCart = Helper.getItems();
        User user = Helper.getUser();
        Cart userCart = user.getCart();
        for(Item item: itemsOnCart){
            userCart.addItem( item );
            userCart.getTotal().add( item.getPrice() );
        }

        Item itemToRemove = itemsOnCart.get(0);
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(itemToRemove.getId());
        request.setQuantity(1);

        Cart cartItemRemoved = new Cart();
        cartItemRemoved.setId(userCart.getId());
        cartItemRemoved.setTotal(userCart.getTotal());
        cartItemRemoved.setUser(user);
        cartItemRemoved.setItems(userCart.getItems());
        cartItemRemoved.setTotal(userCart.getTotal());

        cartItemRemoved.removeItem(itemToRemove);

        Optional<Item> optionalItem = Optional.of(itemToRemove);
        when( userRepository.findByUsername(  request.getUsername()  )).thenReturn(user);
        when( itemRepository.findById(  request.getItemId()  )).thenReturn(optionalItem);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Cart cart = response.getBody();
        Assert.assertEquals( cartItemRemoved.getItems().size(), cart.getItems().size() );
        Assert.assertArrayEquals( cartItemRemoved.getItems().toArray(), cart.getItems().toArray() );
        Assert.assertEquals( cartItemRemoved.getTotal(), cart.getTotal() );

    }

    @Test
    public void verify_submitOrder(){
        System.out.println("verify_submitOrder");
        List<Item> itemsOnCart = Helper.getItems();
        User user = Helper.getUser();
        Cart userCart = user.getCart();
        for(Item item: itemsOnCart){
            userCart.addItem( item );
            userCart.getTotal().add( item.getPrice() );
        }
        user.setCart(userCart);
        userCart.setUser(user);
        when( userRepository.findByUsername(  user.getUsername()  )).thenReturn(user);
        ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        UserOrder userOrder = response.getBody();
        Assert.assertArrayEquals( userCart.getItems().toArray() ,  userOrder.getItems().toArray() );
        Assert.assertEquals( userCart.getTotal(), userOrder.getTotal() );
    }

    @Test
    public void verify_orderHistory(){
        System.out.println("verify_orderHistory");
        List<Item> items = Helper.getItems();
        User user = Helper.getUser();
        UserOrder orderToRetrieve = new UserOrder();
        orderToRetrieve.setItems(items);
        orderToRetrieve.setTotal( items.stream().map(Item::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add) );
        orderToRetrieve.setUser(user);
        orderToRetrieve.setId(0l);


        when( userRepository.findByUsername(  user.getUsername()  )).thenReturn(user);
        when( orderRepository.findByUser(  user )).thenReturn( Arrays.asList( orderToRetrieve) );

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        List<UserOrder> userOrders = response.getBody();
        Assert.assertArrayEquals( orderToRetrieve.getItems().toArray(), userOrders.get(0).getItems().toArray()  );
        Assert.assertEquals(  orderToRetrieve.getTotal(), userOrders.get(0).getTotal()  );
        Assert.assertEquals(  orderToRetrieve.getUser().getUsername(), userOrders.get(0).getUser().getUsername()  );
    }

}
