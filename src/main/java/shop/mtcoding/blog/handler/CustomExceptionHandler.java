package shop.mtcoding.blog.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import shop.mtcoding.blog.dto.ResponseDto;
import shop.mtcoding.blog.handler.ex.CustomApiException;
import shop.mtcoding.blog.handler.ex.CustomException;
import shop.mtcoding.blog.util.Script;

// @ControllerAdvice// 파일 요청
@RestControllerAdvice // 데이터 요청
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> customException(CustomException e) {
        // return Script.back(e.getMessage());
        String responseBody = Script.back(e.getMessage());
        return new ResponseEntity<>(responseBody, e.getStatus());
    }

    @ExceptionHandler(CustomApiException.class)
    public ResponseEntity<?> CustomApiException(CustomApiException e) {
        // return Script.back(e.getMessage());
        return new ResponseEntity<>(new ResponseDto<>(1, e.getMessage(), null), e.getStatus());
    }
}