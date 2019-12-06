package io.github.kmakma.adventofcode.y2019

class OrbitalMap {
    private val spaceObjects: MutableMap<String, SpaceObject> = mutableMapOf()

    companion object {
        fun parse(orbits: List<String>): OrbitalMap {
            val orbitalMap = OrbitalMap()
            for (orbit in orbits) {
                val spaceObjects = orbit.split(")")
                orbitalMap.addOrbit(spaceObjects[0], spaceObjects[1])
            }
            return orbitalMap
        }
    }

    fun totalNumberOfOrbits(): Int {
        return spaceObjects.values.map { it.numberOfOrbits() }.sum()
    }

    private fun addOrbit(centerName: String, satelliteName: String) {
        val center = getSpaceObject(centerName)
        val satellite = getSpaceObject(satelliteName)
        center.addSatellite(satellite)
        satellite.setCenter(center)
    }

    private fun getSpaceObject(soName: String): SpaceObject {
        return spaceObjects.getOrPut(soName) { SpaceObject(soName) }
    }

    fun travelDistance(start: String, end: String): Int {
        val comToStart = getSpaceObject(start).pathFromCOM()
        val comToEnd = getSpaceObject(end).pathFromCOM()
        val intersection = comToStart.intersect(comToEnd)
        return (comToStart.size + comToEnd.size) - 2 * intersection.size
    }
}

class SpaceObject(private val name: String) {
    private lateinit var center: SpaceObject
    private val satellites: MutableSet<SpaceObject> = mutableSetOf()

    fun addSatellite(satellite: SpaceObject) {
        satellites.add(satellite)
    }

    fun setCenter(spaceObject: SpaceObject) {
        center = spaceObject
    }

    fun numberOfOrbits(): Int {
        return if (::center.isInitialized) {
            center.numberOfOrbits() + 1
        } else {
            0
        }
    }

    fun pathFromCOM(): MutableList<String> {
        return if (::center.isInitialized) {
            center.pathFromCOM().apply { this.add(name) }
        } else {
            mutableListOf(name)
        }
    }
}