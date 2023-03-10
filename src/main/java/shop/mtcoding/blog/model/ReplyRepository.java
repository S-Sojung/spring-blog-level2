package shop.mtcoding.blog.model;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import shop.mtcoding.blog.dto.reply.ReplyResp.ReplyAllRespDto;
import shop.mtcoding.blog.dto.reply.ReplyResp.ReplyDetailRespDto;

@Mapper
public interface ReplyRepository {

        public List<Reply> findAll();

        public Reply findById(int id);

        // id, comment, userId, boardId
        public List<ReplyDetailRespDto> findByBoardIdWithUser(int boardId);

        public List<ReplyAllRespDto> findAllWithUser();

        public int insert(@Param("comment") String comment, @Param("boardId") int boardId, @Param("userId") int userId);

        public int updateById(@Param("id") int id, @Param("comment") String comment);

        public int deleteById(int id);
}
