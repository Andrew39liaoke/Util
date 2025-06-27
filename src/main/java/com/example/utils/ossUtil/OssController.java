package com.example.utils.ossUtil;

import com.example.utils.R;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.UUID;
@RestController
//@RequestMapping("/eduoss/fileoss")
@CrossOrigin
public class OssController {
    //上传身份证的方法
    @PostMapping("/eduoss/fileoss")
    public R uploadOssFile(@RequestPart MultipartFile file)  throws Exception{
        //返回上传到oss的路径
        String  url= OSSutil.uploadObject2OSS(OSSutil.getOSSClient(), file, OSSutil.BACKET_NAME, createSingleFileName(file));
        return R.ok().data("url",url).data("info", RecognizeIdcard.Recognize(url));
    }
    /**
     * 上传支付的付款信息的图片
     * @param file
     * @return
     */
    @PostMapping("/eduoss/expenses")
    public R  uploadPayFile(@RequestPart MultipartFile file) throws Exception{
        //调用上传工具 上传支付截图得到 url地址
        String  url= OSSutil.uploadObject2OSS(OSSutil.getOSSClient(), file, OSSutil.BACKET_NAME, createSingleFileName(file));
        //调用
        return  R.ok().data("url",url).data("info", PayOrcResult.parseContent(RecognizeGeneral.recognize(url)).setPayImg(url));
    }

    /**
     * 上传客户的照片
     * @param file 接收前端传过来的文件对象
     */
    @PostMapping("/eduoss/customer/fileoss")
    public R uploadOssFileCustomer(@RequestPart MultipartFile file)  throws Exception{
        //获取上传文件  MultipartFile
        //返回上传到oss的路径
        String  url= OSSutil.uploadObject2OSS(OSSutil.getOSSClient(), file, OSSutil.BACKET_NAME, createSingleFileName(file));
        HashMap<String, Object> map = new HashMap<>();
        map.put("name",file.getOriginalFilename());
        map.put("url",url);
        return R.ok().data(map);
    }
    public String createSingleFileName(MultipartFile file) {
        if (file == null)
            return "nophoto.png";
        if (file.getSize() <= 0L)
            return "nophoto.png";
        return String.valueOf(UUID.randomUUID().toString().replaceAll("-", "")) + "." + getFileExt(file.getContentType());
    }
    public String getFileExt(String contentType) {
        if ("image/bmp".equals(contentType))
            return "bmp";
        if ("image/gif".equals(contentType))
            return "gif";
        if ("image/jpeg".equals(contentType))
            return "jpg";
        if ("image/png".equals(contentType))
            return "png";
        if ("application/pdf".equals(contentType))
            return "pdf";
        return null;
    }
}
