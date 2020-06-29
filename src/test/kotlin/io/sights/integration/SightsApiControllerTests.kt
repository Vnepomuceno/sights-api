package io.sights.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.whenever
import io.sights.controller.ManageSightPayload
import io.sights.controller.SightsApiController
import io.sights.models.Author
import io.sights.models.Contact
import io.sights.models.Sight
import io.sights.repository.SightsRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.util.ResourceUtils
import java.io.FileReader

@ExtendWith(SpringExtension::class)
@WebMvcTest(controllers = [SightsApiController::class])
class SightsApiControllerTests {

    @MockBean
    private lateinit var sightsRepository: SightsRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var sights: List<Sight>

    @BeforeEach
    fun setup() {
        sights = objectMapper.readValue(FileReader(ResourceUtils.getFile("classpath:Sight.json")))
    }

    @Test
    fun `GET sights - When invoked it returns a list of sights and responds with HTTP 200 - OK`() {
        whenever(sightsRepository.findAll()).thenReturn(sights)

        mockMvc.perform(get("/sights"))
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(sights)))
    }

    @Test
    fun `POST sights - When invoked with a valid payload, it returns the ID of the created sight and responds with HTTP 201 - Created`() {
        val manageSightPayload = ManageSightPayload(
            labels = listOf(),
            descriptions = listOf(),
            locations = listOf(),
            websites = listOf(),
            contact = Contact(phoneNumber = null, emailAddress = null),
            schedules = listOf(),
            images = listOf(),
            author = Author(id = "", source = "")
        )

        val sight = Sight(
            id = "sightId",
            citySdkId = "citySdkId",
            base = "baseUri",
            labels = listOf(),
            descriptions = listOf(),
            locations = listOf(),
            websites = listOf(),
            contact = null,
            schedules = listOf(),
            images = listOf(),
            author = Author(id = "authorId", source = "authorSource")
        )

        whenever(
            sightsRepository.save(
                argThat<Sight> {
                    this.labels == manageSightPayload.labels &&
                        this.descriptions == manageSightPayload.descriptions &&
                        this.locations == manageSightPayload.locations &&
                        this.websites == manageSightPayload.websites &&
                        this.contact == manageSightPayload.contact &&
                        this.schedules == manageSightPayload.schedules &&
                        this.images == manageSightPayload.images &&
                        this.author == manageSightPayload.author
                }
            )
        ).thenReturn(sight)

        mockMvc.perform(
            post("/sights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(manageSightPayload))
        )
            .andExpect(status().isCreated)
            .andExpect(content().string(sight.id))
    }

    @Test
    fun `POST sights - When invoked with an invalid payload, it does not save a new sight in the repository and responds with HTTP 400 - Bad Request`() {
        val invalidPayload = """
            {
                "descriptions":[],
                "locations":[],
                "websites":[],
                "contact": {
                    "phoneNumber":null,
                    "emailAddress":null
                },
                "schedules":[],
                "images":[],
                "author": {
                    "id":"",
                    "source":""
                }
            }
        """.trimIndent()

        whenever(sightsRepository.save(any<Sight>())).thenReturn(
            Sight(
                id = "sightId",
                citySdkId = "citySdkId",
                base = "baseUri",
                labels = listOf(),
                descriptions = listOf(),
                locations = listOf(),
                websites = listOf(),
                contact = null,
                schedules = listOf(),
                images = listOf(),
                author = Author(id = "authorId", source = "authorSource")
            )
        )

        mockMvc.perform(
            post("/sights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPayload)
        )
            .andExpect(status().isBadRequest)
    }
}
