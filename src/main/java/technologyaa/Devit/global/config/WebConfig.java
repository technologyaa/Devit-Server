package technologyaa.Devit.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${spring.file.upload.profile-dir}")
    private String profileDir;

    @Value("${spring.file.upload.project-dir}")
    private String projectDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/profiles/**")
                .addResourceLocations("file:"+profileDir+"/");

        registry.addResourceHandler("/uploads/projects/**")
                .addResourceLocations("file:"+projectDir+"/");
    }
}
