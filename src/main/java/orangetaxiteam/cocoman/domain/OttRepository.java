package orangetaxiteam.cocoman.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OttRepository extends JpaRepository<Ott, String> {
}
