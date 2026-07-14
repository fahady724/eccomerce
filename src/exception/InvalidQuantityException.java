package exception;

public class InvalidQuantityException extends Exception{
    private int requestedQuantity;
    private int availableQuantity;

    public int getRequestedQuantity(){
        return requestedQuantity;
    }
    public int getAvailableQuantity(){
        return availableQuantity;
    }
    
    public InvalidQuantityException(int requestedQuantity, int availableQuantity){
        super("Invalid quantity! Requested: " + requestedQuantity + " but only " + availableQuantity + " available.");
        this.availableQuantity = availableQuantity;
        this.requestedQuantity = requestedQuantity;
    }
}
