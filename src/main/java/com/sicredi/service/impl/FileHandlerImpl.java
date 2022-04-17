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
	public List<List<String>> processFileBySynchronizer(String csvPath, SynchronizeService synchronize) throws IOException {
		log.info("Trying reading the csv file");
		treateNewCsvPathName(csvPath);
		
		List<List<String>> records = new ArrayList<>();
		
		try (Scanner scanner = new Scanner(new File(csvPath));) {
			csvHeader = scanner.nextLine();
			appendToNewCsv(null, true);
			
			while (scanner.hasNextLine()) {
				records.add(getRecordFromLine(scanner.nextLine()));

				if (countBygroup == counterRecord) {
					appendToNewFileInBackground(synchronize.run(records));
					records = new ArrayList<>();
					counterRecord = 0;
				} else {
					counterRecord++;
				}
			}
			if(!records.isEmpty())
				appendToNewFileInBackground(synchronize.run(records));
		} catch (FileNotFoundException e) {
			log.error("CSV not found");
			throw e;
		}
		log.info("Csv file read sucessufuly");
		return records;

	}
	
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
	
	private void appendToNewFileInBackground(List<List<String>> newRecords) {
		new Thread(() -> {
			try {
				appendToNewCsv(newRecords,false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	
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

	private void treateNewCsvPathName(String csvPath) {
		String nameWithoutExtension = csvPath.substring(0, csvPath.lastIndexOf('.'))
				.substring(csvPath.lastIndexOf('\\') + 1);
		String extensionFile = csvPath.substring(csvPath.lastIndexOf('.') + 1);
		String path = csvPath.replace(nameWithoutExtension+"."+extensionFile,"");
		String date = String.valueOf(new Date().getTime());

		this.newCsvPathName = String.format("%s\\%s_%s.%s", path, nameWithoutExtension, date, extensionFile);
	}

}
