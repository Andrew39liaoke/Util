package com.example.utils.ossUtil;

import cn.hutool.core.date.DateTime;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
public class OSSutil {
	//阿里云API的内或外网域名
	public static String ENDPOINT="xxxx";
	//阿里云API的密钥Access Key ID
	public  static String ACCESS_KEY_ID="xxx";
	//阿里云API的密钥Access Key Secret
	public static String ACCESS_KEY_SECRET="xxx";
	//阿里云API的bucket名称
	public  static String BACKET_NAME="xxx";
	//阿里云API的文件夹名称
//	public static String FOLDER="yyx/";
	public static String FOLDER="xxx/";
	/**
	 * 获取阿里云OSS客户端对象
	 * @return ossClient
	 */
	public static  OSSClient getOSSClient(){
		return new OSSClient(ENDPOINT,ACCESS_KEY_ID, ACCESS_KEY_SECRET);
	}
	/**
	 * 创建存储空间
	 * @param ossClient      OSS连接
	 * @param bucketName 存储空间
	 * @return
	 */
	public  static String createBucketName(OSSClient ossClient,String bucketName){
		//存储空间
		final String bucketNames=bucketName;
		if(!ossClient.doesBucketExist(bucketName)){
			//创建存储空间
			Bucket bucket=ossClient.createBucket(bucketName);
			System.out.println("创建空间成功！");
			return bucket.getName();
		}
		return bucketNames;
	}
	/**
	 * 删除存储空间buckName
	 * @param ossClient  oss对象
	 * @param bucketName  存储空间
	 */
	public static  void deleteBucket(OSSClient ossClient, String bucketName){
		ossClient.deleteBucket(bucketName);
		System.out.println(("删除" + bucketName + "Bucket成功"));
	}
	/**
	 * 创建模拟文件夹
	 * @param ossClient oss连接
	 * @param bucketName 存储空间
	 * @param folder   模拟文件夹名如"qj_nanjing/"
	 * @return  文件夹名
	 */
	public  static String createFolder(OSSClient ossClient,String bucketName,String folder){
		//文件夹名
		final String keySuffixWithSlash =folder;
		//判断文件夹是否存在，不存在则创建
		if(!ossClient.doesObjectExist(bucketName, keySuffixWithSlash)){
			//创建文件夹
			ossClient.putObject(bucketName, keySuffixWithSlash, new ByteArrayInputStream(new byte[0]));
			System.out.println("创建文件夹成功");
			//得到文件夹名
			OSSObject object = ossClient.getObject(bucketName, keySuffixWithSlash);
			String fileDir=object.getKey();
			return fileDir;
		}
		return keySuffixWithSlash;
	}
	/**
	 * 根据key删除OSS服务器上的文件
	 * @param ossClient  oss连接
	 * @param bucketName  存储空间
	 * @param folder  模拟文件夹名 如"qj_nanjing/"
	 * @param fileName Bucket下文件名 如："cake.jpg"
	 */
	public static void deleteFile(OSSClient ossClient, String bucketName, String folder, String fileName){
		ossClient.deleteObject(bucketName, folder + fileName);
		System.out.println(("删除" + bucketName + "下的文件" + folder + fileName + "成功"));
	}

	/**
	 * 上传图片至OSS
	 * @param ossClient  oss连接
	 * @param file 上传文件（文件全路径如：D:\\image\\cake.jpg）
	 * @param bucketName  存储空间
	 * @return String 返回的唯一MD5数字签名
	 * */
	public static  String uploadObject2OSS(OSSClient ossClient, MultipartFile file, String bucketName, String fileName) {
		String resultStr = null;
		try {
			//以输入流的形式上传文件
			InputStream is = file.getInputStream();
			//文件大小
			Long fileSize = file.getSize();
			if(fileSize>0) {
				//创建上传Object的Metadata
				ObjectMetadata metadata = new ObjectMetadata();
				//上传的文件的长度
				metadata.setContentLength(is.available());
				//指定该Object被下载时的网页的缓存行为
				metadata.setCacheControl("no-cache");
				//指定该Object下设置Header
				metadata.setHeader("Pragma", "no-cache");
				//指定该Object被下载时的内容编码格式
				metadata.setContentEncoding("utf-8");
				//文件的MIME，定义文件的类型及网页编码，决定浏览器将以什么形式、什么编码读取文件。如果用户没有指定则根据Key或文件名的扩展名生成，
				//如果没有扩展名则填默认值application/octet-stream
				metadata.setContentType(getContentType(fileName));
				//指定该Object被下载时的名称（指示MINME用户代理如何显示附加的文件，打开或下载，及文件名称）
				metadata.setContentDisposition("filename/filesize=" + fileName + "/" + fileSize + "Byte.");
				String datePath=new DateTime().toString("yyyy/MM/dd")+"/";
				//上传文件   (上传文件流的形式)
				String uploadfileName=FOLDER+fileName;
				PutObjectResult putResult = ossClient.putObject(bucketName, uploadfileName, is, metadata);
				//解析结果
				//https://gjblog.oss-cn-shenzhen.aliyuncs.com/online/avatar6334378c6a974bdf9334c20a4b71c334.jpg
//				public static String ENDPOINT="http://oss-cn-shenzhen.aliyuncs.com";
				String url = "https://"+bucketName+"."+ENDPOINT.split("//")[1]+"/"+uploadfileName;
				return url;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultStr;
	}

	/**
	 * 通过文件名判断并获取OSS服务文件上传时文件的contentType
	 * @param fileName 文件名
	 * @return 文件的contentType
	 */
	public static  String getContentType(String fileName){
		//文件的后缀名
		String fileExtension = fileName.substring(fileName.lastIndexOf("."));
		if(".bmp".equalsIgnoreCase(fileExtension)) {
			return "image/bmp";
		}
		if(".gif".equalsIgnoreCase(fileExtension)) {
			return "image/gif";
		}
		if(".jpeg".equalsIgnoreCase(fileExtension) || ".jpg".equalsIgnoreCase(fileExtension)  || ".png".equalsIgnoreCase(fileExtension) ) {
			return "image/jpeg";
		}
		if(".html".equalsIgnoreCase(fileExtension)) {
			return "text/html";
		}
		if(".txt".equalsIgnoreCase(fileExtension)) {
			return "text/plain";
		}
		if(".vsd".equalsIgnoreCase(fileExtension)) {
			return "application/vnd.visio";
		}
		if(".ppt".equalsIgnoreCase(fileExtension) || "pptx".equalsIgnoreCase(fileExtension)) {
			return "application/vnd.ms-powerpoint";
		}
		if(".doc".equalsIgnoreCase(fileExtension) || "docx".equalsIgnoreCase(fileExtension)) {
			return "application/msword";
		}
		if(".xml".equalsIgnoreCase(fileExtension)) {
			return "text/xml";
		}
		if (".pdf".equalsIgnoreCase(fileExtension)) {
			return "application/pdf";
		}
		//默认返回类型
		return "image/jpeg";
	}
}
