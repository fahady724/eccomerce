package exception;

public class ProductNotFoundException extends Exception {
    private int productId;

    public ProductNotFoundException(int productId) {
        super("Product with ID " + productId + " not found!");
        this.productId = productId;
    }

    public int getProductId() { 
        return productId; 
    }
}