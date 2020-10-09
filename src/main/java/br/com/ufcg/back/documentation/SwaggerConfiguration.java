package br.com.ufcg.back.documentation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    public class ApplicationSwaggerConfig {

        @Bean
        public Docket api() {
            return new Docket(DocumentationType.SWAGGER_2)
                    .select()
                    .apis(RequestHandlerSelectors.basePackage("br.com.ufcg.back.controllers"))
                    .paths(PathSelectors.any())
                    .build()
                    .useDefaultResponseMessages(false)
                    .globalResponseMessage(RequestMethod.GET, responseMessageForGET())
                    .apiInfo(apiInfo());
        }

        private List<ResponseMessage> responseMessageForGET() {
            return new ArrayList<ResponseMessage>() {{
                add(new ResponseMessageBuilder()
                        .code(500)
                        .message("Erro interno no servidor.")
                        .responseModel(new ModelRef("Erro Interno!"))
                        .build());

            }};
        }

        private ApiInfo apiInfo() {
            return new ApiInfoBuilder()
                    .title("Lets Work Together (Backend)")
                    .description("Documentação swagger do backend da aplicação: Lets Work Together!")
                    .version("1.0.0")
                    .license("Apache License Version 2.0")
                    .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
                    .build();
        }
    }
}
