package com.njcool.console.controller;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.api.impl.WxOpenServiceImpl;
import me.chanjar.weixin.open.bean.message.WxOpenXmlMessage;
import me.chanjar.weixin.open.bean.result.WxOpenAuthorizerInfoResult;
import me.chanjar.weixin.open.bean.result.WxOpenQueryAuthResult;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;

/**
 * 微信认证API
 *
 * @author vino
 * @create 2018-10-25
 **/
@RestController
@RequestMapping("/ui/api/")
public class WechatController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WxOpenServiceImpl wechatService;

    /**
     * 首次启动后需要 等待收到 微信推送的 component_verify_ticket 后才可以使用其他接口
     * 在第三方平台创建审核通过后，微信服务器每隔10分钟会向第三方的消息接收地址
     * 推送一次component_verify_ticket，用于获取第三方平台接口调用凭据
     *
     * @param request
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "notify/receive_ticket", method = { RequestMethod.POST, RequestMethod.GET })
    public Object receiveTicket(HttpServletRequest request) throws IOException {
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String msgSignature = request.getParameter("msg_signature");
        String signature = request.getParameter("signature");
        String encType = request.getParameter("encrypt_type");
        //获得微信发来的加密消息
        StringWriter writer = new StringWriter();
        IOUtils.copy(request.getInputStream(), writer, "UTF-8");
        String requestBody = writer.toString();
        this.logger.info(
                "\n接收微信请求：[signature=[{}], encType=[{}], msgSignature=[{}],"
                        + " timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
                signature + encType + msgSignature + timestamp + nonce, requestBody);

        if (!StringUtils.equalsIgnoreCase("aes", encType)
                || !wechatService.getWxOpenComponentService().checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }

        // aes加密的消息
        WxOpenXmlMessage inMessage = WxOpenXmlMessage.fromEncryptedXml(requestBody,
                wechatService.getWxOpenConfigStorage(), timestamp, nonce, msgSignature);
        this.logger.debug("\n消息解密后内容为：\n{} ", inMessage.toString());
        try {
            String out = wechatService.getWxOpenComponentService().route(inMessage);
            this.logger.debug("\n组装回复信息：{}", out);
        } catch (WxErrorException e) {
            this.logger.error("receive_ticket", e);
        }

        return "success";
    }

    /**
     * 跳转到微信授权页面 扫码授权
     * @param request
     * @param response
     */
    @RequestMapping("auth/goto_auth_url")
    public void gotoPreAuthUrl(HttpServletRequest request, HttpServletResponse response) {
        String host = request.getHeader("host");
        String url = "http://" + host + "/ui/api/uth/jump.htm";
        try {
            url = wechatService.getWxOpenComponentService().getPreAuthUrl(url);
            response.sendRedirect(url);
        } catch (WxErrorException | IOException e) {
            logger.error("gotoPreAuthUrl", e);
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/auth/jump")
    @ResponseBody
    public WxOpenQueryAuthResult jump(@RequestParam("auth_code") String authorizationCode) {
        try {
            WxOpenQueryAuthResult queryAuthResult = wechatService.getWxOpenComponentService()
                    .getQueryAuth(authorizationCode);
            logger.info("getQueryAuth", queryAuthResult);
            return queryAuthResult;
        } catch (WxErrorException e) {
            logger.error("gotoPreAuthUrl", e);
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/get_authorizer_info")
    @ResponseBody
    public WxOpenAuthorizerInfoResult getAuthorizerInfo(@RequestParam String appId) {
        try {
            return wechatService.getWxOpenComponentService().getAuthorizerInfo(appId);
        } catch (WxErrorException e) {
            logger.error("getAuthorizerInfo", e);
            throw new RuntimeException(e);
        }
    }
}