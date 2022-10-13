from enum import Enum

class PlanetIndexInDynamic(Enum):
    SUN = -1
    EARTH = 0
    VENUS = 1
    SPACESHIP = 2


planet_index_dict = {"sun":PlanetIndexInDynamic.SUN, "earth":PlanetIndexInDynamic.EARTH, "venus":PlanetIndexInDynamic.VENUS, "spaceship":PlanetIndexInDynamic.SPACESHIP}
    