package br.com.selat.springkafkastreamspoc.model;

public class EnrichedOrder {
    private Product product;
    private Customer customer;
    private Order order;

    public EnrichedOrder() {
    }

    public EnrichedOrder(Product product, Customer customer, Order order) {
        this.product = product;
        this.customer = customer;
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
