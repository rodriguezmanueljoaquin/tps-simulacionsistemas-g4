from cmath import pi
import random

from constants import WALL_VERTICAL, BOTTOM_GAP, TOP_GAP, WALL_HORIZONTAL


class Particle:    
    
    def __init__(self,id,x,y,velx,vely,lastCollisionType):
        self.id = id
        self.x = x
        self.y = y
        self.velx = velx
        self.vely = vely
        random.seed(self.id)
        self.color_id = (random.random()*2*pi) - pi,
        self.lastCollisionType = lastCollisionType

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

    def getImpulse(self,mass):
        # print(f"Particle last collision type : {self.lastCollisionType}")
        #Si el choque no fue contra alguna de las paredes o los tabiques de la abertura, el impulso es 0
        if(self.lastCollisionType == 0 or self.lastCollisionType == 5):
            return 0
        #Sino, retornamos el impulso correspondiente
        if(self.lastCollisionType==2):
            impulseVelocity = self.vely
        else:
            impulseVelocity = self.velx

        return 2*mass*abs(impulseVelocity)
        


