package com.yotouch.base.web.connect;

import com.yotouch.base.web.BaseController;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import weibo4j.Account;
import weibo4j.Oauth;
import weibo4j.Users;
import weibo4j.http.AccessToken;
import weibo4j.model.User;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
public class WeiboConnectController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(WeiboConnectController.class);

    @RequestMapping("/connect/weibo/callback")
    public String weiboConnectCallback(
            @RequestParam("code") String code,
            @RequestParam(value = "state", defaultValue = "") String backUrl,
            HttpServletResponse response
    ) throws WeiboException, JSONException {

        Oauth oauth = new Oauth();
        AccessToken token = oauth.getAccessTokenByCode(code);
        String accessToken = token.getAccessToken();
        String weiboUid = token.getUid();

        DbSession dbSession = this.getDbSession();
        Entity weiboUser = dbSession.queryOneRawSql("weiboUser", "uid = ?", new Object[]{weiboUid});
        if (weiboUser == null) {
            weiboUser = dbSession.newEntity("weiboUser");
            weiboUser.setValue("uid", weiboUid);
        }

        Users users = new Users(accessToken);
        User u = users.showUserById(weiboUid);
        logger.info("Weibo connected user " + u);

        weiboUser.setValue("tokenExpiresIn", token.getExpireIn());
        weiboUser.setValue("accessToken", accessToken);
        weiboUser.setValue("refreshToken", token.getRefreshToken());
        weiboUser.setValue("headImgUrl", u.getavatarLarge());
        weiboUser.setValue("name", u.getName());
        weiboUser.setValue("gender", u.getGender());
        weiboUser.setValue("verified", u.isVerified());
        weiboUser.setValue("verifiedReason", u.getVerifiedReason());

        /*
        Weibo connected user User
        [
            id=1640099734,
            screenName=尹伟铭,
            name=尹伟铭,
            province=11,
            city=1,
            location=北京 东城区,
            description=The people who are crazy enough to think they can change the world, are the ones who do.,
            url=http://yinwm.com,
            profileImageUrl=http://tp3.sinaimg.cn/1640099734/50/5731738782/1,
            userDomain=yinwm,
            gender=m,
            followersCount=1313,
            friendsCount=738,
            statusesCount=3594,
            favouritesCount=353,
            createdAt=Fri Aug 28 16:35:02 CST 2009,
            following=false,
            verified=true,
            verifiedType=0,
            allowAllActMsg=false,
            allowAllComment=true,
            followMe=false,
            avatarLarge=http://tp3.sinaimg.cn/1640099734/180/5731738782/1,
            onlineStatus=0,
            status=Status [user=null, idstr=3951080792782641, createdAt=Wed Mar 09 11:35:31 CST 2016, id=3951080792782641, text=永远不说我爱你, source=Source [url=http://weibo.com/, relationShip=nofollow, name=微博 weibo.com], favorited=false, truncated=false, inReplyToStatusId=-1, inReplyToUserId=-1, inReplyToScreenName=, thumbnailPic=http://ww4.sinaimg.cn/thumbnail/61c1ef96jw1f1qggu6ljqj20go0cdmyz.jpg, bmiddlePic=http://ww4.sinaimg.cn/bmiddle/61c1ef96jw1f1qggu6ljqj20go0cdmyz.jpg, originalPic=http://ww4.sinaimg.cn/large/61c1ef96jw1f1qggu6ljqj20go0cdmyz.jpg, retweetedStatus=null, geo=null, latitude=-1.0, longitude=-1.0, repostsCount=0, commentsCount=0, mid=3951080792782641, annotations=, mlevel=0, visible=Visible [type=0, list_id=0]],
            biFollowersCount=336,
            remark=null,
            lang=zh-cn,
            verifiedReason=有福妈妈CTO尹伟铭,
            weihao=,
            statusId=]
         */

        dbSession.save(weiboUser);

        String url = null;
        if (backUrl.contains("?")) {
            url = backUrl + "&weiboUid=" + weiboUid;
        } else {
            url = backUrl + "?weiboUid=" + weiboUid;
        }

        Cookie c = new Cookie("cbUrl", "");
        c.setDomain("/");
        response.addCookie(c);

        logger.info("Weibo redirect url " + url);

        return "redirect:" + url;

    }

}
