package com.esprit.models.products;

import com.esprit.models.users.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;

@Log4j2
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Product management entity class for the RAKCHA application. Manages product
 * data and relationships with database persistence.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */


public class ShoppingCart {

    private int quantity;
    private Product product;
    private User user;
    private LocalDateTime addedAt;
    private Long id;

    /**
     * Create a ShoppingCart instance for a new (not yet persisted) cart without an id.
     *
     * @param user     the owner of the cart
     * @param product  the product added to the cart
     * @param quantity the quantity of the product in the cart
     */
    public ShoppingCart(final User user, final Product product, final int quantity) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
        this.addedAt = LocalDateTime.now();
    }

}


