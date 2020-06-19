package linkedin.linkedintest.test;

import org.testng.annotations.Test;

import linkedin.linkedintest.page.Page;

public class LinkedInTest extends Page{
	
	@Test
	public void LinkedIn() throws Exception{
		readCSV();
		login("",""); // enter your linkedin username and password
		navigateToConnections();
		sendMessageToConnections(10,""); // Number of users you want to send message(ex:10), and the message
	}
}
