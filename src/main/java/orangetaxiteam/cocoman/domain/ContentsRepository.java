package orangetaxiteam.cocoman.domain;

import orangetaxiteam.cocoman.domain.exceptions.BadRequestException;
import orangetaxiteam.cocoman.domain.exceptions.ErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentsRepository extends JpaRepository<Contents, String> {
    Optional<Contents> findByTitle(String title);

    default Contents findByTitleOrElseThrow(String title) {
        return this.findByTitle(title).orElseThrow(
                () -> new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "invalid contents title")
        );
    }
}
