import android.os.Build
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ro.pub.acs.playersneeded.player.Player
import ro.pub.acs.playersneeded.roomscreen.RoomViewModel

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class RoomViewModelTest {

    @Mock
    private lateinit var roomViewModel: RoomViewModel

    @Before
    fun setup() {
        val mockToken = "mock_token"
        val mockUsername = "mock_username"
        val mockId = 1

        roomViewModel = RoomViewModel(mockToken, mockId, mockUsername)
    }

    @Test
    fun testCheckPlayerId() {
        // Create a sample player list
        val playerList = arrayOf(
            Player(1, "john@example.com", "John", 3, 2),
            Player(2, "alice@example.com", "Alice", 5, 4),
            Player(3, "bob@example.com", "Bob", 2, 1)
        )

        // Set the player list in the view model
        roomViewModel.playersList = playerList

        // Test the checkPlayerId method
        assertTrue(roomViewModel.checkPlayerId(1)) // Existing player ID
        assertFalse(roomViewModel.checkPlayerId(4)) // Non-existing player ID
    }

    @Test
    fun testSetDetails() {
        // Sample JSON response
        val jsonString = """
            {
                "name": "Room 1",
                "sport_type": "Football",
                "skill_level": "Intermediate",
                "max_no_players": "10",
                "date": "2023-05-26",
                "time": "18:00",
                "extra_details": "Bring your own equipment",
                "location_address": "123 Main St",
                "location_lat": 37.1234,
                "location_lon": -122.5678,
                "room_administrator": {
                    "id": "1"
                }
            }
        """.trimIndent()

        // Set the details in the view model
        roomViewModel.setDetails(jsonString)

        // Test the values set in the view model
        assertEquals("Room 1", roomViewModel.roomName.value)
        assertEquals("Football", roomViewModel.sportType.value)
        assertEquals("Intermediate", roomViewModel.skillLevel.value)
        assertEquals("10", roomViewModel.noPlayers.value)
        assertEquals("2023-05-26", roomViewModel.date.value)
        assertEquals("18:00", roomViewModel.time.value)
        assertEquals("Bring your own equipment", roomViewModel.extraDetails.value)
        assertEquals("123 Main St", roomViewModel.address.value)
        roomViewModel.lat.value?.let { assertEquals(37.1234, it, 0.0001) }
        roomViewModel.lon.value?.let { assertEquals(-122.5678, it, 0.0001) }
        assertEquals(1, roomViewModel.idPlayerAdmin.value)
    }
}
