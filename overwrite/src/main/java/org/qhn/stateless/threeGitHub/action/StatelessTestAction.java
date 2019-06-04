package org.qhn.stateless.threeGitHub.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.qhn.stateless.threeGitHub.domain.BaseResponse;
import org.qhn.stateless.threeGitHub.service.UserRoleService;
import org.qhn.stateless.threeGitHub.util.CommonUtil;
import org.qhn.stateless.threeGitHub.util.CryptoUtil;
import org.qhn.stateless.threeGitHub.util.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import io.jsonwebtoken.SignatureAlgorithm;

/**
 * 无状态鉴权测试
 * JWT(JSON WEB TOKEN)
 * HMAC(HASH消息认证码)
 *
 * @author wangjie (https://github.com/wj596)
 * @date 2016年9月15日
 *
 */
@Controller
@RequestMapping("/stateless")
public class StatelessTestAction {

	@Autowired
	private UserRoleService userRoleService;

    /**
     * hmac签名鉴权
     */
    @RequestMapping("/hmac_test")
    public String hmacTest(Model model) {
    	model.addAttribute("hmacAlg", ShiroUtils.getShiroProperties().getHmacAlg());
    	model.addAttribute("hmacSecretKey",ShiroUtils.getShiroProperties().getHmacSecretKey());
    	return "stateless/hmac_test";
    }

    /**
     * hmac签名鉴权
     */
    @RequestMapping("/jwt_test")
    public String jwtTest(Model model) {
    	model.addAttribute("hmacAlg",ShiroUtils.getShiroProperties().getHmacAlg());
    	model.addAttribute("hmacSecretKey",ShiroUtils.getShiroProperties().getHmacSecretKey());
    	return "stateless/jwt_test";
    }


    @RequestMapping("/hmac_digest")
    public @ResponseBody
	BaseResponse hmacDigest(@RequestParam(name="base_string") String base_string) {

    	String hmac_digest =
    			CryptoUtil.hmacDigest(base_string
    					,ShiroUtils.getShiroProperties().getHmacSecretKey()
    					,ShiroUtils.getShiroProperties().getHmacAlg());
        return BaseResponse.ok().add("hmac_digest", hmac_digest).message("签名成功");
    }

    @RequestMapping("/issue_jwt")
    public @ResponseBody BaseResponse issueJwt() {

    	String usersAccount = ShiroUtils.getUser().getAccount();
    	List<String> userRoles = userRoleService.listUserRoles(usersAccount);

    	String jwt =  CryptoUtil.issueJwt(ShiroUtils.getShiroProperties().getJwtSecretKey()
						  ,ShiroUtils.getUser().getAccount()
						  ,ShiroUtils.getUser().getAccount()
						  ,10000l
						  , CommonUtil.join(userRoles)
						  ,null
						  ,SignatureAlgorithm.HS512);
        return BaseResponse.ok().add("jwt", jwt).message("令牌生成成功");
    }

    @RequestMapping("/hmac_post")
    public @ResponseBody BaseResponse hmacPost(
    		@RequestParam(name="apiName") String apiName
    		,@RequestParam(name="parameter1") String parameter1
			,@RequestParam(name="parameter2") String parameter2
			,@RequestParam(name="hmac_app_id") String hmac_app_id
			,@RequestParam(name="hmac_timestamp") long hmac_timestamp
			,@RequestParam(name="hmac_digest") String hmac_digest
			,HttpServletRequest request) {

		MultiValueMap<String, Object> form = new LinkedMultiValueMap<String, Object>();
		form.add("parameter1", parameter1);
		form.add("parameter2", parameter2);
	    form.add("hmac_app_id", hmac_app_id);
	    form.add("hmac_timestamp", hmac_timestamp);
	    form.add("hmac_digest", hmac_digest);
	    try{
	    	String restPostUrl = request.getRequestURL().toString().replace("stateless/hmac_post", "");
	    	restPostUrl += "hmac_api/"+apiName;
	    	RestTemplate restTemplate = new RestTemplate();
	    	String result = restTemplate.postForObject(restPostUrl, form, String.class);
	        return BaseResponse.ok().message(result);
	    }catch(HttpClientErrorException e){
	        return BaseResponse.fail().message(e.getResponseBodyAsString());
	    }
    }

    @RequestMapping("/hmac_get")
    public @ResponseBody BaseResponse hmacGet(
    		 @RequestParam(name="apiName") String apiName
    		,@RequestParam(name="parameter1") String parameter1
			,@RequestParam(name="parameter2") String parameter2
			,@RequestParam(name="hmac_app_id") String hmac_app_id
			,@RequestParam(name="hmac_timestamp") long hmac_timestamp
			,@RequestParam(name="hmac_digest") String hmac_digest
			,HttpServletRequest request) {

	    StringBuilder queryStr = new StringBuilder();
	    queryStr.append("?parameter1="+parameter1);
	    queryStr.append("&parameter2="+parameter2);
	    queryStr.append("&hmac_app_id="+hmac_app_id);
	    queryStr.append("&hmac_timestamp="+hmac_timestamp);
	    queryStr.append("&hmac_digest="+hmac_digest);

	    try{
	    	String restGetUrl = request.getRequestURL().toString().replace("stateless/hmac_get", "");
	    	restGetUrl += "hmac_api/"+apiName;
	    	restGetUrl += queryStr;
	    	RestTemplate restTemplate = new RestTemplate();
	    	String result = restTemplate.getForObject(restGetUrl, String.class);
	        return BaseResponse.ok().message(result);
	    }catch(HttpClientErrorException e){
	        return BaseResponse.fail().message(e.getResponseBodyAsString());
	    }
    }


    @RequestMapping("/jwt_post")
    public @ResponseBody BaseResponse jwtPost(
    						 @RequestParam(name="apiName") String apiName
    						,@RequestParam(name="jwt") String jwt
    						,HttpServletRequest request) {

		MultiValueMap<String, Object> form = new LinkedMultiValueMap<String, Object>();
		form.add("jwt", jwt);
	    try{
	    	String restPostUrl = request.getRequestURL().toString().replace("stateless/jwt_post", "");
	    	restPostUrl += "jwt_api/"+apiName;
	    	RestTemplate restTemplate = new RestTemplate();
	    	String result = restTemplate.postForObject(restPostUrl, form, String.class);
	    	return BaseResponse.ok().message(result);
	    }catch(HttpClientErrorException e){
	        return BaseResponse.fail().message(e.getResponseBodyAsString());
	    }
    }

    @RequestMapping("/jwt_get")
    public @ResponseBody BaseResponse jwtGet(
    		 @RequestParam(name="apiName") String apiName
    		,@RequestParam(name="jwt") String jwt
			,HttpServletRequest request) {
	    try{
	    	String restGetUrl = request.getRequestURL().toString().replace("stateless/jwt_get", "");
	    	restGetUrl += "jwt_api/"+apiName+"?jwt="+jwt;
	    	RestTemplate restTemplate = new RestTemplate();
	    	String result = restTemplate.getForObject(restGetUrl, String.class);
	        return BaseResponse.ok().message(result);
	    }catch(HttpClientErrorException e){
	        return BaseResponse.fail().message(e.getResponseBodyAsString());
	    }
    }

}
