package com.future.platform.account.handler;

import com.future.platform.commons.utils.VerifyCodeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 图片验证码和短信验证码处理
 * @Date:Created in 2019/9/26 18:26.
 * @Modified By:
 */
public class ValidatePicAndSmsHandler {

    private static Logger logger = LoggerFactory.getLogger(ValidatePicAndSmsHandler.class);

    private
    /**
     * 配置渠道手机开户 - 生成验证码图片
     */
    @RequestMapping("/common/getPicCaptcha.do")
    public void getPicCaptcha(HttpServletRequest request, HttpServletResponse response) {

        try {
            //生成验证码
            String code = VerifyCodeUtils.generateVerifyCode(4);

            //生成图片
            VerifyCodeUtils.outputImage(100, 36, response.getOutputStream(), code);

            //验证码放入缓存
            String phone = request.getAttribute("phone").toString();
            if(StringUtils.isNotBlank(phone)){
                String key = ActivityConstants.PICTURE_CAPTCHA  + "/" + phone;
                objectStorage.setObj(key, code);
                logger.info("getCaptchaPicture---key:" + key + ",value:" + code);
            }
        } catch (Exception e) {
            logger.error("生成验证码图片异常:" + e);
        }
    }

    /**
     * 配置渠道手机开户 - 验证图片或短信验证码
     */
    @RequestMapping("/common/checkCaptcha.do")
    public void checkCaptcha(Model model) {

        try {
            String phone = model.getValue("phone");
            String code = req.getParams().getValue("code");
            String type = req.getParams().getValue("type");
            logger.info("checkCaptcha:" + type + ",phone:" + phone + ",code:" + code);

            if(StringUtils.isNotBlank(phone) && StringUtils.isNotBlank(code) && StringUtils.isNotBlank(type)){
                if(ActivityConstants.CAPTCHA_TYPE_PICTURE.equals(type)){ //图片验证码
                    String redisCode = objectStorage.getObj(ActivityConstants.PICTURE_CAPTCHA + "/" + phone, String.class);
                    if(code.equalsIgnoreCase(redisCode)){
                        String turns = this.getTurns(req.getParams().getValue("moduleDataId"));
                        if(this.sendCaptchaMessage(phone, turns)){  //图片验证完需发送短信验证码
                            respSuccess(resp, null);
                            return;
                        }else{
                            respFailed(resp, "今日累计获取验证码5次已满,请明日再试");
                            return;
                        }
                    }else{
                        respFailed(resp, "图片验证码错误");
                        return;
                    }
                }else if(ActivityConstants.CAPTCHA_TYPE_MESSAGE.equals(type)){  //短信验证码
                    String redisCode = objectStorage.getObj(ActivityConstants.MESSAGE_CAPTCHA + "/" + phone, String.class);
                    if(code.equalsIgnoreCase(redisCode)){
                        String channel = req.getParams().getValue("channel");
                        String channelValue = req.getParams().getValue("channelValue");
                        this.sendChannel(phone, channel, channelValue);  //短信验证完需送渠道关系
                        respSuccess(resp, null);
                        return;
                    }else{
                        respFailed(resp, "短信验证码错误");
                        return;
                    }
                }
            }
            respFailed(resp, "验证码错误");

        } catch (Exception e) {
            respFailed(resp, e);
        }
    }

    /**
     * 发送短信验证码
     */
    private boolean sendCaptchaMessage(String phone, String turns){

        try {
            //短信验证码一天五次校验
            String today = DateTimeUtil.specificFormatDate(new Date());
            String timeKey = ActivityConstants.MESSAGE_TIME + today + "/" + phone;
            String time = objectStorage.getObj(timeKey, String.class);
            if(StringUtils.isBlank(time)){
                objectStorage.setObj(timeKey, 1);
            }else{
                Long currentTime = objectCache.incrBy(timeKey, 1);
                int turnsInt = StringUtils.isNotBlank(turns) ? Integer.valueOf(turns) : 5;
                if(currentTime > turnsInt){
                    return false;
                }
            }
            //生产六位数字验证码并且放入缓存
            String messageCode = String.valueOf((int)((Math.random()*9+1)*100000));
            String key = ActivityConstants.MESSAGE_CAPTCHA  + "/" + phone;
            objectStorage.setObj(key, messageCode);
            objectStorage.expire(ActivityConstants.MESSAGE_CAPTCHA, phone, 600);  //设置过期时间为十分钟

            //发送验证码短信
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setTemplateId(NotifyTemplateConstant.CHANNEL_OPEN_CAPTCHA);
            messageInfo.setPhone(phone);
            messageInfo.setUserId(ANONYM_USER);
            Map<String, String> param = new HashMap<String, String>();
            param.put("messageCode", messageCode);
            messageInfo.setParam(param);
            boolean result = messageInfoService.sendMessage(messageInfo);
            logger.info("sendCaptchaMessage--phone:" + phone + ",result:" + result);
            return true;

        } catch (Exception e) {
            logger.error("发送短信验证码异常:" + e);
            return false;
        }
    }
}
