package com.pat.app.cwtool;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Launcher {
	private static Logger s_logger = LoggerFactory.getLogger(Launcher.class);

	public static void main(String[] args) throws IOException {
		String userDir = System.getProperty("user.dir");
		File currentFolder = new File(userDir);
		File[] dataFiles = currentFolder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("财务明细账") || name.startsWith("银行明细账");
			}
		});
		if (dataFiles.length == 0) {
			System.err.println("当前目录找不到财务明细账或银行明细账文件, 请仅放置要处理的明细账文件在当前目录.");
		}
		String bankSheetPath = null;
		String financeSheetPath = null;
		for (File dataFile : dataFiles) {
			if (dataFile.getName().contains("财务明细账")) {
				financeSheetPath = dataFile.getAbsolutePath();
			} else if (dataFile.getName().contains("银行明细账")) {
				bankSheetPath = dataFile.getAbsolutePath();
			}
		}

		if (bankSheetPath == null || financeSheetPath == null) {
			System.err.println("当前目录找不到财务明细账或银行明细账文件, 请仅放置要处理的明细账文件在当前目录.");
		}

		String[] batchArgs = { "-bankSheetPath=" + bankSheetPath, "-financeSheetPath=" + financeSheetPath };
		System.exit(SpringApplication.exit(SpringApplication.run(CwToolConfig.class, batchArgs)));
	}

}
