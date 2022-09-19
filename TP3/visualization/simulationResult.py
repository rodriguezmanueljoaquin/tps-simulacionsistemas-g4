import math
from turtle import width

class SimulationResult:

    def __init__(self,N,width,height,gap,v,mass):
        self.N = N
        self.width = width
        self.height = height
        self.gap = gap
        self.v = v
        self.fpDict = dict()
        self.particlesDict = dict() 
        self.balanceTime = None
        self.mass = mass

    def __str__(self):
        return "{N="+str(self.N)+";width="+str(self.width)+";height="+str(self.height)+";gap="+str(self.gap)+";v="+str(self.v)+";mass="+str(self.mass)+";fpDict="+str(self.fpDict)+";particlesDict="+str(self.particlesDict)+";balanceTime="+str(self.balanceTime)+"}"

    def __repr__(self):
        return self.__str__()

    def setBalanceTime(self,time):
        self.balanceTime = time

    def setPressure(self,totalImpulse,finalTime):
        # print(f"total impulse : {totalImpulse}")
        # print(f"finalTime : {finalTime}")
        # print(f"balanceTime : {self.balanceTime}")
        self.pressure = totalImpulse/((finalTime-self.balanceTime)*self.getPerimeter())

    def getTemperature(self):
        return (self.mass*(self.v**2))/2

    def getPerimeter(self):
        return 2*self.width+2*self.height+2*(self.height-self.gap)

    
    