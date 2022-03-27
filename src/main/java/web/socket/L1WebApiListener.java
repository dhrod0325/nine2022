package web.socket;

import ks.app.LineageAppContext;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import web.config.WebServerConfig;

import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;

@Component
public class L1WebApiListener {
    private final Logger logger = LogManager.getLogger();

    private final ArrayBlockingQueue<L1WebApiData> data = new ArrayBlockingQueue<>(512);

    private int connectionFailCount = 0;

    public static L1WebApiListener getInstance() {
        return LineageAppContext.getBean(L1WebApiListener.class);
    }

    public void putApi(L1WebApiData vo) {
        try {
            data.put(vo);
        } catch (InterruptedException e) {
            logger.error("오류", e);
        }
    }

    public void pushApi(L1WebApiData vo) {
        try {
            CloseableHttpClient client = HttpClientBuilder.create().build();
            String host = WebServerConfig.WEB_API_HOST + ":" + WebServerConfig.WEB_API_PORT + WebServerConfig.WEB_API_URL;

            logger.debug("웹서버에 API 요청 HOST : {}", host);

            HttpPost post = new HttpPost(host);
            post.setEntity(new UrlEncodedFormEntity(vo.getPairData(), "utf-8"));
            CloseableHttpResponse response = client.execute(post);

            logger.debug("웹 서버 응답 : {}", IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8));

            client.close();
        } catch (ConnectException e) {
            connectionFailCount++;

            logger.error("웹 서버 요청에 실패하였습니다. 실패 횟수 : {}", connectionFailCount);

            if (connectionFailCount > 10) {
                WebServerConfig.WEB_API_USE = false;
                logger.info("웹 서버 사용이 정지되었습니다");
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    @Scheduled(fixedDelay = 200)
    public void scheduled() {
        if (!WebServerConfig.WEB_API_USE) {
            return;
        }

        if (!LineageAppContext.isRun()) {
            return;
        }

        if (!data.isEmpty()) {
            for (int i = 0; i < data.size(); i++) {
                pushApi(data.poll());
            }
        }
    }
}
