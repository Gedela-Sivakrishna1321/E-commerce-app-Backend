package com.ecommerce.Service;

import org.hibernate.query.NativeQuery.ReturnableResultNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.Exceptions.ProductException;
import com.ecommerce.Exceptions.UserException;
import com.ecommerce.Models.Cart;
import com.ecommerce.Models.CartItem;
import com.ecommerce.Models.Product;
import com.ecommerce.Models.User;
import com.ecommerce.Repository.CartRepository;
import com.ecommerce.Request.AddItemRequest;

@Service
public class CartServiceImplementation implements CartService {
	
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private CartItemService cartItemService;
	@Autowired
	private ProductService productService;
	@Autowired
	private UserService userService;

	@Override
	public Cart createCart(User user) {
		
		Cart cart = new Cart();
		cart.setUser(user);
		return cartRepository.save(cart);
	}

	@Override
	public String addCartItem(Long userId, AddItemRequest req) throws ProductException {
			
		Cart cart = cartRepository.findCartByUserId(userId);
		Product product = productService.findProductById(req.getProductId());
		
		System.out.println("C H E C K - 2");
		CartItem isCartItemPresent = cartItemService.isCartItemExist(cart, product, req.getSize(), userId);
		System.out.println("C H E C K - 4");
		
		if(isCartItemPresent == null) {
			System.out.println(" C H E C K _ 5");
			CartItem cartItem = new CartItem();
			cartItem.setProduct(product);
			cartItem.setCart(cart);
			cartItem.setQuantity(req.getQuantity());
			cartItem.setUserId(userId);
			
			int price = cartItem.getQuantity() * product.getDiscountedPrice();
			cartItem.setPrice(price);
			cartItem.setSize(req.getSize());
			
			CartItem createdCartItem = cartItemService.createCartItem(cartItem);
			cart.getCartItems().add(createdCartItem);
		
			System.out.println("Item Added To Cart Successfully .........");
			
			return "Item Added To Cart";
		}
		
		System.out.println(" C H E C K - 6");
		return "Item Already Exist in the cart";
		
	}

	@Override
	public Cart findUserCart(Long userId) throws UserException {
		
		Cart cart = cartRepository.findCartByUserId(userId);
		
		// Update the total cumilative price of all the items in the cart
		int totalPrice = 0;
		int totalDiscountedPrice = 0;
		int totalItem = 0;
		
		if(cart == null) {
			System.out.println("User cart is empty and creating cart for user .. ");
			User user = userService.findUserById(userId);
			cart = createCart(user);
		}
		
		if(cart.getCartItems().size() != 0) {
		
			for(CartItem cartItem : cart.getCartItems()) {
				totalPrice = totalPrice + cartItem.getPrice();
				totalDiscountedPrice = totalDiscountedPrice + cartItem.getDiscountedPrice();
				totalItem = totalItem + cartItem.getQuantity();
			}
			
			cart.setTotalItem(totalItem);
			cart.setTotalPrice(totalPrice);
			cart.setTotalDiscountedPrice(totalDiscountedPrice);
			int discountAmount = (totalPrice - totalDiscountedPrice);
			double discount = ((double)discountAmount / totalPrice) * 100;
//			System.out.println(" D IS C OUNT - " + discount);
//			System.out.println("Total Price - " + totalPrice);
//			System.out.println("Total Discounted Price " + totalDiscountedPrice);
//			System.out.println("Discount amount - " + discountAmount);
//			System.out.println("Discount Percentage - " + discount );
			cart.setDiscount((int)discount); 
			
		}
		
		
		return cartRepository.save(cart);
	}

}
