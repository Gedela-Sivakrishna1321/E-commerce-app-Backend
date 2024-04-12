package com.ecommerce.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.Exceptions.UserException;
import com.ecommerce.Exceptions.cartItemException;
import com.ecommerce.Models.Cart;
import com.ecommerce.Models.CartItem;
import com.ecommerce.Models.User;
import com.ecommerce.Repository.CartItemRepository;
import com.ecommerce.Response.ApiResponse;
import com.ecommerce.Service.CartItemService;
import com.ecommerce.Service.CartService;
import com.ecommerce.Service.UserService;

@RestController
@RequestMapping("/api/cart_items")
//@CrossOrigin(origins = "http://localhost:3000")
public class CartItemController {
	@Autowired
	private CartItemService cartItemService;
	@Autowired
	private UserService userService;
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	private CartItemRepository cartItemRepository;
	
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse> removeItemFromCart(@PathVariable Long id,
			@RequestHeader("Authorization") String jwt) throws UserException, cartItemException {
		
		User user = userService.findUserProfileByJwt(jwt);
		
		cartItemService.removeCartItem(user.getId(), id);
		
		Cart userCart = cartService.findUserCart(user.getId());
		
		ApiResponse response = new ApiResponse();
		response.setMessage("Item Removed From Cart Successfully ");
		
		return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse> updateCartItem(@PathVariable Long id,
			@RequestHeader("Authorization") String jwt, @RequestBody CartItem cartItem ) throws UserException, cartItemException {
		
		User user = userService.findUserProfileByJwt(jwt);
		CartItem updateCartItem = cartItemService.updateCartItem(user.getId(), id, cartItem);
		
		ApiResponse response = new ApiResponse();
		response.setMessage("Cart Item Updated successfully ..!");
//		System.out.println("Cart Item Updated successfully !");
		return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
	}
	
	@GetMapping("/all")
	public ResponseEntity<List<CartItem>> findallCartItemsHandler() {
		
		List<CartItem> cartItems = cartItemRepository.findAll();
		
		return new ResponseEntity<List<CartItem>>(cartItems, HttpStatus.OK);
	}

}
