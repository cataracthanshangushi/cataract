package com.taitan.system.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger 配置
 * <p>
 * Spring Doc FAQ: https://springdoc.org/#faq
 *
 * @author haoxr
 * @date 2023/2/17
 */
@Configuration
public class SwaggerConfig {

    /**
     * 接口信息
     */
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("Authorization",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer").bearerFormat("JWT")
                        )
                )
                .info(new Info()
                        .title("taitan-boot 接口文档")
                        .version("2.0.0")
                        .description("接口文档")
                        .license(new License().name("Apache 2.0")
                                .url("https://www.youlai.tech"))
                );
    }

    /**
     * 接口分组-系统接口
     *
     * @return
     */
    @Bean
    public GroupedOpenApi groupedOpenApi() {
        String paths[] = {"/api/**"};

        String packagesToScan[] = {"com.taitan.system.controller"};
        return GroupedOpenApi.builder().group("系统接口")
                .packagesToScan(packagesToScan)
                .pathsToMatch(paths)
                .build();
    }







}
