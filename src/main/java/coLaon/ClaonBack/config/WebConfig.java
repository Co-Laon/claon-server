package coLaon.ClaonBack.config;

import coLaon.ClaonBack.center.web.converter.CenterReportTypeConverter;
import coLaon.ClaonBack.center.web.converter.CenterSearchOptionConverter;
import coLaon.ClaonBack.post.web.converter.PostReportTypeConverter;
import coLaon.ClaonBack.user.web.converter.MetropolitanAreaConverter;
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
        registry.addConverter(new MetropolitanAreaConverter());
        registry.addConverter(new AppStoreConverter());
        registry.addConverter(new PostReportTypeConverter());
        registry.addConverter(new CenterReportTypeConverter());
    }
}
