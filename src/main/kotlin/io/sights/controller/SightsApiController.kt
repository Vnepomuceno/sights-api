package io.sights.controller

import io.sights.models.Author
import io.sights.models.Contact
import io.sights.models.Description
import io.sights.models.Image
import io.sights.models.Label
import io.sights.models.Location
import io.sights.models.Schedule
import io.sights.models.Sight
import io.sights.models.Website
import io.sights.repository.SightsRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/sights")
class SightsApiController(private val sightsRepository: SightsRepository) {

    @GetMapping
    fun getAllSights(): List<Sight> {
        return sightsRepository.findAll()
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createSight(@RequestBody manageSightPayload: ManageSightPayload): String {
        return sightsRepository.save(manageSightPayload.toSight()).id
    }
}

fun ManageSightPayload.toSight() =
    Sight(
        base = "sights-api",
        labels = this.labels,
        descriptions = this.descriptions,
        locations = this.locations,
        websites = this.websites,
        contact = this.contact,
        schedules = this.schedules,
        images = this.images,
        author = this.author
    )

data class ManageSightPayload(
    val labels: List<Label>,
    val descriptions: List<Description>?,
    val locations: List<Location>,
    val websites: List<Website>,
    val contact: Contact,
    val schedules: List<Schedule>,
    val images: List<Image>,
    val author: Author
)
