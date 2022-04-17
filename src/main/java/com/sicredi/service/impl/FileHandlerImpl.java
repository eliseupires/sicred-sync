package com.sicredi.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sicredi.service.FileHandlerService;
import com.sicredi.service.SynchronizeService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileHandlerImpl implements FileHandlerService {
	@Value("${file.csv.delimiter}")
	private String delimiter;
	@Value("${sincronize.count.by.group}")
	private int countBygroup;
	@Value("${file.csv.column.synchronized.name}")
	private String synchronizedColumnName;

	private String csvHeader = "";
	private int counterRecord = 0;
	private String newCsvPathName;

	@Override
	public List<List<String>> processFileBySynchronizer(String csvPath, SynchronizeService synchronize)
			throws IOException {
		log.info("Trying reading the csv file");
		treateNewCsvPathName(csvPath);

		List<List<String>> records = new ArrayList<>();
		// uma vez o arquivo carregado corretamente, é scaneado linha por linha
		// agrupando os registros respeitando a quantidade definida no properties
		try (Scanner scanner = new Scanner(new File(csvPath));) {
			log.info("Csv file read sucessufuly");
			// pegando o cabeçalho
			csvHeader = scanner.nextLine();
			// adicionando o cabeçalho ao novo arquivo
			appendToNewCsv(null, true);

			while (scanner.hasNextLine()) {
				records.add(getRecordFromLine(scanner.nextLine()));
				// atindingida a quantidade de agrupamento, é realizado a sincronização e
				// adicionado ao novo csv em thread
				if (countBygroup == counterRecord) {
					appendToNewFileInBackground(synchronize.run(records));
					records = new ArrayList<>();
					counterRecord = 0;
				} else {
					counterRecord++;
				}
			}
			if (!records.isEmpty())
				appendToNewFileInBackground(synchronize.run(records));
		} catch (FileNotFoundException e) {
			log.error("CSV not found");
			throw e;
		}
		
		return records;

	}

	/***
	 * Metodo responsabel em converter uma string em list baseado do delimitador
	 * 
	 * @param line
	 * @return
	 */
	private List<String> getRecordFromLine(String line) {
		List<String> values = new ArrayList<>();
		try (Scanner rowScanner = new Scanner(line)) {
			rowScanner.useDelimiter(delimiter);
			while (rowScanner.hasNext()) {
				values.add(rowScanner.next());
			}
		}
		return values;
	}

	/***
	 * Metodo responsavel em adicionar o novos registros no novo csv em plano de
	 * fundo
	 * 
	 * @param newRecords
	 */
	private void appendToNewFileInBackground(List<List<String>> newRecords) {
		new Thread(() -> {
			try {
				appendToNewCsv(newRecords, false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	/***
	 * Metodo responsavel em adicionar os novos registros no novo csv, podendo
	 * somente colocar o cabeçalho ou os itens baseado o parametro passado
	 * 
	 * @param records
	 * @param header
	 * @throws IOException
	 */
	private void appendToNewCsv(List<List<String>> records, boolean header) throws IOException {
		try (FileWriter csvWriter = new FileWriter(newCsvPathName, true);) {
			if (header) {
				csvWriter.append(csvHeader.concat(delimiter + synchronizedColumnName));
				csvWriter.append("\n");
			} else {
				for (List<String> rowData : records) {
					csvWriter.append(String.join(delimiter, rowData));
					csvWriter.append("\n");
				}
			}
		} catch (IOException e) {
			log.error("Error when trying to create CSV file");
			throw e;
		}
	}

	/***
	 * Metodo responsavel em tratar o nome do novo csv
	 * @param csvPath
	 */
	private void treateNewCsvPathName(String csvPath) {
		String extensionFile = csvPath.substring(csvPath.lastIndexOf('.') + 1);
		String date = String.valueOf(new Date().getTime());

		this.newCsvPathName = csvPath.replaceAll("(?i)\\.[^.\\\\/:*?\"<>|\r\n]+$",
				"_".concat(date).concat(".").concat(extensionFile));
	}

}
