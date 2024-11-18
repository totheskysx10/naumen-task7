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
     * Товар 3
     */
    private Product product3;

    /**
     * Перед каждым тестом создаёт покупателя и два товара разного количества
     */
    @BeforeEach
    void setup() {
        customer = new Customer(1, "123");
        product1 = new Product("p1", 3);
        product2 = new Product("p2", 6);
        product3 = new Product("p3", 0);
    }

    /**
     * Тест взятия корзины пользователем
     */
    @Test
    void getCartTest() {
        Cart cart = shoppingService.getCart(customer);
        assertNotNull(cart);
    }

    /**
     * Тест взятия корзины пользователем, если пользователь null
     */
    @Test
    void getCartTestCustomerNull() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> shoppingService.getCart(null));

        Assertions.assertEquals("Невозможно получить корзину для пользователя null", e.getMessage());
    }

    /**
     * Тест получения всех товаров (ассортимента)
     * Нет реализации, так как в тестируемом методе нет логики.
     * Если нужно протестировать, логичнее будет сделать тесты на вызываемый внутри тестируемого метод
     */
    @Test
    void getAllProductsTest() {

    }

    /**
     * Тест получения товара по наименованию
     * Нет реализации, так как в тестируемом методе нет логики.
     * Если нужно протестировать, логичнее будет сделать тесты на вызываемый внутри тестируемого метод
     */
    @Test
    void getProductByNameTest() {

    }

    /**
     * Тест покупки при успешной покупке
     */
    @Test
    void buyTestSuccess() throws BuyException {
        Cart cart = new Cart(customer);
        cart.add(product1, 2);
        cart.add(product2, 5);

        Assertions.assertTrue(shoppingService.buy(cart));
        Mockito.verify(productDaoMock).save(new Product("p1", 1));
        Mockito.verify(productDaoMock).save(new Product("p2", 1));
        Assertions.assertTrue(cart.getProducts().isEmpty());
    }

    /**
     * Тест покупки всего количества товара
     */
    @Test
    void buyTestAll() throws BuyException {
        Cart cart = new Cart(customer);
        cart.add(product1, 3);

        Assertions.assertTrue(shoppingService.buy(cart));
        Mockito.verify(productDaoMock).save(new Product("p1", 0));
        Assertions.assertTrue(cart.getProducts().isEmpty());
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
    void buyTestAddMoreProducts() {
        Cart cart = new Cart(customer);

        Exception e1 = Assertions.assertThrows(IllegalArgumentException.class, () -> cart.add(product1, 10));
        Assertions.assertEquals("Невозможно добавить товар 'p1' в корзину, т.к. нет необходимого количества товаров", e1.getMessage());

        Exception e2 = Assertions.assertThrows(IllegalArgumentException.class, () -> cart.add(product3, 1));
        Assertions.assertEquals("Невозможно добавить товар 'p3' в корзину, т.к. нет необходимого количества товаров", e2.getMessage());
    }

    /**
     * Тест покупки если пытаться купить больше товара чем доступно
     * Здесь проверяю выброс только исключения для p3, ведь метод после этого прекратит выполнение
     */
    @Test
    void buyTestBuyMoreProducts() {
        Cart cart = new Cart(customer);
        cart.add(product3, 1);
        cart.add(product1, 3);

        Exception e = Assertions.assertThrows(BuyException.class, () -> shoppingService.buy(cart));
        Assertions.assertEquals("В наличии нет необходимого количества товара 'p3'", e.getMessage());
    }

    /**
     * Тест покупки при некорректном количестве товара (ноль, меньше нуля)
     */
    @Test
    void buyTestInvalidProductsQuantity() {
        Cart cart = new Cart(customer);

        Exception e1 = Assertions.assertThrows(IllegalArgumentException.class, () -> cart.add(product1, -1));
        Assertions.assertEquals("Количество товара должно быть положительным", e1.getMessage());

        Exception e2 = Assertions.assertThrows(IllegalArgumentException.class, () -> cart.add(product1, 1));
        Assertions.assertEquals("Количество товара должно быть положительным", e2.getMessage());
    }

    /**
     * Тест покупки при дробном количестве товара
     * Тест не реализован (согласно требованиям задачи оставлено пустое тело метода),
     * так при реализации код не запустится из-за несоответствия дробного числа
     * типу int (а изменять основной код запрещено)
     */
    @Test
    void buyTestDoubleProductsQuantity() {

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
