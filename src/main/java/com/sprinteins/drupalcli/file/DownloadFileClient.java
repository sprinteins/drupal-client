package com.sprinteins.drupalcli.file;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;


public class DownloadFileClient extends FileClient {

	private final String uploadBaseUri;

	public DownloadFileClient(ObjectMapper objectMapper, String baseUri, String apiKey, HttpClient httpClient) {
		super(objectMapper, baseUri, apiKey, httpClient);
		this.uploadBaseUri = baseUri + "/file/upload/paragraph/downloads/field_file_to_upload";
	}

	public FileUploadModel upload(Path path) throws IOException {
			return super.upload(path, this.uploadBaseUri);
	}
}
