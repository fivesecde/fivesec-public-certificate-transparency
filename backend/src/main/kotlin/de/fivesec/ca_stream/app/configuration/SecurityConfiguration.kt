package de.fivesec.ca_stream.app.configuration

import de.fivesec.ca_stream.app.authentication.ApiKeyAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class SecurityConfiguration(
    private val certLogConfiguration: CertLogConfiguration
) {

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain? {
        return http
            .csrf { it.disable() }
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests {
                it.anyRequest().authenticated()
            }
            .addFilterBefore(
                ApiKeyAuthenticationFilter(
                    certLogConfiguration.apiKey,
                    PathPatternRequestMatcher.withDefaults().matcher("/api/v1/ca/**"),
                ), UsernamePasswordAuthenticationFilter::class.java
            )
            .build()
    }

    @Bean
    fun configureContentNegotiation(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer) {
                configurer.defaultContentType(MediaType.APPLICATION_JSON)
            }
        }
    }

    @Bean
    fun corsAndInterceptorConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**").allowedMethods("*").allowedOrigins("*")
            }
        }
    }
}