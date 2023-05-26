package ro.pub.acs.playersneeded.room

import kotlin.properties.Delegates

class Room(var name: String, var sport: String, var date: String, var time: String,
           var location: String, var address: String, var extraDetails: String,
           var skillLevel: String, var noPlayers:Int, var id:Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        val otherRoom = other as Room

        return name == otherRoom.name &&
                sport == otherRoom.sport &&
                date == otherRoom.date &&
                time == otherRoom.time &&
                location == otherRoom.location &&
                address == otherRoom.address &&
                extraDetails == otherRoom.extraDetails &&
                skillLevel == otherRoom.skillLevel &&
                noPlayers == otherRoom.noPlayers &&
                id == otherRoom.id
    }

    override fun hashCode(): Int {
        // Implement hashCode() if needed
        return super.hashCode()
    }
}