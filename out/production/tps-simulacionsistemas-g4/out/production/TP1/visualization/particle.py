import itertools

class Particle:
    id_iter = itertools.count(start=1,step=1)
    
    
    def __init__(self,x,y):
        self.x = x
        self.y = y
        self.id = next(self.id_iter)

    def __eq__(self, other):
        return self.id == other.id

    def __str__(self):
        return "{Id="+str(self.id)+";position=("+str(self.x)+";"+str(self.y)+")}"

    def __repr__(self):
        return self.__str__()
    
    def __hash__(self):
        return self.id

    def setRadius(self,radius):
        self.radius = radius

    def setProperty(self,property):
        self.property = property

