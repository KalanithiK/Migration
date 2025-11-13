package com.example.Migration.model;


import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_selection")
public class CustomerSelection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @OneToMany(mappedBy = "customerSelection", cascade = CascadeType.ALL)
    private List<SelectedProduct> selectedProducts;

    // Getters and Setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public List<SelectedProduct> getSelectedProducts() { return selectedProducts; }
    public void setSelectedProducts(List<SelectedProduct> selectedProducts) { this.selectedProducts = selectedProducts; }
}
