from enum import Enum

class PlanetType(Enum):
    SUN = -1
    EARTH = 0
    VENUS = 1
    SPACESHIP = 2


planetType_dict = {"sun":PlanetType.SUN, "earth":PlanetType.EARTH, "venus":PlanetType.VENUS, "spaceship":PlanetType.SPACESHIP}
    