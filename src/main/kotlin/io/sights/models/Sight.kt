package io.sights.models

import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document(collection = "sights")
@CompoundIndexes(
    CompoundIndex(name = "sight_idx", def = "{'labels': 1, 'locations': 1}", unique = true)
)
data class Sight(
    val id: String = uuid(),
    val citySdkId: String? = null,
    val base: String,
    val labels: List<Label>,
    val descriptions: List<Description>?,
    val locations: List<Location>?,
    val websites: List<Website>?,
    val contact: Contact?,
    val schedules: List<Schedule>?,
    val images: List<Image>?,
    val author: Author
)

data class Label(
    val id: String = uuid(),
    val term: String,
    val value: String,
    val language: String?
)

data class Description(
    val id: String = uuid(),
    val value: String,
    val lang: String
)

data class Location(
    val id: String = uuid(),
    val address: String,
    val city: String,
    val country: String,
    val coordinates: String
)

data class Website(
    val id: String = uuid(),
    val href: String,
    val type: String
)

data class Contact(
    val phoneNumber: String?,
    val emailAddress: String?
)

data class Schedule(
    val id: String = uuid(),
    val open: String
)

data class Image(
    val id: String = uuid(),
    val href: String,
    val type: String
)

data class Author(
    val id: String = uuid(),
    val source: String
)

private fun uuid() = UUID.randomUUID().toString().replace("-", "")
