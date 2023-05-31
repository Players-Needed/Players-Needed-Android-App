package ro.pub.acs.playersneeded

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import ro.pub.acs.playersneeded.room.Room
import ro.pub.acs.playersneeded.yourrooms.YourRoomsViewModel

@RunWith(RobolectricTestRunner::class)
class YourRoomsViewModelTest {

    private lateinit var viewModel: YourRoomsViewModel

    @Before
    fun setUp() {
        // Initialize your ViewModel with necessary dependencies
        val tokenArgument = "yourToken"
        val usernameArg = "yourUsername"
        viewModel = YourRoomsViewModel(tokenArgument, usernameArg)
    }

    @Test
    fun testCheckFilters_AllFiltersSet() {
        // Set up the filter values
        viewModel.setSportType("Football")
        viewModel.setDateFrom("2023-05-27")
        viewModel.setDateUntil("2023-05-30")
        viewModel.setTimeFrom("10:00")
        viewModel.setTimeUntil("18:00")
        viewModel.setRoomName("Room")
        viewModel.setSkillLevel("Intermediate")

        // Perform the method under test
        val arguments = viewModel.checkFilters()

        // Verify that the arguments map contains all the expected filters and values
        Assert.assertEquals("Football", arguments["sport_type"])
        Assert.assertEquals("2023-05-27", arguments["date_from"])
        Assert.assertEquals("2023-05-30", arguments["date_until"])
        Assert.assertEquals("10:00", arguments["time_from"])
        Assert.assertEquals("18:00", arguments["time_until"])
        Assert.assertEquals("Room", arguments["name"])
        Assert.assertEquals("Intermediate", arguments["skill_level"])
    }

    @Test
    fun testCheckFilters_NoFiltersSet() {
        // Perform the method under test
        val arguments = viewModel.checkFilters()

        // Verify that the arguments map is empty
        Assert.assertTrue(arguments.isEmpty())
    }

    @Test
    fun testAddRooms() {
        // Create a sample JSON string representing room data
        val json = """
            [
                {
                    "name": "Room 1",
                    "sport_type": "Football",
                    "date": "2023-05-27",
                    "time": "15:00",
                    "location_address": "Some Address",
                    "id": "1"
                },
                {
                    "name": "Room 2",
                    "sport_type": "Basketball",
                    "date": "2023-05-28",
                    "time": "10:00",
                    "location_address": "Another Address",
                    "id": "2"
                }
            ]
        """.trimIndent()

        // Perform the method under test
        viewModel.addRooms(json)

        // Verify that the roomList has been populated with the expected rooms
        val expectedRoomList = arrayOf(
            Room("Room 1", "Football", "2023-05-27", "15:00", "", "Some Address", "", "", 1, 1),
            Room("Room 2", "Basketball", "2023-05-28", "10:00", "", "Another Address", "", "", 1, 2)
        )
        Assert.assertArrayEquals(expectedRoomList, viewModel.roomList)
    }

    @Test
    fun testRefreshFilters() {
        // Set up some filter values
        viewModel.setSportType("Football")
        viewModel.setDateFrom("2023-05-27")

        // Perform the method under test
        viewModel.refreshFilters()

        // Verify that all filter values have been reset to null
        Assert.assertNull(viewModel.sportTypeFilter.value)
        Assert.assertNull(viewModel.dateFromFilter.value)
        // Verify the reset of other filter values as needed
    }

// Add more test cases for other
}

