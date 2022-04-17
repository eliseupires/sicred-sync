package com.sicredi.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sicredi.service.ReceitaService;
import com.sicredi.service.SynchronizeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SynchronizeServiceImpl implements SynchronizeService{
	@Autowired
	private ReceitaService receitaService;
	
	@Override
	public List<List<String>> run(List<List<String>> rows) throws IOException {
		
		List<List<String>> newRecords = new ArrayList<>();
		
		log.info("Starting synchronization of records' group");
		for (List<String> row : rows) {
			String[] vRow = row.toArray(new String[row.size()]);
			boolean sincronized = false;
			try {
				sincronized = receitaService.atualizarConta(vRow[0], vRow[1].replace("-", ""), Double.parseDouble(vRow[2].replace(",", ".")), vRow[3]);
			} catch (RuntimeException | InterruptedException e) {
				e.printStackTrace();
			}
			
			List<String> newRow = new ArrayList<>(row);
			newRow.add(String.valueOf(sincronized));
			
			newRecords.add(newRow);
		}
		
		log.info("Finishing synchronization of records' group");
		return newRecords;
		
	}
	
}
