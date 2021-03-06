package ks;

import basic.test.basic.SprCheckTest;
import ks.app.LineageApplication;
import ks.constants.L1ItemTypes;
import ks.run.*;
import ks.test.*;
import ks.util.L1ServerUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import web.socket.L1WebApiData;
import web.socket.L1WebApiListener;

import javax.annotation.Resource;


@SpringBootTest(
        properties = {
                "spring.config.location=file:data/config/",
                "spring.profiles.active=local",
                "l1j.server.mode=test"
        },
        classes = {
                LineageApplication.class
        }
)
public class TestApplication {

    private static final Logger logger = LogManager.getLogger();

    @Resource
    private ElfBlessingTest elfBlessingTest;

    @Test
    public void moveTest() {
        new MoveTest().test();
    }

    @Test
    public void dragonArmorTest() {
        new DragonArmorTest().papooTest();
    }

    @Test
    public void adenBoardTest() {
        new AdenBoardTest().test();
    }

    @Test
    public void weaponAttrDamageTest() {
        new WeaponAttrDamageTest().test();
    }

    @Test
    public void magicDamageTest() {
        MagicDamageTest test = new MagicDamageTest();

        //test.npcToPc();
        test.pcToPc();
    }

    @Test
    public void resolveBugTest() {
        new ResolveBugTest().test();
    }

    @Test
    public void adenaUpdateTest() {
        new AdenaUpdateTest().test();
    }

    @Test
    public void huntDropCheck() {
        new HuntDropTest().íëě§ęł();
    }

    @Test
    public void updateSprList() {
        new UpdateSprTest().test(460000229);
    }

    @Test
    public void elfBlessingTest() {
        elfBlessingTest.test();
    }

    @Test
    public void test() {
        try {
            for (String a : L1ItemTypes.weaponTypes.keySet()) {
                System.out.println(a + "\t" + L1ItemTypes.weaponTypes.get(a));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void apiTest() {
        L1WebApiData apiData = new L1WebApiData("chat");
        apiData.put("name", "ëŠí°ě¤");
        apiData.put("target", "ëŻ¸ěíźě");
        apiData.put("text", "íě¤í¸");
        apiData.put("type", "ěźë°");

        L1WebApiListener.getInstance().pushApi(apiData);
    }

    @Test
    public void ëŞě¤íě¤í¸() {
        new HitTest().test();
    }

    @Test
    public void reset() {
        L1ServerUtils.getInstance().init();
    }

    @Test
    public void updateTest() {
        new SprCheckTest().update();
    }
}
