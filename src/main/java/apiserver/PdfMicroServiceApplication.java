package apiserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.MultipartConfigElement;

/**
 * Created by mnimer on 5/5/14.
 */
@Configuration
@EnableWebMvc
@EnableAutoConfiguration
@ComponentScan
@PropertySource("classpath:config/application.properties")
@ImportResource( {"classpath:pdf-flow-config.xml"} )
public class PdfMicroServiceApplication extends WebMvcConfigurerAdapter  implements EmbeddedServletContainerCustomizer
{

    public static void main(String[] args)
    {
        SpringApplication.run(PdfMicroServiceApplication.class, args);
    }


    @Override public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        registry.addResourceHandler("/api-docs/**").addResourceLocations("classpath:/META-INF/resources/api-docs/");
    }


    @Override
    public void customize(ConfigurableEmbeddedServletContainer configurableEmbeddedServletContainer)
    {
        //registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        //
    }


    //@Bean
    //public ServletRegistrationBean

    /***
    @Bean
    public WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter WebMvcConfiguration()
    {
        return new LocalWebMvcConfiguration();
    }

    public class LocalWebMvcConfiguration extends WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter
    {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry)
        {
            super.addResourceHandlers(registry);
        }
    }
     ***/

    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("100MB");
        factory.setMaxRequestSize("200MB");
        return factory.createMultipartConfig();
    }

}
