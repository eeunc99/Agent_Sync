package com.app.daol.config;

import com.app.daol.util.Aes;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * application.properties 내 DB 접속정보 등을 암복호화하기 위한 jasypt 설정.
 * 마스터 키(jasypt.encryptor.key)는 AES로 한 번 더 감싸 전달하며,
 * 실제 값은 환경변수/시크릿 매니저에서 주입한다.
 */
@EnableEncryptableProperties
@Configuration
public class JasyptConfig {

	@Value("${jasypt.encryptor.key}")
	private String aesEncryptorKey;

	@Bean("jasyptStringEncryptor")
	StringEncryptor databaseStringEncryptor() throws Exception {
		String encryptorKey = Aes.getInstance().decryptString(aesEncryptorKey);

		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();
        config.setPassword(encryptorKey);
        encryptor.setConfig(config);
        return encryptor;
    }
}
