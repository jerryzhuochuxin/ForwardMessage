package com.jerryzhuo;

import com.aliyun.openservices.iot.api.Profile;
import com.aliyun.openservices.iot.api.message.MessageClientFactory;
import com.aliyun.openservices.iot.api.message.callback.MessageCallback;
import com.aliyun.openservices.iot.api.message.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Properties;

/**
 * author jerryzhuo
 */
public class Main {
    private static Properties config;
    private static RestTemplate restHttpTools;
    private static Logger logger;
    private static String forwardUrl;

    /**
     * 启动类
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws Exception {
        Init.init();
        ForMessage.forwardMessage();
    }

    /**
     * 初始化配置类
     * author jerryzhuo
     */
    private static class Init {
        /**
         * 初始化静态变量
         *
         * @throws IOException
         */
        private static void init() throws Exception {
            restHttpTools = new RestTemplate();
            config = getConfig();
            logger = LoggerFactory.getLogger(Main.class);
            forwardUrl = config.getProperty("url");
            checkConfig();
        }

        /**
         * 检查配置的所有字段是否为空
         *
         * @throws Exception
         */
        private static void checkConfig() throws Exception {
            checkStringIfNullOrThrowException("accessKey is null", config.getProperty("accessKey"));
            checkStringIfNullOrThrowException("accessSecret is null", config.getProperty("accessSecret"));
            checkStringIfNullOrThrowException("regionId is null", config.getProperty("regionId"));
            checkStringIfNullOrThrowException("uid is null", config.getProperty("uid"));
            checkStringIfNullOrThrowException("url is null", config.getProperty("url"));
        }

        /**
         * 得到配置文件
         *
         * @return
         * @throws IOException
         */
        private static Properties getConfig() throws IOException {
            Properties result = new Properties();
            result.load(new ClassPathResource("myProp.properties").getInputStream());
            return result;
        }

        /**
         * 检查字符串是否为空，如果是则抛出errorMessage
         *
         * @param errorMessage
         * @param checkStringValue
         * @throws Exception
         */
        private static void checkStringIfNullOrThrowException(String errorMessage, String checkStringValue) throws Exception {
            if (checkStringValue == null || "".equals(checkStringValue)) {
                throw new Exception(errorMessage);
            }
        }
    }

    /**
     * author jerryzhuo
     * 转发消息类
     */
    private static class ForMessage {
        /**
         * 得到请求对象
         * @param m 消息对象
         * @return
         */
        private static MultiValueMap<String, HttpEntity<?>> getRequestObject(Message m) {
            MultiValueMap<String, HttpEntity<?>> result = new MultipartBodyBuilder().build();

            result.set("payload", new HttpEntity<>(new String(m.getPayload())));
            result.set("messageId", new HttpEntity<>(m.getMessageId()));
            result.set("topic", new HttpEntity<>(m.getTopic()));
            result.set("generateTime", new HttpEntity<>(String.valueOf(m.getGenerateTime())));
            result.set("qos", new HttpEntity<>(String.valueOf(m.getQos())));

            return result;
        }

        /**
         * 转发消息
         */
        private static void forwardMessage() {
            MessageClientFactory
                    .messageClient(
                            Profile.getAccessKeyProfile(
                                    new StringBuilder()
                                            .append("https://")
                                            .append(config.getProperty("uid"))
                                            .append(".iot-as-http2.")
                                            .append(config.getProperty("regionId"))
                                            .append(".aliyuncs.com")
                                            .toString(),
                                    config.getProperty("regionId"),
                                    config.getProperty("accessKey"),
                                    config.getProperty("accessSecret")
                            )
                    )
                    .connect(
                            messageToken -> {
                                logger.info(
                                        restHttpTools
                                                .postForObject(
                                                        forwardUrl,
                                                        getRequestObject(messageToken.getMessage()),
                                                        String.class
                                                )
                                );
                                return MessageCallback.Action.CommitSuccess;
                            }
                    );
        }
    }
}
