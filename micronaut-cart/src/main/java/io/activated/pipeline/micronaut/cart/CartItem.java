package io.activated.pipeline.micronaut.cart;

import java.math.BigDecimal;

public class CartItem {

  private Product product;
  private int quantity;
  private Integer rating;
  private boolean someFlag;
  private Boolean someOtherFlag;
  private float amount;
  private Float otherAmount;
  private double doubleAmount;
  private Double otherDoubleAmount;
  private BigDecimal otherDoubleBigAmount;

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

  public Integer getRating() {
    return rating;
  }

  public void setRating(Integer rating) {
    this.rating = rating;
  }

  public boolean isSomeFlag() {
    return someFlag;
  }

  public void setSomeFlag(boolean someFlag) {
    this.someFlag = someFlag;
  }

  public Boolean getSomeOtherFlag() {
    return someOtherFlag;
  }

  public void setSomeOtherFlag(Boolean someOtherFlag) {
    this.someOtherFlag = someOtherFlag;
  }

  public float getAmount() {
    return amount;
  }

  public void setAmount(float amount) {
    this.amount = amount;
  }

  public Float getOtherAmount() {
    return otherAmount;
  }

  public void setOtherAmount(Float otherAmount) {
    this.otherAmount = otherAmount;
  }

  public double getDoubleAmount() {
    return doubleAmount;
  }

  public void setDoubleAmount(double doubleAmount) {
    this.doubleAmount = doubleAmount;
  }

  public Double getOtherDoubleAmount() {
    return otherDoubleAmount;
  }

  public void setOtherDoubleAmount(Double otherDoubleAmount) {
    this.otherDoubleAmount = otherDoubleAmount;
  }

  public BigDecimal getOtherDoubleBigAmount() {
    return otherDoubleBigAmount;
  }

  public void setOtherDoubleBigAmount(BigDecimal otherDoubleBigAmount) {
    this.otherDoubleBigAmount = otherDoubleBigAmount;
  }
}
