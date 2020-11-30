package com.blz.addressBook;

import java.sql.Date;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class AddressBookRESTAPITest {

	AddressBookService serviceObj;

	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
		serviceObj = new AddressBookService();
	}

	public AddressBookData[] getAddressBookContactList() {
		Response response = RestAssured.get("/addressBook");
		AddressBookData gsonToContacts[] = new Gson().fromJson(response.asString(), AddressBookData[].class);
		return gsonToContacts;
	}

	@Test
	public void givenContactsInJsonServer_WhenRetrieved_ShouldMatchTotalCount() {
		AddressBookData gsonContacts[] = getAddressBookContactList();
		AddressBookRestAPI restApiObj;
		restApiObj = new AddressBookRestAPI(Arrays.asList(gsonContacts));
		long count = restApiObj.countREST_IOEntries();
		Assert.assertEquals(3, count);
	}

	@Test
	public void addedNewEmployee_ShouldMatch201ResponseAndTotalCount() throws AddressBookException {
		AddressBookData gsonContacts[] = getAddressBookContactList();
		serviceObj.addNewContactsUsingRestAPI(Arrays.asList(gsonContacts));
		AddressBookData newContact = new AddressBookData(5, "Fnamethree", "Lnamethree", Date.valueOf("2019-01-03"), "Home",
				"Charai", "Thane", "Maharashtra", 400601, "9001002005", "fnamethree@gmail.com");
		Response response = addContactToJsonServer(newContact);
		int HTTPstatusCode = response.getStatusCode();
		Assert.assertEquals(201, HTTPstatusCode);
		newContact = new Gson().fromJson(response.asString(), AddressBookData.class);
		serviceObj.addNewContactsUsingRestAPI(Arrays.asList(newContact));
		long count = serviceObj.entryCount();
		Assert.assertEquals(3, count);
	}

	public Response addContactToJsonServer(AddressBookData newContact) {
		String gsonString = new Gson().toJson(newContact);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(gsonString);
		return request.post("/addressBook");
	}
}
