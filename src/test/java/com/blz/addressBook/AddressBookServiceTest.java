package com.blz.addressBook;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blz.addressBook.AddressBookService.IOService;

public class AddressBookServiceTest {

	static AddressBookService addressBookService;
	static Map<String, Integer> count;

	@BeforeClass
	public static void AddressBookServiceObj() {
		addressBookService = new AddressBookService();
		count = new HashMap<>();
	}

	@Test
	public void givenAddressBookContactsInDB_WhenRetrieved_ShouldMatchContactsCount() throws AddressBookException {
		List<AddressBookData> addressBookData = addressBookService.readAddressBookData(IOService.DB_IO);
		Assert.assertEquals(3, addressBookData.size());
	}

	@Test
	public void givenNewCityAndStateForContact_WhenUpdated_ShouldMatch() throws AddressBookException {
		addressBookService.updateContactCityAndState("Abc", "Thane", "Maharashtra");
		boolean result = addressBookService.checkAddressBookInSyncWithDB("Abc");
		Assert.assertTrue(result);
	}

	@Test
	public void givenDateRange_WhenRetrieved_ShouldmatchEmployeeCount() throws AddressBookException {
		addressBookService.readAddressBookData(IOService.DB_IO);
		LocalDate startDate = LocalDate.of(2018, 01, 01);
		LocalDate endDate = LocalDate.now();
		List<AddressBookData> employeePayrollData = addressBookService.readAddressBookForDateRange(IOService.DB_IO,
				startDate, endDate);
		Assert.assertEquals(3, employeePayrollData.size());
	}

	@Test
	public void givenAddressBookDB_WhenRetrievedCountByState_ShouldReturnCountGroupedByState()
			throws AddressBookException {
		count = addressBookService.countContactsByState(IOService.DB_IO, "State");
		Assert.assertEquals(1, count.get("Maharashtra"), 0);
		Assert.assertEquals(1, count.get("Gujrat"), 0);
		Assert.assertEquals(1, count.get("Rajastan"), 0);
	}

	@Test
	public void givenAddressBookDB_WhenRetrievedCountByCity_ShouldReturnCountGroupedByCity()
			throws AddressBookException {
		count = addressBookService.countContactsByCity(IOService.DB_IO, "City");
		Assert.assertEquals(1, count.get("Mumbai"), 0);
		Assert.assertEquals(1, count.get("Surat"), 0);
		Assert.assertEquals(1, count.get("Jaipur"), 0);
	}

	@Test
	public void givenNewEmployee_WhenAdded_ShouldSyncWithDB() throws AddressBookException {
		addressBookService.readAddressBookData(IOService.DB_IO);
		addressBookService.addContactToAddressBook(4, "Lmn", "Cvs", Date.valueOf("2020-01-01"), "Office",
				"MSEB Office", "Thane", "Maharashtra", 400601, "9900400004", "Lmncvs@gmail.com");
		boolean result = addressBookService.checkAddressBookInSyncWithDB("Lmn");
		Assert.assertTrue(result);
	}
	
	@Test
	public void givenMultipleContact_WhenAdded_ShouldSyncWithDB() throws AddressBookException {
		AddressBookData[] contactArray = {
								new AddressBookData(5,"Xml","Htl",Date.valueOf("2020-03-09"),"Home","Kherwadi",
										"Mumbai","Maharahstra",400001,"9900445005", "xmlhtl@gmail.com"),
								new AddressBookData(6,"Asd","Lkj",Date.valueOf("2019-05-1"),"Office","Charai","Thane",
										"Maharashtra",400601,"9988773006","asd@gmail.com"),
								new AddressBookData(7,"Tvb","Nyu",Date.valueOf("2018-04-01"),"Home","Chowpatty","Mumbai",
										"Maharashtra", 400007,"9988773007","Tvb@yahoo.com")
		};
		addressBookService.addMultipleContactsToDBUsingThreads(Arrays.asList(contactArray));
		boolean isSynced1 = addressBookService.checkAddressBookInSyncWithDB("Xml");
		boolean isSynced2 = addressBookService.checkAddressBookInSyncWithDB("Asd");
		boolean isSynced3 = addressBookService.checkAddressBookInSyncWithDB("Tvb");
		Assert.assertTrue(isSynced1);
		Assert.assertTrue(isSynced2);
		Assert.assertTrue(isSynced3);
	}
}