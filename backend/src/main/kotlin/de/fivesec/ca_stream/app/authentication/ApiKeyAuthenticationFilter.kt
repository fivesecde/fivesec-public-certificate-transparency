package de.fivesec.ca_stream.app.authentication

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.RequestMatcher

class ApiKeyAuthenticationFilter(
    private val allowedApiKey: String,
    matcher: RequestMatcher
) : AbstractAuthenticationProcessingFilter(matcher) {

    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication {

        LOG.atInfo().setMessage("Attempting api key authentication for request: ${request?.requestURI}").log()

        val apiKey = request?.getHeader("X-API-KEY")
        if (apiKey == null || apiKey != allowedApiKey) {
            LOG.atError().setMessage("API key invalid").log()
            throw BadCredentialsException("Invalid API key")
        }
        return UsernamePasswordAuthenticationToken(apiKey, null, listOf())
    }

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication
    ) {
        SecurityContextHolder.getContext().authentication = authResult
        chain.doFilter(request, response)
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }
}