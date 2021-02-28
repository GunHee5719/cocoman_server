package orangetaxiteam.cocoman.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ContentsDetailDTO {
    private ContentsDTO contentsInfo;
    private Double averageStar;
    private List<ReviewDTO> reviewList;
    private List<ContentsDTO> relatedContentsList;
}
