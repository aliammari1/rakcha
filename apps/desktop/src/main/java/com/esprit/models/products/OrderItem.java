package com.esprit.models.products;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

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


public class OrderItem {

    private Order order;
    private Product product;
    private int quantity;
    private Double unitPrice;
    private Long id;

    /**
     * Create a new OrderItem with the specified quantity, product, and order; the
     * `id` remains unset.
     *
     * @param order     the associated Order
     * @param product   the associated Product
     * @param quantity  the number of product units for this item
     * @param unitPrice the price per unit of the product
     */
    public OrderItem(final Order order, final Product product, final int quantity, final Double unitPrice) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    /**
     * Convenience method to get product name.
     *
     * @return the product name
     */
    public String getProductName() {
        return product != null ? product.getName() : "";
    }

    /**
     * Convenience method to get product ID.
     *
     * @return the product ID
     */
    public Long getProductId() {
        return product != null ? product.getId() : null;
    }

    /**
     * Convenience method to calculate subtotal (quantity * unit price).
     *
     * @return the subtotal for this item
     */
    public Double getSubtotal() {
        return quantity * (unitPrice != null ? unitPrice : 0.0);
    }

    // Explicit getters (since Lombok @Data is not generating them)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

}


