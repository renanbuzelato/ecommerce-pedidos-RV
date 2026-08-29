
import com.ecommerce.pedidos.modelo.Produto;

public class App{
    
    public static void main(String[] args) {
        Produto p = new Produto("COD001","TEC-001", "Teclado Mecânico", 150.00, 8);
        Produto r = new Produto("COD001","MON-002", "Monitor 24\"", 899.90, 3);
        
        System.out.println(p);
        System.out.println(r);

        r.baixarEstoque(15);

        System.out.println(r);

    }
}