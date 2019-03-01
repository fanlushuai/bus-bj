package name.auh.bus.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
//@Profile({"dev"}) 线上环境请禁用
public class SwaggerConfig {

    /**
     * 使用方法参见 {@link io.swagger.annotations} 包下各个注解的说明
     */
    @Bean
    public Docket api() {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .description("基于spring boot + jpa + SeimiCrawler2.0 实现。" +
                        "注意：1、基础数据已经提前抓取，在bus_db.mv.db，项目启动之后。通过http://localhost:1111/h2-console访问。账号sa,无密码" +
                        "2、一般不用更新基础数据，如果要更，请使用 抓取控制->启动抓取，更新车站数据库资源 。（name.auh.bus.controller.SrawlerController.updateLineData）")
                .title("北京实时公交爬虫系统 （www.bjbus.com）")
                .build();

        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo).select().apis(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.boot"))).build();
    }
}