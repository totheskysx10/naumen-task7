package shopping;

import customer.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import product.Product;
import product.ProductDao;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Тесты ShoppingService
 */
public class ShoppingServiceTest {

    private final ProductDao productDaoMock = Mockito.mock(ProductDao.class);

    private final ShoppingService shoppingService = new ShoppingServiceImpl(productDaoMock);

    /**
     * Покупатель
     */
    private Customer customer;

    /**
     * Товар 1
     */
    private Product product1;

    /**
     * Товар 2
     */
    private Product product2;

    /**
     * Перед каждым тестом создаёт покупателя и два товара разного количества
     */
    @BeforeEach
    void setup() {
        customer = new Customer(1, "123");
        product1 = new Product("p1", 3);
        product2 = new Product("p2", 6);
    }

    /**
     * Тест получения корзины пользователя
     */
    @Test
    void getCartTest() {
        Cart cart = shoppingService.getCart(customer);
        assertNotNull(cart);
    }

    /**
     * Тест получения корзины пользователя, если пользователь null
     */
    @Test
    void getCartTestCustomerNull() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> shoppingService.getCart(null));

        Assertions.assertEquals("Невозможно получить корзину для пользователя null", e.getMessage());
    }

    /**
     * Тест получения всех товаров (ассортимента)
     */
    @Test
    void getAllProductsTest() {
        List<Product> products = List.of(product1, product2);

        Mockito.when(productDaoMock.getAll()).thenReturn(products);

        Assertions.assertEquals(products, shoppingService.getAllProducts());
    }

    /**
     * Тест получения товара по наименованию
     */
    @Test
    void getProductByNameTest() {
        Mockito.when(productDaoMock.getByName(Mockito.eq("p1"))).thenReturn(product1);

        Assertions.assertEquals(product1, shoppingService.getProductByName("p1"));
    }

    /**
     * Тест получения товара по наименованию, если из БД прилетело null (нет товара)
     * Проверяет, что выкидывается NoProductFoundException (должно быть проверяемым)
     */
    @Test
    void getProductByNameNoProductTest() {
        Mockito.when(productDaoMock.getByName(Mockito.eq("p3"))).thenReturn(null);

        Exception e = Assertions.assertThrows(NoProductFoundException.class, () -> shoppingService.getProductByName("p3"));

        Assertions.assertEquals("Товар с именем 'p3' не найден", e.getMessage());
    }

    /**
     * Тест покупки при успешной покупке
     */
    @Test
    void buyTestSuccess() throws BuyException {
        Cart cart = new Cart(customer);
        cart.add(product1, 2);
        cart.add(product2, 2);

        Mockito.when(productDaoMock.save(product1)).thenReturn(true);
        Mockito.when(productDaoMock.save(product2)).thenReturn(true);

        Assertions.assertTrue(shoppingService.buy(cart));
    }

    /**
     * Тест покупки при пустой корзине
     */
    @Test
    void buyTestEmptyCart() throws BuyException {
        Cart cart = new Cart(customer);

        Assertions.assertFalse(shoppingService.buy(cart));
    }

    /**
     * Тест покупки если положить в корзину больше товара чем доступно
     */
    @Test
    void buyTestMoreProducts() {
        Cart cart = new Cart(customer);

        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> cart.add(product1, 10));

        Assertions.assertEquals("Невозможно добавить товар 'p1' в корзину, т.к. нет необходимого количества товаров", e.getMessage());
    }

    /**
     * Тест покупки без корзины
     */
    @Test
    void buyTestWithoutCart() throws BuyException {
        Cart cart = null;
        Assertions.assertFalse(shoppingService.buy(cart));
    }
}
