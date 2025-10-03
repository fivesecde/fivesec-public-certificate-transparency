package de.fivesec.ca_stream

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(scanBasePackages = ["de.fivesec.ca_stream"])
@ConfigurationPropertiesScan
@EnableScheduling
class CaStreamApplication

fun main(args: Array<String>) {
	runApplication<CaStreamApplication>(*args)
}
