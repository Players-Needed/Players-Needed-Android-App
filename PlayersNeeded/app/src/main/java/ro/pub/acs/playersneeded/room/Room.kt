package ro.pub.acs.playersneeded.room

import kotlin.properties.Delegates

class Room(var name: String, var sport: String, var date: String, var time: String,
           var location: String, var address: String, var extraDetails: String,
           var skillLevel: String, var noPlayers:Int, var id:Int) {
}