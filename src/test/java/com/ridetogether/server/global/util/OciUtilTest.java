package com.ridetogether.server.global.util;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.ConfigFileReader.ConfigFile;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.GetBucketRequest;
import com.oracle.bmc.objectstorage.requests.GetBucketRequest.Fields;
import com.oracle.bmc.objectstorage.requests.GetNamespaceRequest;
import com.oracle.bmc.objectstorage.responses.GetBucketResponse;
import com.oracle.bmc.objectstorage.responses.GetNamespaceResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class OciUtilTest {

	@Test
	public void Test() throws IOException {
		final String bucket = "RideTogetherHYU_Bucket";

		ConfigFile config = ConfigFileReader.parse("~/.oci/config", "DEFAULT");

		AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(config);


		ObjectStorage client = new ObjectStorageClient(provider);
		client.setRegion(Region.AP_CHUNCHEON_1);

		System.out.println("Getting the namespace.");
		GetNamespaceResponse namespaceResponse = client.getNamespace(GetNamespaceRequest.builder().build());

		String namespaceName = namespaceResponse.getValue();

		System.out.println("Creating Get bucket request");
		List<Fields> fieldsList = new ArrayList<>(2);
		fieldsList.add(GetBucketRequest.Fields.ApproximateCount);
		fieldsList.add(GetBucketRequest.Fields.ApproximateSize);
		GetBucketRequest request =
				GetBucketRequest.builder()
						.namespaceName(namespaceName)
						.bucketName(bucket)
						.fields(fieldsList)
						.build();

		System.out.println("Fetching bucket details");
		GetBucketResponse response = client.getBucket(request);

		System.out.println("Bucket Name : " + response.getBucket().getName());
		System.out.println("Bucket Compartment : " + response.getBucket().getCompartmentId());
		System.out.println(
				"The Approximate total number of objects within this bucket : "
						+ response.getBucket().getApproximateCount());
		System.out.println(
				"The Approximate total size of objects within this bucket : "
						+ response.getBucket().getApproximateSize());

	}

}
