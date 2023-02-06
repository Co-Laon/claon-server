package coLaon.ClaonBack.post.service;

import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.NotFoundException;
import coLaon.ClaonBack.common.exception.UnauthorizedException;
import coLaon.ClaonBack.common.validator.IsPrivateValidator;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostLike;
import coLaon.ClaonBack.post.dto.LikeFindResponseDto;
import coLaon.ClaonBack.post.dto.LikeResponseDto;
import coLaon.ClaonBack.post.repository.PostLikeRepository;
import coLaon.ClaonBack.post.repository.PostLikeRepositorySupport;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.BlockUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostLikeRepositorySupport postLikeRepositorySupport;
    private final PaginationFactory paginationFactory;
    private final BlockUserRepository blockUserRepository;

    @Transactional
    public LikeResponseDto createLike(User user, String postId) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        postLikeRepository.findByLikerAndPost(user, post).ifPresent(
                like -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "이미 좋아요 했습니다."
                    );
                }
        );

        return LikeResponseDto.from(
                postLikeRepository.save(PostLike.of(user, post)),
                postLikeRepository.countByPost(post)
        );
    }

    @Transactional
    public LikeResponseDto deleteLike(User user, String postId) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        PostLike like = postLikeRepository.findByLikerAndPost(user, post).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "아직 좋아요 하지 않았습니다."
                )
        );

        postLikeRepository.delete(like);

        return LikeResponseDto.from(
                like,
                postLikeRepository.countByPost(like.getPost())
        );
    }

    @Transactional(readOnly = true)
    public Pagination<LikeFindResponseDto> findLikeByPost(
            User user,
            String postId,
            Pageable pageable
    ) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        if (!post.getWriter().getNickname().equals(user.getNickname())) {
            IsPrivateValidator.of(post.getWriter().getNickname(), post.getWriter().getIsPrivate()).validate();

            if (!blockUserRepository.findBlock(user.getId(), post.getWriter().getId()).isEmpty()) {
                throw new UnauthorizedException(
                        ErrorCode.NOT_ACCESSIBLE,
                        String.format("%s을 찾을 수 없습니다.", post.getWriter().getNickname())
                );
            }
        }

        return this.paginationFactory.create(
                postLikeRepositorySupport.findAllByPost(post.getId(), user.getId(), pageable)
                        .map(LikeFindResponseDto::from)
        );
    }
}
