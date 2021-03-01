package orangetaxiteam.cocoman.application;


import orangetaxiteam.cocoman.application.dto.ContentsDetailDTO;
import orangetaxiteam.cocoman.application.dto.StarRatingCreateRequestDTO;
import orangetaxiteam.cocoman.domain.Contents;
import orangetaxiteam.cocoman.domain.ContentsRecommender;
import orangetaxiteam.cocoman.domain.ContentsRepository;
import orangetaxiteam.cocoman.domain.Genre;
import orangetaxiteam.cocoman.domain.GenreRepository;
import orangetaxiteam.cocoman.domain.OttRepository;
import orangetaxiteam.cocoman.domain.ReviewRepository;
import orangetaxiteam.cocoman.domain.SearchHistory;
import orangetaxiteam.cocoman.domain.SearchHistoryRepository;
import orangetaxiteam.cocoman.domain.StarRating;
import orangetaxiteam.cocoman.domain.StarRatingRepository;
import orangetaxiteam.cocoman.domain.User;
import orangetaxiteam.cocoman.domain.UserRepository;
import orangetaxiteam.cocoman.domain.exceptions.BadRequestException;
import orangetaxiteam.cocoman.domain.exceptions.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContentsApplicationService {
    private final ContentsRepository contentsRepository;
    private final GenreRepository genreRepository;
    private final OttRepository ottRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ContentsRecommender contentsRecommender;
    private final StarRatingRepository starRatingRepository;

    public ContentsApplicationService(
            ContentsRepository contentsRepository,
            GenreRepository genreRepository,
            OttRepository ottRepository,
            SearchHistoryRepository searchHistoryRepository,
            UserRepository userRepository,
            ReviewRepository reviewRepository,
            ContentsRecommender contentsRecommender,
            StarRatingRepository starRatingRepository
    ) {
        this.contentsRepository = contentsRepository;
        this.genreRepository = genreRepository;
        this.ottRepository = ottRepository;
        this.searchHistoryRepository = searchHistoryRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.contentsRecommender = contentsRecommender;
        this.starRatingRepository = starRatingRepository;
    }

    @Transactional(readOnly = true)
    public ContentsDetailDTO findById(String id) {
        Contents contents = this.contentsRepository.findById(id).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        String.format("There are no data matches with contents id : %s", id
                        ))
        );

        List<String> genreList = contents.getGenreSet()
                .stream()
                .map(Genre::getName)
                .collect(Collectors.toList());

        // TODO : I'll do it when implementing contents detail - by GH
        return ContentsDetailDTO.from(
                contents,
                null,
                this.contentsRecommender.getRelatedContentsList(genreList)
        );
    }

    @Transactional
    public void createSearchHistory(
            String contentsId,
            String keyword,
            String currentUserId
    ) {
        Contents contents = this.contentsRepository.findById(contentsId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        String.format("There are no data matches with contents id : %s", contentsId
                        )
                )
        );
        User user = this.userRepository.findById(currentUserId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        String.format("There is no user matches with user id : %s", currentUserId
                        )
                )
        );
        this.searchHistoryRepository.save(SearchHistory.of(contents, keyword, user));
    }

    @Transactional
    public void giveStarRating(String contentsId, StarRatingCreateRequestDTO starRatingCreateRequestDTO) {
        String userId = starRatingCreateRequestDTO.getUserId();
        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "Invalid user id")
        );

        Contents contents = this.contentsRepository.findById(contentsId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        String.format("There are no data matches with contents id : %s", contentsId))
        );

        if (this.starRatingRepository.existsByUserAndContents(user, contents)) {

            throw new BadRequestException(
                    ErrorCode.ROW_DOES_NOT_EXIST, // ToDo : Modify
                    String.format("Already exist value with userId, contentsId : %s, %s", userId, contentsId)
            );
        }

        double rating = starRatingCreateRequestDTO.getRating();
        this.starRatingRepository.save(StarRating.of(rating, user, contents));
    }

    @Transactional
    public void updateStarRating(String contentsId, StarRatingCreateRequestDTO starRatingCreateRequestDTO) {
        String userId = starRatingCreateRequestDTO.getUserId();
        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "Invalid user id")
        );

        Contents contents = this.contentsRepository.findById(contentsId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        String.format("There are no data matches with contents id : %s", contentsId))
        );

        StarRating starRating = this.starRatingRepository.findByUserAndContents(user, contents).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        String.format("There are no data matches with user id, contents id : %s, %s", userId, contentsId))
        );

        double rating = starRatingCreateRequestDTO.getRating();
        starRating.update(rating);
        this.starRatingRepository.save(starRating);
    }
}
