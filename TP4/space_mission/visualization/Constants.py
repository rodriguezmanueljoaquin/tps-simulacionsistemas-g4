from enum import Enum

class PlanetIndexInDynamic(Enum):
    SUN = -1
    EARTH = 0
    VENUS = 1
    MARS = 2
    SPACESHIP = 3


planet_index_dict = {
    "sun":PlanetIndexInDynamic.SUN, 
    "earth":PlanetIndexInDynamic.EARTH, 
    "venus":PlanetIndexInDynamic.VENUS, 
    "mars":PlanetIndexInDynamic.MARS,
    "spaceship":PlanetIndexInDynamic.SPACESHIP
}
    