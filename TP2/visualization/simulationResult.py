import math

class SimulationResult:

    def __init__(self,eta,N,v,L):
        self.eta = eta
        self.N = N
        self.v = v
        self.L = L
        self.vaDict = dict()
        self.particlesDict = dict() 

    def __str__(self):
        return "{eta="+str(self.eta)+";N="+str(self.N)+";v="+str(self.v)+"L="+str(self.L)+";vaDict="+str(self.vaDict)+";particlesDict="+str(self.particlesDict)+"}"

    def __repr__(self):
        return self.__str__()

    def getDensity(self):
        return self.N/math.pow(self.L,2)

    
    