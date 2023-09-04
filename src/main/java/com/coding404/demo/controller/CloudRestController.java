package com.coding404.demo.controller;

import com.coding404.demo.aws.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class CloudRestController {

    @Autowired
    private S3Service s3;

    @PostMapping("/cloudUpload")
    public ResponseEntity<String> cloudUpload(@RequestParam("file_data") MultipartFile file) {

        System.out.println(file);


        try {
            // 파일명
            String originName = file.getOriginalFilename();
            // 파일데이터
            byte[] originData = file.getBytes();

            s3.putS3Object(originName, originData);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>("응답 데이터", HttpStatus.OK);
    }


    // 버킷의 객체 확인
    @GetMapping("/list_bucket_objects")
    public ResponseEntity<String> list_bucket_objects(){
        s3.listBucketObjects();
        return new ResponseEntity<>("응답 데이터", HttpStatus.OK);
    }

    @PostMapping("/delete_bucket_objects")
    public ResponseEntity<String> delete_bucket_objects(@RequestParam("bucket_obj_name") String bucket_obj_name){
        System.out.println(bucket_obj_name);
        return new ResponseEntity<>("응답 데이터", HttpStatus.OK);
    }






}
