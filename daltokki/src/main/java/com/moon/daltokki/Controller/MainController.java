package com.moon.daltokki.Controller;

import
com.fasterxml.jackson.core.JsonProcessingException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.moon.daltokki.Model.SpModel;
import com.moon.daltokki.Service.MainService;
import com.moon.daltokki.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
public class MainController {

    @Autowired
    MainService mainService;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    // 메인페이지 (사용자의 송편 목록 조회)
    @GetMapping(value = "/main")
    public String main(Model model, @RequestParam(required = false) String id) {

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String userId = authentication.getName(); // 사용자의 아이디
        log.info("[MainController][main] userId : {}", id);

        // DB에서 해당 url 아이디 존재 여부 확인
        boolean isIdExists = userService.checkIdExists(id);
        
        if (!isIdExists) { // 존재하지 않을때
            model.addAttribute("idNotExist", true);
        } else { // 존재하면 송편 목록 조회
            List<SpModel> spList = mainService.selectSpList(id);

            model.addAttribute("spList", spList);
            log.info("[MainController][main] spList : {}", spList);
        }

        return "Home";
    }

    // 개별 송편 조회
    @ResponseBody
    @GetMapping(value = "/findSp")
    public SpModel findSp2(@RequestParam String spId) throws JsonProcessingException {
        SpModel sp = mainService.selectSp(spId);
        log.info("[MainController][findSp] sp : {}", sp);
        return sp; // ajax 리턴
    }

    // 송편 지우기
    @ResponseBody
    @PostMapping(value = "/deleteSp")
    public void deleteSp(@RequestParam String spId) throws JsonProcessingException {
        mainService.removeSp(spId);
    }
    
    // QR코드 생성
    @GetMapping(value = "/qr")
    public String QrPage() {
        return "Home";
    }

    @ResponseBody
    @RequestMapping(value = "/getQrcode")
    public ResponseEntity<byte[]> getQrcode2() throws WriterException, IOException {
        // QR 정보
        int width = 200;
        int height = 200;
        String url = "http://localhost:8080/"; // 테스트니까 고정주소

        // QR Code - BitMatrix: qr code 정보 생성
        BitMatrix encode = new MultiFormatWriter()
                .encode(url, BarcodeFormat.QR_CODE, width, height);

        // QR Code - Image 생성. : 1회성으로 생성해야 하기 때문에
        // stream으로 Generate(1회성이 아니면 File로 작성 가능.)
        try {
            //output Stream
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            //Bitmatrix, file.format, outputStream
            MatrixToImageWriter.writeToStream(encode, "PNG", out);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(out.toByteArray());

        }catch (Exception e){log.warn("QR Code OutputStream 도중 Excpetion 발생, {}", e.getMessage());}

        return null;
    }

    // 구글 로그인
    @GetMapping(value = "/googleLogin")
    public String getOuthUrl() {

        return "Home";
    }

    // ---------------------- 지은 -------------------------

}