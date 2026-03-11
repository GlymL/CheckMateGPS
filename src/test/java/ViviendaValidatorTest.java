import com.example.demo.Vivienda;
import com.example.demo.ViviendaValidator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ViviendaValidatorTest {

    @Test
    public void testValido() {
        Vivienda v = new Vivienda("Casa123", "Bonita casa", "foto.png");
        assertNull(ViviendaValidator.validate(v));
    }

    @Test
    public void testNombreInvalido() {
        Vivienda v = new Vivienda("Casa@123", "Bonita casa", "foto.png");
        assertNotNull(ViviendaValidator.validate(v));
    }

    @Test
    public void testFotoInvalida() {
        Vivienda v = new Vivienda("Casa1", "Bonita casa", "foto.gif");
        assertNotNull(ViviendaValidator.validate(v));
    }
}