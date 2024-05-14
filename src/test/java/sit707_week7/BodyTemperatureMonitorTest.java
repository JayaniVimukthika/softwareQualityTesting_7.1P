package sit707_week7;

import org.junit.Assert;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BodyTemperatureMonitorTest {

	@Mock
    private TemperatureSensor temperatureSensor;

    @Mock
    private CloudService cloudService;

    @Mock
    private NotificationSender notificationSender;

    private BodyTemperatureMonitor bodyTemperatureMonitor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        bodyTemperatureMonitor = new BodyTemperatureMonitor(temperatureSensor, cloudService, notificationSender);
    }

	@Test
	public void testStudentIdentity() {
		String studentId = "s220194805";
		Assert.assertNotNull("Student ID is null", studentId);
	}

	@Test
	public void testStudentName() {
		String studentName = "Jayani";
		Assert.assertNotNull("Student name is null", studentName);
	}
	
	//return negative temperature reading
	@Test
	public void testReadTemperatureNegative() {
		when(temperatureSensor.readTemperatureValue()).thenReturn(-1.0);

        double temperature = bodyTemperatureMonitor.readTemperature();

        Assert.assertEquals(-1.0, temperature, 0.01);
        verify(temperatureSensor, times(1)).readTemperatureValue();
	}
	
	
	//return 0 temperature reading
	@Test
	public void testReadTemperatureZero() {
		when(temperatureSensor.readTemperatureValue()).thenReturn(0.0);

        double temperature = bodyTemperatureMonitor.readTemperature();

        Assert.assertEquals(0.0, temperature, 0.01);
        verify(temperatureSensor, times(1)).readTemperatureValue();
	}
	
	//value between 35-37
	@Test
	public void testReadTemperatureNormal() {
		when(temperatureSensor.readTemperatureValue()).thenReturn(36.0);

        double temperature = bodyTemperatureMonitor.readTemperature();

        Assert.assertEquals(36.0, temperature, 0.01);
        verify(temperatureSensor, times(1)).readTemperatureValue();
	}

	//return abnormally high value
	@Test
	public void testReadTemperatureAbnormallyHigh() {
		when(temperatureSensor.readTemperatureValue()).thenReturn(48.0);

        double temperature = bodyTemperatureMonitor.readTemperature();

        Assert.assertEquals(48.0, temperature, 0.01);
        verify(temperatureSensor, times(1)).readTemperatureValue();
	}

	/*
	 * CREDIT or above level students, Remove comments. 
	 */
	@Test
	public void testReportTemperatureReadingToCloud() {
		TemperatureReading temperatureReading = new TemperatureReading();
        doNothing().when(cloudService).sendTemperatureToCloud(any(TemperatureReading.class));

        bodyTemperatureMonitor.reportTemperatureReadingToCloud(temperatureReading);

        verify(cloudService, times(1)).sendTemperatureToCloud(temperatureReading);
	}
	
	
	/*
	 * CREDIT or above level students, Remove comments. 
	 */
	@Test
	public void testInquireBodyStatusNormalNotification() {
		when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("NORMAL");

        bodyTemperatureMonitor.inquireBodyStatus();

        verify(notificationSender, times(1)).sendEmailNotification(eq(bodyTemperatureMonitor.getFixedCustomer()), eq("Thumbs Up!"));
	}
	
	/*
	 * CREDIT or above level students, Remove comments. 
	 */
	@Test
	public void testInquireBodyStatusAbnormalNotification() {
		when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("ABNORMAL");

        bodyTemperatureMonitor.inquireBodyStatus();

        verify(notificationSender, times(1)).sendEmailNotification(eq(bodyTemperatureMonitor.getFamilyDoctor()), eq("Emergency!"));
	}
}
