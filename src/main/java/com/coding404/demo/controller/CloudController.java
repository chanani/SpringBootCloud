package com.coding404.demo.controller;

import com.coding404.demo.aws.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CloudController {

    @Autowired
    private S3Service s3;


    ///////////////////////////////S3////////////////////////////////
    @GetMapping("/main")
    public String main() {
        return "main";
    }

    @GetMapping("S3Request")
    public String S3Request(){
        s3.getBucketList();
        return "redirect:/main";
    }
}
