from cmath import pi
import random

class Particle:    
    
    def __init__(self,id,x,y,velx,vely):
        self.id = id
        self.x = x
        self.y = y
        self.velx = velx
        self.vely = vely
        random.seed(self.id)
        self.color_id = (random.random()*2*pi) - pi

    def __eq__(self, other):
        return self.id == other.id

    def __str__(self):
        return "{Id="+str(self.id)+";position=("+str(self.x)+";"+str(self.y)+");velocity=("+str(self.velx)+";"+str(self.vely)+") }"

    def __repr__(self):
        return self.__str__()
    
    def __hash__(self):
        return self.id

    def getPosition(self):
        return (self.x,self.y)

    def getVelocity(self):
        return (self.velx,self.vely)

