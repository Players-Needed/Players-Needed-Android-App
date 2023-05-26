import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import ro.pub.acs.playersneeded.news.News
import ro.pub.acs.playersneeded.user.UserHomeScreenViewModel

@RunWith(RobolectricTestRunner::class)
class UserHomeScreenViewModelTest {

    @Test
    fun testAddNews() {
        // Create a sample JSON string for news
        val jsonString = """
            [
                {
                    "title": "News 1",
                    "url": "https://example.com/news1",
                    "urlToImage": "https://example.com/image1.jpg"
                },
                {
                    "title": "News 2",
                    "url": "https://example.com/news2"
                },
                {
                    "title": "News 3",
                    "url": "https://example.com/news3",
                    "urlToImage": "https://example.com/image3.jpg"
                }
            ]
        """.trimIndent()

        // Create an instance of UserHomeScreenViewModel
        val viewModel = UserHomeScreenViewModel("mock_token")

        // Call the addNews method
        viewModel.addNews(jsonString)

        // Verify the news list
        val expectedNewsList = arrayOf(
            News("News 1", "https://example.com/news1", "https://example.com/image1.jpg"),
            News("News 2", "https://example.com/news2", "image"),
            News("News 3", "https://example.com/news3", "https://example.com/image3.jpg")
        )
        assertEquals(expectedNewsList.size, viewModel.newsList.size)
        for (i in expectedNewsList.indices) {
            assertEquals(expectedNewsList[i].title, viewModel.newsList[i].title)
            assertEquals(expectedNewsList[i].url, viewModel.newsList[i].url)
            assertEquals(expectedNewsList[i].urlToImage, viewModel.newsList[i].urlToImage)
        }
    }
}
