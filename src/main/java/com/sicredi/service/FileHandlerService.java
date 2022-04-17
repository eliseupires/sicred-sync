package com.sicredi.service;

import java.io.IOException;
import java.util.List;

public interface FileHandlerService {
	public List<List<String>> processFileBySynchronizer(String csvPath,SynchronizeService sincronize)throws IOException;
	
}
