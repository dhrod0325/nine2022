package basic.test.basic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Test5 {
    public static void main(String[] args) throws IOException {
        validCheck();
//
//
//        String a = "0.walk(1 4,128.0:4<478 128.1:4 128.2:4<479 128.3:4)\n" +
//                "\t1.attack(1 6,144.0:3 144.1:3<1519<1517 144.2:1[1520 144.3:1! 144.4:3 144.5:2)\n" +
//                "\t2.damage(1 3,160.0:2 160.1:3[1522[904 160.2:3)\n" +
//                "\t3.breath(1 12,136.11:4 136.0:4 136.1:4 136.2:4 136.3:4 136.4:4 136.5:4 136.6:4 136.7:4 136.8:4 136.9:4 136.10:4)\n" +
//                "\t4.walk onehand sword(1 4,32.0:4<478 32.1:4 32.2:4<479 32.3:4)\n" +
//                "\t5.attack onehand sword(1 6,48.0:1 48.1:3 48.2:2<248<701 48.3:1! 48.4:4 48.5:3)\n" +
//                "\t6.damage onehand sword(1 3,56.0:2 56.1:3[1522[904 56.2:3)\n" +
//                "\t7.breath onehand sword(1 12,40.11:4 40.0:4 40.1:4 40.2:4 40.3:4 40.4:4 40.5:4 40.6:4 40.7:4 40.8:4 40.9:4 40.10:4)\n" +
//                "\t8.death(1 20,232.19:4 232.0:4<1532 232.1:4 232.2:4 232.3:3 232.4:2 232.5:2 232.6:2 232.7:2 232.8:4 232.9:4 232.10:4 232.11:4 232.12:4 232.13:4 232.14:4 232.15:4 232.16:4 232.17:4 232.18:4)\n" +
//                "\t11.walk axe(1 4,128.0:4<478 128.1:4 128.2:4<479 128.3:4)\n" +
//                "\t12.attack axe(1 6,144.0:3 144.1:5 144.2:1<1519<1517 144.3:1! 144.4:4 144.5:2)\n" +
//                "\t13.damage axe(1 3,160.0:2 160.1:3[1522[904 160.2:3)\n" +
//                "\t14.breath axe(1 12,136.11:4 136.0:4 136.1:4 136.2:4 136.3:4 136.4:4 136.5:4 136.6:4 136.7:4 136.8:4 136.9:4 136.10:4)\n" +
//                "\t18.spell direction(1 8,208.0:2 208.1:3 208.2:3 208.3:2 208.4:1 208.5:2! 208.6:2 208.7:1)\n" +
//                "\t19.spell no direction(1 9,216.0:2 216.1:1 216.2:2 216.3:4 216.4:3 216.5:1 216.6:2! 216.7:2 216.8:1)\n" +
//                "\t20.walk cross bow(1 4,64.0:4<478 64.1:4 64.2:4<479 64.3:4)\n" +
//                "\t21.attack cross bow(1 7,80.0:3 80.1:1 80.2:3 80.3:4<461 80.4:3! 80.5:3 80.6:1)\n" +
//                "\t22.damage cross bow(1 3,88.0:2 88.1:3[1522[904 88.2:3)\n" +
//                "\t23.breath cross bow(1 12,72.11:4 72.0:4 72.1:4 72.2:4 72.3:4 72.4:4 72.5:4 72.6:4 72.7:4 72.8:4 72.9:4 72.10:4)\n" +
//                "\t31.spell direction extra(1 9,224.0:2 224.1:1 224.2:2 224.3:2 224.4:2 224.5:4! 224.6:2 224.7:2 224.8:1)\n" +
//                "\t46.walk dagger(1 4,0.0:4<478 0.1:4 0.2:4<479 0.3:4)\n" +
//                "\t47.attack dagger(1 6,16.0:2 16.1:3 16.2:2<901<902 16.3:1! 16.4:3 16.5:2)\n" +
//                "\t48.damage dagger(1 3,24.0:3 24.1:3[1522[904 24.2:4)\n" +
//                "\t49.breath dagger(1 12,8.11:4 8.0:4 8.1:4 8.2:4 8.3:4 8.4:4 8.5:4 8.6:4 8.7:4 8.8:4 8.9:4 8.10:4)\n" +
//                "\t54.walk double sword(1 4,168.0:4<478 168.1:4 168.2:4<479 168.3:4)\n" +
//                "\t55.attack double sword(1 9,184.0:1 184.1:1 184.2:2 184.3:1<1524<1525 184.4:2! 184.5:2 184.6:1 184.7:2 184.8:2)\n" +
//                "\t56.damage double sword(1 3,200.0:2 200.1:3[1522[904 200.2:3)\n" +
//                "\t57.breath double sword(1 12,176.11:4 176.0:4 176.1:4 176.2:4 176.3:4 176.4:4 176.5:4 176.6:4 176.7:4 176.8:4 176.9:4 176.10:4)\n" +
//                "\t58.walk claw(1 4,128.0:4<478 128.1:4 128.2:4<479 128.3:4)\n" +
//                "\t59.attack claw(1 6,144.0:2 144.1:4 144.2:1<1517<1518<1519 144.3:2! 144.4:3 144.5:2)\n" +
//                "\t60.damage claw(1 3,160.0:2 160.1:3[1522[904 160.2:3)\n" +
//                "\t61.breath claw(1 12,136.11:4 136.0:4 136.1:4 136.2:4 136.3:4 136.4:4 136.5:4 136.6:4 136.7:4 136.8:4 136.9:4 136.10:4)\n" +
//                "\t62.walk shuriken(1 4,96.0:4<478 96.1:4 96.2:4<479 96.3:4)\n" +
//                "\t63.attack shuriken(1 6,112.0:3 112.1:3 112.2:3 112.3:2<1513 112.4:4! 112.5:3)\n" +
//                "\t64.damage shuriken(1 3,120.0:2 120.1:3[1522[904 120.2:3)\n" +
//                "\t65.breath shuriken(1 12,104.11:4 104.0:4 104.1:4 104.2:4 104.3:4 104.4:4 104.5:4 104.6:4 104.7:4 104.8:4 104.9:4 104.10:4)\n" +
//                "\t80.alt attack double sword(1 8,192.0:2 192.1:3 192.2:1 192.3:1<248<701 192.4:1! 192.5:1 192.6:2 192.7:3)\n" +
//                "\t81.alt attack claw(1 6,152.0:2 152.1:3 152.2:2<1517<1518<1519 152.3:2! 152.4:3 152.5:2)\n" +
//                "\t101.shadow(13734)\n" +
//                "\t105.clothes(2 13951 13952)\n" +
//                "\t102.type(10)";
//
//        List<String> slowList = new ArrayList<>();
//        List<String> fastList = new ArrayList<>();
//
//        for (String k : a.split("\n")) {
//            String ss = k.trim();
//
//            String te = ss.substring(ss.indexOf(".") + 1);
//
//            if (te.startsWith("type") || te.startsWith("walk") || te.startsWith("breath") || te.startsWith("shadow") || te.startsWith("clothes")) {
//                slowList.add(ss);
//            } else {
//                fastList.add(ss);
//            }
//        }
//
//        System.out.println("110.framerate(25)");
//        for (String s : fastList) {
//            System.out.println(s);
//        }
//
//        System.out.println("110.framerate(24)");
//        for (String s : slowList) {
//            System.out.println(s);
//        }


//        for (int i = 8; i < 52; i++) {
//            System.out.println("dex:" + i + "=" + CalcStat.calcStatBowHitUp(i));
//        }
    }

    public static void validCheck() throws IOException {
        List<String> f = Files.readAllLines(Paths.get("source/update/TW13081901.txt"));
        List<String> sss = new ArrayList<>();
        String prev = "";

        List<String> intList = new ArrayList<>();

        int preva = -1;

        for (String s : f) {
            if (s.isEmpty()) {
                System.out.println(preva);
                continue;
            }

            if (s.startsWith("#")) {
                String k = s.replace("\t", " ");
                String[] t = k.split(" ");

                String key = t[0].replace("#", "");

                int v = Integer.parseInt(key);

                if (preva + 1 != v) {
                    System.out.println(v);
                }

                preva = v;
            }

            sss.add(s);
        }
    }

    public static String createFrameRate(String frameString, int start, String s2, String polyName, int framerate) {

        List<String> slowList = new ArrayList<>();
        List<String> fastList = new ArrayList<>();

        for (String k : frameString.split("\n")) {
            String ss = k.trim();

            String te = ss.substring(ss.indexOf(".") + 1);

            if (te.startsWith("type") || te.startsWith("walk") || te.startsWith("breath")) {
                slowList.add(ss);
            } else {
                fastList.add(ss);
            }
        }

        System.out.println("110.framerate(25)");
        for (String s : fastList) {
            System.out.println(s);
        }

        System.out.println("110.framerate(24)");
        for (String s : slowList) {
            System.out.println(s);
        }

        return null;
    }
}
