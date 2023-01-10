package com.sprinteins.drupalcli.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpClient;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;


public class DownloadFileClient extends FileClient {

	private final String uploadBaseUri;

	public DownloadFileClient(ObjectMapper objectMapper, String baseUri, String apiKey, HttpClient httpClient) {
		super(objectMapper, baseUri, apiKey, httpClient);
		this.uploadBaseUri = baseUri + "/file/upload/paragraph/downloads/field_file_to_upload?_format=json";
	}

	public FileUploadModel upload(Path path) throws NoSuchAlgorithmException {
			return super.upload(path, this.uploadBaseUri);
	}
}
