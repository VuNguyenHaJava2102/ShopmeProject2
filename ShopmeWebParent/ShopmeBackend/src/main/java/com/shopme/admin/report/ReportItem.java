package com.shopme.admin.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReportItem {

    private String identifier;
    private float grossSales;
    private float netSales;
    private int numberOfOrder;

    // Constructor
    public ReportItem(String identifier) {
        this.identifier = identifier;
    }

    public ReportItem(String identifier, String grossSales, String netSales) {
        this.identifier = identifier;
    }

    // Equals and HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportItem that = (ReportItem) o;
        return identifier.equals(that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    // Utility function
    public void addGrossSales(float amount) {
        this.grossSales += amount;
    }

    public void addNetSales(float amount) {
        this.netSales += (amount);
    }

    public void increaseNumberOfOrder() {
        ++this.numberOfOrder;
    }
}
