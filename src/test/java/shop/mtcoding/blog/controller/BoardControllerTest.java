package shop.mtcoding.blog.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.mtcoding.blog.dto.board.BoardReq.BoardUpdateReqDto;
import shop.mtcoding.blog.dto.board.BoardResp.BoardDetailRespDto;
import shop.mtcoding.blog.dto.board.BoardResp.BoardMainRespDto;
import shop.mtcoding.blog.model.User;

//서비스에 붙은 트랜잭션은 메서드가 종료 되었을 때 커밋, 실행하다 오류터지면 롤백 
//@Transactional(rollbackFor = CustomException.class) 특정 경우에만 롤백 시킬 수도 있다. 기본은 RuntimeException 

//test에 하는 트랜잭션은 테스트 격리 시 사용함. 메서드 실행 직후 롤백 
//단, auto_increment 초기화가 안됨 
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class BoardControllerTest {

    @Autowired
    private MockMvc mvc;

    private MockHttpSession mockSession;

    @Autowired
    private ObjectMapper om;

    @Test
    public void update_test() throws Exception {
        // given
        int id = 1;
        // String requestBody = "title=수정된 제목입니다&content=수정된 내용입니다";
        BoardUpdateReqDto boardUpdateReqDto = new BoardUpdateReqDto();
        boardUpdateReqDto.setTitle("수정된제목");
        boardUpdateReqDto.setContent("수정된내용");

        String requestBody = om.writeValueAsString(boardUpdateReqDto);
        // System.out.println("테스트 : "+requestBody);

        // when
        ResultActions resultActions = mvc.perform(
                put("/board/" + id)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .session(mockSession)); // session이 주입된 채로 요청

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.print("테스트: " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.msg").value("게시글 수정 성공"));
        resultActions.andExpect(status().isCreated());
    }

    @Test
    public void delete_test() throws Exception {
        // given
        int id = 1;

        // when
        ResultActions resultActions = mvc.perform(
                delete("/board/" + id).session(mockSession)); // session이 주입된 채로 요청
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.print("테스트: " + responseBody);

        /**
         * jsonPath
         * 최상위를 찾을 때는 $ 를 이용
         * 객체 탐색 은 닷 (.) 을 이용
         * 배열 : [0] 인덱스 이용
         * {"code":1,"msg":"삭제성공","data":null}
         * => $.code, $.msg ,$.data 등으로 찾을 수 있다.
         */
        // then
        resultActions.andExpect(jsonPath("$.msg").value("삭제성공"));
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void datail_test() throws Exception {
        // given
        int id = 4;

        // when
        ResultActions resultActions = mvc.perform(
                get("/board/" + id));

        Map<String, Object> map = resultActions.andReturn().getModelAndView().getModel();
        BoardDetailRespDto dto = (BoardDetailRespDto) map.get("dto");

        String model = om.writeValueAsString(dto);
        System.out.println("테스트 : " + model);

        // then
        resultActions.andExpect(status().isOk());
        assertThat(dto.getId()).isEqualTo(4);
        assertThat(dto.getUsername()).isEqualTo("love");
        assertThat(dto.getTitle()).isEqualTo("4번째 제목");
        assertThat(dto.getContent()).isEqualTo("4번째 내용");
        assertThat(dto.getUserId()).isEqualTo(2);
    }

    @Test
    public void main_test() throws Exception {
        // given

        // when
        ResultActions resultActions = mvc.perform(
                get("/"));

        Map<String, Object> map = resultActions.andReturn().getModelAndView().getModel();
        List<BoardMainRespDto> dtos = (List<BoardMainRespDto>) map.get("dtos");

        String model = om.writeValueAsString(dtos);
        System.out.println("테스트 : " + model);

        // then
        resultActions.andExpect(status().isOk());
        assertThat(dtos.size()).isEqualTo(6);
        assertThat(dtos.get(0).getUsername()).isEqualTo("ssar");
        assertThat(dtos.get(0).getTitle()).isEqualTo("1번째 제목");

    }

    @BeforeEach // Test메서드 실행 직전마다 호출된다
    public void setUp() {
        // 임시 세션 생성하기
        User user = new User();
        user.setId(1);
        user.setUsername("ssar");
        user.setPassword("1234");
        user.setEmail("ssar@nate.com");
        user.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

        mockSession = new MockHttpSession();
        mockSession.setAttribute("principal", user);
    }

    @Test
    public void save_test() throws Exception {
        // given
        // String title = "";
        // for (int i = 0; i < 99; i++) {
        // title += "가";
        // }
        // String requestBody = "title=" + title + ".&content=내용1입니다.";

        BoardUpdateReqDto boardUpdateReqDto = new BoardUpdateReqDto();
        boardUpdateReqDto.setTitle("수정된제목");
        boardUpdateReqDto.setContent("수정된내용");

        String requestBody = om.writeValueAsString(boardUpdateReqDto);

        // when
        ResultActions resultActions = mvc.perform(
                post("/board")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(mockSession)); // session이 주입된 채로 요청

        // then
        // resultActions.andExpect(status().is3xxRedirection());
        resultActions.andExpect(jsonPath("$.msg").value("게시글 작성 성공"));
        resultActions.andExpect(status().isCreated());

    }
}
