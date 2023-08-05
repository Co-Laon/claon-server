package claon.config;

import claon.center.web.converter.CenterSearchOptionConverter;
import claon.user.web.converter.OAuth2ProviderConverter;
import claon.version.web.converter.AppStoreConverter;
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
