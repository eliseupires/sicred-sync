package com.sicredi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.sicredi.service.FileHandlerService;
import com.sicredi.service.SynchronizeService;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class SicrediSynchronizerApplication implements CommandLineRunner {

	@Autowired
	private FileHandlerService fileHandlerService;
	@Autowired
	private SynchronizeService synchronizeService;

	public static void main(String[] args) {
		SpringApplication.run(SicrediSynchronizerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (args.length > 1) {
			log.error("unexpected parameter: {}", args[1]);
			return;
		}

		if (args.length == 0) {
			log.error("expected parameter <input-file>");
			return;
		}
		log.info("Starting synchronization process");
		try {
			//é passado como parametro o path csv juntamente com a estratégia de sicronização
			fileHandlerService.processFileBySynchronizer(args[0], synchronizeService);
		} catch (Exception e) {
			log.error("Finishing process unsuccessfully. Details: {}", e.getMessage());
		}
		log.info("Finishing synchronization process");
	}
}
