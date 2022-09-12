import math

class SimulationResult:

    def __init__(self,N,width,height,gap,v):
        self.N = N
        self.width = width
        self.height = height
        self.gap = gap
        self.v = v
        self.fpDict = dict()
        self.particlesDict = dict() 
        self.balanceTime = None

    def __str__(self):
        return "{N="+str(self.N)+";width="+str(self.width)+";height="+str(self.height)+";gap="+str(self.gap)+";v="+str(self.v)+";fpDict="+str(self.fpDict)+";particlesDict="+str(self.particlesDict)+";balanceTime="+str(self.balanceTime)+"}"

    def __repr__(self):
        return self.__str__()

    def setBalanceTime(self,time):
        self.balanceTime = time

    
    