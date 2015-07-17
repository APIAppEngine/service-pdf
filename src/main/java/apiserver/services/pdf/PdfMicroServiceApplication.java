package apiserver.services.pdf;

import apiserver.filters.MashapeAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.DispatcherType;
import javax.servlet.MultipartConfigElement;
import java.util.EnumSet;

/**
 * Created by mnimer on 5/5/14.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableWebMvc
@ImportResource( {"pdf-flow-config.xml","cache-flow-config.xml"} )
public class PdfMicroServiceApplication extends DelegatingWebMvcConfiguration
{

    public static void main(String[] args)
    {
        SpringApplication.run(PdfMicroServiceApplication.class, args);
    }

    @Override
    protected void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }



    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("100MB");
        factory.setMaxRequestSize("110MB");
        return factory.createMultipartConfig();
    }



    @Value("${mashape.key}")
    private String mashapeKey = null;

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {

        MashapeAuthFilter mashapeAuthFilter = new MashapeAuthFilter();
        mashapeAuthFilter.setMashapeKey(mashapeKey);

        FilterRegistrationBean registration = new FilterRegistrationBean();
        //registration.setFilter(new MetricsFilter());
        registration.setFilter(mashapeAuthFilter);

        registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
        return registration;
    }


}
