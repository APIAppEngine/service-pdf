package apiserver;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * Created by mnimer on 5/17/14.
 */

@EnableWebMvc
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter
{
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters)
    {
        super.configureMessageConverters(converters);
    }
}