package sobs;

import java.util.List;

public class SOBItem {
	private int transactionLimit;
	private int buyNowPrice;
	private SOBClient seller;
	private SOBClient currBidder;
	private String name;
	private String description;
	private int currBid;
	private boolean isSold; 
	private int itemNumber;
	private int bidNumber;
	
	public int getItemNumber() {
		return itemNumber;
	}
	public void setItemNumber(int itemNumber) {
		this.itemNumber = itemNumber;
	}
	public int getTransactionLimit() {
		return transactionLimit;
	}
	public void setTransactionLimit(int transactionLimit) {
		this.transactionLimit = transactionLimit;
	}
	public int getBuyNowPrice() {
		return buyNowPrice;
	}
	public void setBuyNowPrice(int buyNowPrice) {
		this.buyNowPrice = buyNowPrice;
	}
	public SOBClient getSeller() {
		return seller;
	}
	public void setSeller(SOBClient seller) {
		this.seller = seller;
	}
	public SOBClient getCurrBidder() {
		return currBidder;
	}
	public void setCurrBidder(SOBClient currBidder) {
		this.currBidder = currBidder;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isSold() {
		return isSold;
	}
	public void setSold(boolean isSold) {
		this.isSold = isSold;
	}
	public int getCurrBid() {
		return currBid;
	}
	public void setCurrBid(int currBid) {
		this.currBid = currBid;
	}
	
	public String printItem(){
		return this.itemNumber + " " + this.name + " " + this.seller.getUsername() + " " + this.currBid + " " + this.buyNowPrice + " " + this.description;		
	}
	public int getBidNumber() {
		return bidNumber;
	}
	public void setBidNumber(int bidNumber) {
		this.bidNumber = bidNumber;
	}
	public void incrementBidNumber() {
		this.bidNumber = bidNumber + 1;
	}
	
}
