package coLaon.ClaonBack.config;

import coLaon.ClaonBack.center.web.converter.CenterSearchOptionConverter;
import coLaon.ClaonBack.user.web.converter.OAuth2ProviderConverter;
import coLaon.ClaonBack.version.web.converter.AppStoreConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new CenterSearchOptionConverter());
        registry.addConverter(new OAuth2ProviderConverter());
        registry.addConverter(new AppStoreConverter());
    }
}
