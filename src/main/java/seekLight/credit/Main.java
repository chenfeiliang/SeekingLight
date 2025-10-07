package seekLight.credit;


import seekLight.credit.engine.impl.QuotaReqFlowCreditEngine;
import seekLight.credit.flow.QuotaReqFlow;

import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        QuotaReqFlowCreditEngine quotaReqFlowCreditEngine = new QuotaReqFlowCreditEngine();
        QuotaReqFlow quotaReqFlow = new QuotaReqFlow();
        quotaReqFlow.setBusiSno(UUID.randomUUID().toString());
        quotaReqFlow.setStep("");
        quotaReqFlow.setRoute("judgeType,zhihuGenerator,zhiHuPublish");
        quotaReqFlow.putParam("zhihu_question","有人分享一下在Soul的经历吗？反正，有一次在soul上面聊到一个女的，照片发过来挺好，聊了挺久，也聊了s，啥都看了。然后有一天我说去河里游泳，她追过来说要看我游泳，我在河里远远看到了她来，真的是，我想瞬间潜到水里不出来了。但本着礼貌的原则，还是上岸了，跟她对话了几句，我说我要在这里游很久，你有事可以先回去，她说没事，我可以等你游完。真的，面对面看到她时，150+的体重，和照片完全不符，牙齿都是看得到缝的，聊了几句我赶紧回去水里，河里那时候人很多，我愣是在河里泡了一个多小时，她还不走，后面我没办法了，趁着她玩手机，游到对岸，潜水，潜到了下游，再从下游游回来这边，绕到了她后面的更衣间，换了衣服撒腿跑，发现整个人都泡白了");
        quotaReqFlow.putParam("zhiHuPublish_questionId","612883747");
        quotaReqFlowCreditEngine.doFlow(quotaReqFlow);
    }
}