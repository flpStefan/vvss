package tasks.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tasks.model.ArrayTaskList;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskServiceTest {

    private DateService dateService;
    private TasksService tasksService;
    private ArrayTaskList taskList;

    @BeforeEach
    void setUp() {
        taskList = new ArrayTaskList();
        tasksService = new TasksService(taskList);
        dateService = new DateService(tasksService);
    }

    @AfterEach
    void tearDown() {
        dateService = null;
        tasksService = null;
        taskList = null;
    }

    private static Stream<Arguments> provideValidParams() {
        return Stream.of(
                Arguments.of("9:00",
                        new GregorianCalendar(2025, Calendar.MARCH, 30).getTime(),
                        new GregorianCalendar(2025, Calendar.MARCH, 30, 9, 0).getTime()),
                Arguments.of("0:0",
                        new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                        new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 0).getTime()),
                Arguments.of("1:1",
                        new GregorianCalendar(1970, Calendar.JANUARY, 2).getTime(),
                        new GregorianCalendar(1970, Calendar.JANUARY, 2, 1, 1).getTime())
        );
    }

    @Order(1)
    @ParameterizedTest
    @MethodSource("provideValidParams")
    @DisplayName("getDateMergedWithTime with valid parameters")
    public void getDateMergedWithTimeValid(String time, Date noTimeDate, Date expectedDate) {
        Date date = dateService.getDateMergedWithTime(time, noTimeDate);
        assertEquals(expectedDate, date);
    }

    @Order(2)
    @Test
    @DisplayName("getDateMergedWithTime with invalid hour")
    public void getDateMergedWithTimeInvalidHour() {
        String time = "24:00";
        Date noTimeDate = new GregorianCalendar(2025, Calendar.MARCH, 30).getTime();
        assertThrows(IllegalArgumentException.class, () -> dateService.getDateMergedWithTime(time, noTimeDate));
    }

    @Order(3)
    @Test
    @DisplayName("getDateMergedWithTime with invalid minute")
    public void getDateMergedWithTimeInvalidMinute() {
        String time = "23:60";
        Date noTimeDate = new GregorianCalendar(2025, Calendar.MARCH, 30).getTime();
        assertThrows(IllegalArgumentException.class, () -> dateService.getDateMergedWithTime(time, noTimeDate));
    }

    @Order(4)
    @Test
    @DisplayName("getDateMergedWithTime with invalid time format")
    public void getDateMergedWithTimeInvalidFormat() {
        String time = "invalid";
        Date noTimeDate = new GregorianCalendar(2025, Calendar.MARCH, 30).getTime();
        assertThrows(IllegalArgumentException.class, () -> dateService.getDateMergedWithTime(time, noTimeDate));
    }

    @Order(5)
    @Test
    @DisplayName("getDateMergedWithTime with negative hour")
    public void getDateMergedWithTimeNegativeHour() {
        String time = "-1:00";
        Date noTimeDate = new GregorianCalendar(2025, Calendar.MARCH, 30).getTime();
        assertThrows(IllegalArgumentException.class, () -> dateService.getDateMergedWithTime(time, noTimeDate));
    }

    @Order(6)
    @Test
    @DisplayName("getDateMergedWithTime with negative minute")
    public void getDateMergedWithTimeNegativeMinute() {
        String time = "10:-1";
        Date noTimeDate = new GregorianCalendar(2025, Calendar.MARCH, 30).getTime();
        assertThrows(IllegalArgumentException.class, () -> dateService.getDateMergedWithTime(time, noTimeDate));
    }

    @Order(7)
    @Test
    @DisplayName("Add Task to ArrayTaskList")
    public void addValidTask() {
        Date taskTime = new GregorianCalendar(2025, Calendar.MARCH, 30, 10, 0).getTime();
        tasks.model.Task task = new tasks.model.Task("Test Task", taskTime);

        assertEquals(0, taskList.size());

        taskList.add(task);

        assertEquals(1, taskList.size());

        assertEquals(task, taskList.getTask(0));

        Date taskTime2 = new GregorianCalendar(2025, Calendar.MARCH, 31, 11, 0).getTime();
        tasks.model.Task task2 = new tasks.model.Task("Test Task 2", taskTime2);
        taskList.add(task2);

        assertEquals(2, taskList.size());

        assertEquals(task, taskList.getTask(0));
        assertEquals(task2, taskList.getTask(1));
    }

    @Order(8)
    @Test
    @DisplayName("Add invalid (null) Task to ArrayTaskList")
    public void addInvalidTask() {
        assertEquals(0, taskList.size());

        assertThrows(NullPointerException.class, () -> taskList.add(null));

        assertEquals(0, taskList.size());
    }

    @Order(9)
    @Test
    @DisplayName("Add Task - BVA Valid Case")
    public void addTaskBVAValid() {
        Date taskTime = new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 0).getTime(); // Earliest valid time
        tasks.model.Task task = new tasks.model.Task("Valid Title", taskTime);
        task.setActive(true);
        task.setTime(taskTime, new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 1).getTime(), 1); // Smallest valid interval
        taskList.add(task);
        assertEquals(1, taskList.size());
        assertEquals(task, taskList.getTask(0));
        assertTrue(taskList.getTask(0).isActive());
        assertEquals(1, taskList.getTask(0).getRepeatInterval());
    }

    @Order(10)
    @Test
    @DisplayName("Add Task - BVA Invalid Case")
    public void addTaskBVAInvalid() {
        assertThrows(NullPointerException.class, () -> taskList.add(null));

        Date startTime = new GregorianCalendar(2025, Calendar.MARCH, 30, 10, 0).getTime();
        Date endTime = new GregorianCalendar(2025, Calendar.MARCH, 30, 11, 0).getTime();

        assertThrows(IllegalArgumentException.class, () ->
                new tasks.model.Task("Invalid Task", startTime, endTime, 0));

        assertThrows(IllegalArgumentException.class, () ->
                new tasks.model.Task("Invalid Task", new Date(-1)));
    }
}