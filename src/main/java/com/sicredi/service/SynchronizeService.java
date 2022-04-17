package com.sicredi.service;

import java.io.IOException;
import java.util.List;

public interface SynchronizeService {
	List<List<String>> run(List<List<String>> rows) throws  IOException;
}
