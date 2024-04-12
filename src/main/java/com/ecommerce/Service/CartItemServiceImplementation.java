package com.ecommerce.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.ecommerce.Exceptions.UserException;
import com.ecommerce.Exceptions.cartItemException;
import com.ecommerce.Models.Cart;
import com.ecommerce.Models.CartItem;
import com.ecommerce.Models.Product;
import com.ecommerce.Models.User;
import com.ecommerce.Repository.CartItemRepository;
import com.ecommerce.Repository.CartRepository;

@Service
public class CartItemServiceImplementation implements CartItemService {
	
	@Autowired
	private CartItemRepository cartItemRepository;
	@Autowired
	private UserService userService;
	
	@Autowired
	@Lazy
	private CartService cartService;
	
	@Autowired
	private CartRepository cartRepository;

	@Override
	public CartItem createCartItem(CartItem cartItem) {
		
		cartItem.setQuantity(1);
		cartItem.setPrice(cartItem.getProduct().getPrice()*cartItem.getQuantity());
		cartItem.setDiscountedPrice(cartItem.getProduct().getDiscountedPrice()*cartItem.getQuantity());
		
		CartItem createdCartItem = cartItemRepository.save(cartItem);
		System.out.println("Successfully added the product to the cart .........................");
		return createdCartItem;
	}

	@Override
	public CartItem updateCartItem(Long userId, Long id, CartItem cartItem) throws cartItemException, UserException {
		
		CartItem item = findCartItemById(id);
		User user = userService.findUserById(item.getUserId());
		
		if(user.getId().equals(userId)) {
			item.setQuantity(cartItem.getQuantity());
			item.setPrice(cartItem.getQuantity()*item.getProduct().getPrice());
			item.setDiscountedPrice(cartItem.getQuantity()*item.getProduct().getDiscountedPrice());
//			double Price = item.getPrice();
//			double discountedPrice = item.getDiscountedPrice();
//			int discount = (int)((Price - discountedPrice) / Price ) * 100;
			
		}
		
		return cartItemRepository.save(item);
	}

	@Override
	public CartItem isCartItemExist(Cart cart, Product product, String size, Long userId) {
		
		CartItem cartItem = cartItemRepository.isCartItemExist(cart, product, size, userId);
		System.out.println("C H E C K - 3");
		return cartItem;
	}

	@Override
	public void removeCartItem(Long userId, Long cartItemId) throws cartItemException, UserException {
		
		CartItem item = findCartItemById(cartItemId);
		
		User user = userService.findUserById(item.getUserId());
		
		User reqUser = userService.findUserById(userId);
//		System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOOOOOOOo");
//		if(user.getId().equals(reqUser.getId())) {
			Cart userCart = cartService.findUserCart(userId);
			userCart.getCartItems().remove(item);
			userCart.setTotalPrice(userCart.getTotalPrice() - item.getPrice()); // update the total Price
			userCart.setTotalItem(userCart.getTotalItem() - 1); // update the total Items count
			userCart.setTotalDiscountedPrice(userCart.getTotalDiscountedPrice() - item.getDiscountedPrice()); // update the discounted Price
			double MarkedPrice = userCart.getTotalPrice();
			double costPrice = userCart.getTotalDiscountedPrice();
			int discount = (int)( (MarkedPrice - costPrice) / MarkedPrice ) * 100; // Calculating the discount 
			userCart.setDiscount(discount);
			
			cartItemRepository.deleteById(cartItemId); // Delete the Item from the cartItem also so that you can add the Item to the cart next time
			
			cartRepository.save(userCart);
			System.out.println("Item removed from cart successfully !");
//		} else {
//			throw new UserException("You cannot delete this item - " + reqUser.getId());
//		}
		
	}

	@Override
	public CartItem findCartItemById(Long cartItemId) throws cartItemException {
		
		Optional<CartItem> optional = cartItemRepository.findById(cartItemId);
		
		if(optional.isPresent()) {
			return optional.get();
		}
		
		throw new cartItemException("Item not found with id - " + cartItemId);
	}

}
