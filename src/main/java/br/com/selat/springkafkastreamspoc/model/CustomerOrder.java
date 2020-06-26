package br.com.selat.springkafkastreamspoc.model;

public class CustomerOrder {
    private Customer customer;
    private Order order;
    private Long productId;

    public CustomerOrder() {
    }

    public CustomerOrder(Customer customer, Order order) {
        this.customer = customer;
        this.order = order;
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

    public Long productId() {
        return this.productId;
    }
}
