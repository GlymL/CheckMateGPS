package CheckMateGPS.src;

import com.tuapp.dto.ViviendaDTO;
import com.tuapp.models.Vivienda;
import com.tuapp.repositories.ViviendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ViviendaService {

    @Autowired
    private ViviendaRepository viviendaRepository;

    public Vivienda crearVivienda(ViviendaDTO dto) throws IOException {
        // CM1-4: Comprobar si el nombre ya existe
        if (viviendaRepository.existsByNombre(dto.getNombre())) {
            throw new IllegalArgumentException("La operación ha fallado: El nombre de la vivienda ya existe y debe ser único.");
        }

        // CM1-2 y CM1-3: Validar que hay foto y su formato
        if (dto.getFoto() == null || dto.getFoto().isEmpty()) {
            throw new IllegalArgumentException("Falta adjuntar la foto de la vivienda.");
        }
        
        String contentType = dto.getFoto().getContentType();
        if (contentType == null || (!contentType.equals("image/png") && !contentType.equals("image/jpeg"))) {
            throw new IllegalArgumentException("La foto debe de ser en formato .png o .jpeg.");
        }

        // Crear la entidad y guardar
        Vivienda nuevaVivienda = new Vivienda();
        nuevaVivienda.setNombre(dto.getNombre());
        nuevaVivienda.setDescripcion(dto.getDescripcion());
        nuevaVivienda.setFoto(dto.getFoto().getBytes());

        return viviendaRepository.save(nuevaVivienda);
    }
}