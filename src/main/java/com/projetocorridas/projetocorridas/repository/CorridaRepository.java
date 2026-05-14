import com.projetocorridas.projetocorridas.model.Corrida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CorridaRepository extends JpaRepository<Corrida, UUID> {
    Optional<Corrida> findByTitulo(String titulo);
}
