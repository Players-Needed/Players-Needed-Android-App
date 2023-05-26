package ro.pub.acs.playersneeded

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ro.pub.acs.playersneeded.joinroom.JoinRoomViewModel
import ro.pub.acs.playersneeded.room.Room

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class JoinRoomViewModelTest {

    private lateinit var viewModel: JoinRoomViewModel

    @Before
    fun setup() {
        val mockToken = "mock_token"
        val mockUsername = "mock_username"

        viewModel = JoinRoomViewModel(mockToken, mockUsername)
    }

    @Test
    fun testCheckFilters() {
        // Set filter values
        viewModel.setSportType("Football")
        viewModel.setSkillLevel("Advanced")
        viewModel.setRoomName("Room 1")
        viewModel.setDateFrom("2023-05-27")
        viewModel.setDateUntil("2023-05-28")
        viewModel.setTimeFrom("09:00")
        viewModel.setTimeUntil("10:00")

        // Call the method to be tested
        val filters = viewModel.checkFilters()

        // Verify that the filters are set correctly
        assertEquals("Football", filters["sport_type"])
        assertEquals("Advanced", filters["skill_level"])
        assertEquals("Room 1", filters["name"])
        assertEquals("2023-05-27", filters["date_from"])
        assertEquals("2023-05-28", filters["date_until"])
        assertEquals("09:00", filters["time_from"])
        assertEquals("10:00", filters["time_until"])
    }

    @Test
    fun testAddRooms() {
        val mockJsonResponse = """
            [
                {
                    "name": "Room 1",
                    "sport_type": "Sport 1",
                    "date": "2023-05-27",
                    "time": "09:00",
                    "location_address": "Address 1",
                    "id": "1"
                },
                {
                    "name": "Room 2",
                    "sport_type": "Sport 2",
                    "date": "2023-05-28",
                    "time": "10:00",
                    "location_address": "Address 2",
                    "id": "2"
                }
            ]
        """.trimIndent()

        // Call the method to be tested
        viewModel.addRooms(mockJsonResponse)

        // Verify that the room list is populated correctly
        val expectedRoomList = arrayOf(
            Room("Room 1", "Sport 1", "2023-05-27", "09:00", "", "Address 1", "", "", 1, 1),
            Room("Room 2", "Sport 2", "2023-05-28", "10:00", "", "Address 2", "", "", 1, 2)
        )
        assertArrayEquals(expectedRoomList, viewModel.roomList)
    }

    @Test
    fun testRefreshFilters() {
        // Set some filter values
        viewModel.setSportType("Football")
        viewModel.setSkillLevel("Advanced")
        viewModel.setRoomName("Room 1")

        // Call the method to be tested
        viewModel.refreshFilters()

        // Verify that the filters are reset
        assertNull(viewModel.sportTypeFilter.value)
        assertNull(viewModel.skillLevelFilter.value)
        assertNull(viewModel.roomNameFilter.value)
    }
}
